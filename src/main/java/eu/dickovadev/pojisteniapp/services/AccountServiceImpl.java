package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.ChangePasswordDTO;
import eu.dickovadev.pojisteniapp.models.exceptions.AccessDeniedException;
import eu.dickovadev.pojisteniapp.models.exceptions.InvalidPasswordException;
import eu.dickovadev.pojisteniapp.repositories.UserRepository;
import eu.dickovadev.pojisteniapp.models.dto.RegisterDTO;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import eu.dickovadev.pojisteniapp.models.exceptions.DuplicateEmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final UserService userService;

    @Autowired
    public AccountServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuditLogService auditLogService,
            UserService userService
    ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void create(RegisterDTO user){
        log.info("Attempting to register user: {}", user.getEmail());

        UserEntity userEntity = new UserEntity();

        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.addRole(Role.ROLE_REGISTERED);

        try {
            userRepository.save(userEntity);
            log.info("User registered successfully: {}", user.getEmail());

            auditLogService.logAction(
                    "INSERT",
                    "UserEntity",
                    userEntity.getUserId(),
                    "New user registered with email: " + user.getEmail()
            );

        } catch (DataIntegrityViolationException ex) {
            log.warn("User registration failed due to duplicate email: {}", user.getEmail());
            throw new DuplicateEmailException();
        } catch (Exception ex) {
            log.error("Unexpected error during user registration", ex);
            throw ex;
        }
    }

    @Transactional
    @Override
    public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
        log.info("Password change requested for user ID: {}", userId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity authenticatedUser = (UserEntity) authentication.getPrincipal();
        if (authenticatedUser == null || authenticatedUser.getUserId() != userId) {
            log.warn("Access denied: user {} attempted to change password of user {}", authenticatedUser != null ? authenticatedUser.getUserId() : "null", userId);
            throw new AccessDeniedException();
        }

        // Retrieve user entity from database
        UserEntity userEntity = userService.getEntityByIdOrThrow(userId);

        // Validate current password matches stored password
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), userEntity.getPassword())) {
            log.warn("Password validation failed for user: {}", userEntity.getEmail());
            throw new InvalidPasswordException();
        }

        // Encode and set the new password
        userEntity.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));

        // Save the updated user entity
        userRepository.save(userEntity);
        log.info("Password successfully updated for user: {}", userEntity.getEmail());

        try {
            auditLogService.logAction(
                    "UPDATE",
                    "UserEntity",
                    userEntity.getUserId(),
                    "Password updated for user with email: " + userEntity.getEmail()
            );
        } catch (Exception ex) {
            log.warn("Audit logging failed during password change for user: {}", userEntity.getEmail(), ex);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Looking up user by username: {}", username);
        return userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", username);
                    return new UsernameNotFoundException("Username, " + username + " not found");
                });
    }
}
