package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.mappers.AuditLogMapper;
import eu.dickovadev.pojisteniapp.mappers.UserMapper;
import eu.dickovadev.pojisteniapp.models.dto.AuditLogDTO;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.enums.PolicyType;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import eu.dickovadev.pojisteniapp.models.responses.AdminUsersResponse;
import eu.dickovadev.pojisteniapp.models.responses.AuditLogResponse;
import eu.dickovadev.pojisteniapp.models.responses.NullUsersResponse;
import eu.dickovadev.pojisteniapp.models.responses.StatisticsResponse;
import eu.dickovadev.pojisteniapp.repositories.AuditLogRepository;
import eu.dickovadev.pojisteniapp.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final UserRepository userRepository;
    private final PaginationService paginationService;
    private final UserMapper userMapper;
    private final UserService userService;
    private final AuditLogService auditLogService;
    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Autowired
    public AdminServiceImpl(UserRepository userRepository,
                            PaginationService paginationService,
                            UserMapper userMapper,
                            UserService userService,
                            AuditLogService auditLogService,
                            AuditLogRepository auditLogRepository,
                            AuditLogMapper auditLogMapper) {
        this.userRepository = userRepository;
        this.paginationService = paginationService;
        this.userMapper = userMapper;
        this.userService = userService;
        this.auditLogService = auditLogService;
        this.auditLogRepository = auditLogRepository;
        this.auditLogMapper = auditLogMapper;
    }


    @Override
    public NullUsersResponse getPaginatedNullUsers(int page, int pageSize) {
        log.debug("Fetching paginated null users: page {}, pageSize {}", page, pageSize);

        Pageable pageable = paginationService.createPageable(page, pageSize);

        Page<UserDTO> userPage = userRepository.findUsersWithNullFirstNameOrLastName(pageable)
                .map(user -> userMapper.toDTO(user));

        List<UserDTO> userDTOList = userPage.getContent();
        Map<String, Integer> paginationMetadata = paginationService.getPaginationMetadata(userPage);

        return new NullUsersResponse(userDTOList, paginationMetadata);
    }

    @Override
    public AdminUsersResponse getPaginatedAdminUsers(int page, int pageSize) {
        log.debug("Fetching paginated admin users: page {}, pageSize {}", page, pageSize);

        Pageable pageable = paginationService.createPageable(page, pageSize);

        Page<UserDTO> adminPage = userRepository.findAdminUsers(Role.ROLE_ADMIN, pageable)
                .map(user -> userMapper.toDTO(user));

        List<UserDTO> adminDTOList = adminPage.getContent();
        Map<String, Integer> paginationMetadata = paginationService.getPaginationMetadata(adminPage);

        return new AdminUsersResponse(adminDTOList, paginationMetadata);
    }

    @Override
    public AuditLogResponse getAuditLogs(int page, int pageSize) {
        log.debug("Fetching audit logs: page {}, pageSize {}", page, pageSize);

        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("id")));

        Page<AuditLogDTO> auditLogPage = auditLogRepository.findAll(pageable)
                .map(log -> auditLogMapper.toDTO(log));

        List<AuditLogDTO> auditLogDTOList = auditLogPage.getContent();
        Map<String, Integer> paginationMetadata = paginationService.getPaginationMetadata(auditLogPage);

        return new AuditLogResponse(auditLogDTOList, paginationMetadata);
    }

    @Override
    @Transactional
    public void setAdminRole(long userId) {

        log.info("Assigning ADMIN role to user with ID: {}", userId);
        UserEntity fetchedUser = userService.getEntityByIdOrThrow(userId);

        fetchedUser.addRole(Role.ROLE_ADMIN);
        userRepository.save(fetchedUser);
        log.info("Successfully updated role for user {}", userId);

        try {
            auditLogService.logAction(
                    "UPDATE",
                    "UserEntity",
                    userId,
                    "User with ID " + userId + " assigned ADMIN role."
            );
        } catch (Exception ex) {
            log.warn("Audit log failed for setAdminRole on user {}", userId, ex);
        }
    }

    @Override
    @Transactional
    public void removeAdminRole(long userId) {
        log.info("Removing ADMIN role from user with ID: {}", userId);
        // Fetch the user by ID
        UserEntity fetchedUser = userService.getEntityByIdOrThrow(userId);

        fetchedUser.removeRole(Role.ROLE_ADMIN);

        // Save the updated user entity
        userRepository.save(fetchedUser);
        log.info("Successfully removed role from user {}", userId);

        try {
            auditLogService.logAction(
                    "UPDATE",
                    "UserEntity",
                    userId,
                    "User with ID " + userId + " removed ADMIN role."
            );
        } catch (Exception ex) {
            log.warn("Audit log failed for removeAdminRole on user {}", userId, ex);
        }
    }
}
