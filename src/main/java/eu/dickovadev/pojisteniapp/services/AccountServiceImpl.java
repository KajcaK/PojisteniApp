package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.exceptions.AccessDeniedException;
import eu.dickovadev.pojisteniapp.models.exceptions.InvalidPasswordException;
import eu.dickovadev.pojisteniapp.repositories.UserRepository;
import eu.dickovadev.pojisteniapp.models.dto.AccountDTO;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import eu.dickovadev.pojisteniapp.models.exceptions.DuplicateEmailException;
import eu.dickovadev.pojisteniapp.models.exceptions.PasswordsDoNotEqualException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

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
    public void create(AccountDTO user){

        validatePasswordMatch(user.getPassword(), user.getConfirmPassword());

        UserEntity userEntity = new UserEntity();

        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        userEntity.addRole(Role.ROLE_REGISTERED);

        try {
            userRepository.save(userEntity);

            auditLogService.logAction(
                    "INSERT",
                    "UserEntity",
                    userEntity.getUserId(),
                    "New user registered with email: " + user.getEmail()
            );

        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateEmailException();
        }
    }

    @Transactional
    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity authenticatedUser = (UserEntity) authentication.getPrincipal();
        if (authenticatedUser == null || authenticatedUser.getUserId() != userId) {
            throw new AccessDeniedException();
        }

        // Validate new passwords match
        validatePasswordMatch(newPassword, confirmPassword);

        // Retrieve user entity from database
        UserEntity userEntity = userService.getEntityByIdOrThrow(userId);

        // Validate current password matches stored password
        if (!passwordEncoder.matches(currentPassword, userEntity.getPassword())) {
            throw new InvalidPasswordException();
        }

        // Encode and set the new password
        userEntity.setPassword(passwordEncoder.encode(newPassword));

        // Save the updated user entity
        userRepository.save(userEntity);

        // Log the action
        auditLogService.logAction(
                "UPDATE",
                "UserEntity",
                userEntity.getUserId(),
                "Password updated for user with email: " + userEntity.getEmail()
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username, " + username + " not found"));
    }

    private void validatePasswordMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new PasswordsDoNotEqualException();
        }
    }
}
