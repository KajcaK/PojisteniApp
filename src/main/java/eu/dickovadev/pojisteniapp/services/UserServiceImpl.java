package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.entities.PolicyEntity;
import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.exceptions.AccessDeniedException;
import eu.dickovadev.pojisteniapp.repositories.PolicyRepository;
import eu.dickovadev.pojisteniapp.repositories.UserRepository;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import eu.dickovadev.pojisteniapp.models.exceptions.EmailAlreadyExistsException;
import eu.dickovadev.pojisteniapp.models.exceptions.UserNotFoundException;
import eu.dickovadev.pojisteniapp.mappers.PolicyMapper;
import eu.dickovadev.pojisteniapp.mappers.UserMapper;
import eu.dickovadev.pojisteniapp.models.responses.UserDetailResponse;
import eu.dickovadev.pojisteniapp.models.responses.UserIndexResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service class responsible for managing user-related operations.
 * Handles user creation, editing, deletion, and fetching user details along with associated policies.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PolicyMapper policyMapper;
    private final PolicyRepository policyRepository;
    private final PaginationService paginationService;
    private final SearchService searchService;
    private final AuditLogService auditLogService;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            PolicyMapper policyMapper,
            PolicyRepository policyRepository,
            PaginationService paginationService,
            SearchService searchService,
            AuditLogService auditLogService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.policyMapper = policyMapper;
        this.policyRepository = policyRepository;
        this.paginationService = paginationService;
        this.searchService = searchService;
        this.auditLogService = auditLogService;
    }

    /**
     * Creates a new user using the provided UserDTO data.
     * It checks if the email already exists, adds the default role, and then persists the new user entity.
     * After successful creation, logs the action in the audit log.
     *
     * @param userDTO the UserDTO containing user data.
     * @return the ID of the newly created user.
     * @throws EmailAlreadyExistsException if a user with the given email already exists.
     */
    @Override
    @Transactional
    public long create(
            UserDTO userDTO
    ) {

        // Check if user already exists by email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        // Create new user entity and map the DTO
        UserEntity newUser = userMapper.toEntity(userDTO);

        // Set default role
        newUser.addRole(Role.ROLE_INSURED);

        // Save the new user
        userRepository.save(newUser);

        // Update the DTO with the newly generated userId
        userDTO.setUserId(newUser.getUserId()); // needed for redirect

        auditLogService.logAction(
                "CREATE",
                "UserEntity",
                newUser.getUserId(),
                "Customer with ID " + newUser.getUserId() + " created."
        );

        return userDTO.getUserId();
    }

    /**
     * Edits an existing user's details based on the provided UserDTO data.
     * This method is accessible only by administrators. It checks for duplicate emails, handles roles
     * (especially the REGISTERED role for users with passwords), and saves the updated user data.
     * The action is logged in the audit log.
     *
     * @param user   the UserDTO containing updated user data.
     * @param userId the ID of the user to edit.
     * @throws EmailAlreadyExistsException if the new email is already taken by another user.
     */
    @Override
    @Transactional
    public void editByAdmin(
            UserDTO user,
            long userId
    ) {
        // Ensure userId is correct
        user.setUserId(userId);

        UserEntity fetchedUser = getEntityByIdOrThrow(user.getUserId());

        // Check if email has changed, and if it's already taken
        checkEmailChangeAndDuplication(user, fetchedUser);
        // Make sure role REGISTERED stays if user has password
        handlePasswordRole(user, fetchedUser);

        // Update user entity and save
        userMapper.updateUserEntity(user, fetchedUser);
        userRepository.save(fetchedUser);

        auditLogService.logAction(
                "UPDATE",
                "UserEntity",
                userId,
                "Customer with ID " + userId + " edited by admin."
        );
    }

    /**
     * Allows customers to edit their own details, but prevents modifying roles.
     * Ensures the email is not duplicated and that the user retains certain roles (like REGISTERED).
     * This method also logs the action for auditing purposes.
     *
     * @param user   the UserDTO containing updated user data.
     * @param userId the ID of the user to edit.
     * @throws EmailAlreadyExistsException if the new email is already taken by another user.
     */
    @Override
    @Transactional
    public void editByCustomer(
            UserDTO user,
            long userId
    ) {
        // Ensure userId is correct
        user.setUserId(userId);

        UserEntity fetchedUser = getEntityByIdOrThrow(user.getUserId());

        // Check if email has changed, and if it's already taken
        checkEmailChangeAndDuplication(user, fetchedUser);
        // Make sure role REGISTERED stays if user has password
        handlePasswordRole(user, fetchedUser);

        // Customer can't edit roles - check for roles from entity
        if (fetchedUser.getRoles() != null) {
            for (Role role : fetchedUser.getRoles()) {
                user.getRoles().add(role);
            }
        }

        // Update user entity and save
        userMapper.updateUserEntity(user, fetchedUser);
        userRepository.save(fetchedUser);

        auditLogService.logAction(
                "UPDATE",
                "UserEntity",
                userId,
                "Customer with ID " + userId + " edited by customer."
        );
    }

    /**
     * Removes a user from the system, including all associated insurance policies.
     * All policies where the user is the policyholder or the insured user are deleted.
     * The user deletion is logged in the audit log.
     *
     * @param userId the ID of the user to remove.
     */
    @Override
    @Transactional
    public void remove(long userId) {
        UserEntity fetchedEntity = getEntityByIdOrThrow(userId);
        // delete policies where user is policyHolder or insuredUser
        policyRepository.deleteByPolicyHolderOrInsuredUser(fetchedEntity, fetchedEntity);
        userRepository.delete(fetchedEntity);

        auditLogService.logAction(
                "DELETE",
                "UserEntity",
                userId,
                "Customer with ID " + userId + " deleted."
        );
    }

    /**
     * Retrieves a list of all users, excluding those with the "ADMIN" role.
     *
     * @return a list of UserDTOs representing all users except administrators.
     */
    @Override
    public List<UserDTO> getUsersListExcludingAdmins() {
        return StreamSupport.stream(userRepository.findUsersExcludingAdmins().spliterator(), false)
                .map(i -> userMapper.toDTO(i))
                .toList();
    }

    /**
     * Retrieves a user's details by their ID, including associated insurance policies.
     * If the user is not found, a UserNotFoundException is thrown.
     *
     * @param userId the ID of the user to retrieve.
     * @return the UserDTO containing the user data and associated policies.
     * @throws UserNotFoundException if the user with the given ID is not found.
     */
    @Override
    public UserDTO getByIdOrElseThrow(long userId) {

        // Fetch the UserEntity from the database
        UserEntity fetchedUser = userRepository.findByIdWithRoles(userId)
                .orElseThrow(UserNotFoundException::new);

        // Convert the UserEntity to UserDTO
        UserDTO userDTO = userMapper.toDTO(fetchedUser);

        // Fetch the policies where the user is either the policyHolder or insuredUser
        Set<PolicyEntity> allPolicies = policyRepository
                .findByPolicyHolderOrInsuredUser(fetchedUser, fetchedUser);

        // Map the fetched policies to PolicyDTOs
        Set<PolicyDTO> policyDTOs = allPolicies.stream()
                .map(policy -> policyMapper.toDTO(policy))
                .collect(Collectors.toSet());

        // Set the mapped policies in the UserDTO
        userDTO.setPolicies(policyDTOs);

        // Return the populated UserDTO
        return userDTO;
    }

    /**
     * Retrieves a paginated list of users, optionally filtered by search query and field.
     *
     * @param query       the search query (optional).
     * @param searchField the field to search (optional).
     * @param page        the current page number.
     * @param pageSize    the number of users per page.
     * @return a UserIndexResponse containing the paginated list of users and pagination metadata.
     */
    @Override
    public UserIndexResponse getPaginatedUsers(String query, String searchField, int page, int pageSize) {
        Page<UserDTO> userPage;
        Pageable pageable = paginationService.createPageable(page, pageSize);

        // search query
        if (query != null && !query.isEmpty()) {
            userPage = searchService.search(query, searchField, UserDTO.class, pageable);
        } else {
            userPage = userRepository.findNonAdminUsersWithValidNames(pageable)
                    .map(user -> userMapper.toDTO(user));
        }

        // Get pagination metadata using the PaginationService
        Map<String, Integer> paginationMetadata = paginationService.getPaginationMetadata(userPage);
        return new UserIndexResponse(userPage.getContent(), paginationMetadata);
    }

    /**
     * Retrieves the user's data and paginated insurance policies.
     * If the user is not found or the access is denied, it throws an exception.
     *
     * @param userId   the ID of the user to retrieve.
     * @param page     the current page number for pagination.
     * @param pageSize the number of policies per page.
     * @return a UserDetailResponse containing the user and their paginated policies.
     * @throws AccessDeniedException if the authenticated user is not authorized to view the details.
     */
    @Override
    public UserDetailResponse getUserWithPaginatedPolicies(long userId, int page, int pageSize) {
        UserDTO user = getByIdOrElseThrow(userId);

        Pageable pageable = PageRequest.of(page - 1, pageSize); // Pageable is zero-indexed

        Page<PolicyEntity> policyPage = policyRepository.findByUserId(userId, pageable);

        List<PolicyDTO> policyDToList = policyPage.getContent().stream()
                .map(policy -> policyMapper.toDTO(policy))
                .toList();

        Map<String, Integer> paginationMetadata = paginationService.getPaginationMetadata(policyPage);

        return new UserDetailResponse(user, policyDToList, paginationMetadata);
    }

    /**
     * Retrieves data for editing a user's profile.
     * This method is used to populate the form with existing user data.
     *
     * @param userId the ID of the user to retrieve.
     * @param user   the UserDTO containing updated user data.
     * @return the updated UserDTO with user data.
     */
    @Override
    public UserDTO getUserEditData(long userId, UserDTO user) {

        UserDTO fetchedUser = getByIdOrElseThrow(userId);
        userMapper.updateUserDTO(fetchedUser, user);

        return user;
    }

    /**
     * Retrieves a user entity by its ID, or throws a UserNotFoundException if the user does not exist.
     *
     * @param userId the ID of the user to retrieve.
     * @return the UserEntity associated with the given ID.
     * @throws UserNotFoundException if the user is not found.
     */
    @Override
    public UserEntity getEntityByIdOrThrow(long userId) {
        // Retrieve the UserEntity based on userId
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    /**
     * Handles the addition or removal of the REGISTERED role based on the presence of a password.
     * If the user has a password, the REGISTERED role is added; otherwise, it is removed.
     *
     * @param user        the UserDTO containing user data.
     * @param fetchedUser the UserEntity to check.
     */
    private void handlePasswordRole(UserDTO user, UserEntity fetchedUser) {
        if (fetchedUser.getPassword() != null) {
            user.getRoles().add(Role.ROLE_REGISTERED);
        } else {
            user.getRoles().remove(Role.ROLE_REGISTERED);
        }

    }

    /**
     * Checks whether the email has been changed and verifies if the new email is already taken.
     * If the email is already in use, throws an EmailAlreadyExistsException.
     *
     * @param user        the UserDTO with the updated email.
     * @param fetchedUser the UserEntity to compare the email with.
     * @throws EmailAlreadyExistsException if the email is already taken.
     */
    private void checkEmailChangeAndDuplication(UserDTO user, UserEntity fetchedUser) {
        if (!fetchedUser.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
    }
}
