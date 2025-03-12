package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.entities.PolicyEntity;
import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.PolicyUserDTO;
import eu.dickovadev.pojisteniapp.models.enums.PolicyType;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import eu.dickovadev.pojisteniapp.models.exceptions.AccessDeniedException;
import eu.dickovadev.pojisteniapp.models.responses.PolicyCreateResponse;
import eu.dickovadev.pojisteniapp.repositories.EventRepository;
import eu.dickovadev.pojisteniapp.repositories.PolicyRepository;
import eu.dickovadev.pojisteniapp.repositories.UserRepository;
import eu.dickovadev.pojisteniapp.models.dto.EventDTO;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.exceptions.InvalidPolicyDatesException;
import eu.dickovadev.pojisteniapp.models.exceptions.PolicyNotFoundException;
import eu.dickovadev.pojisteniapp.mappers.EventMapper;
import eu.dickovadev.pojisteniapp.mappers.PolicyMapper;
import eu.dickovadev.pojisteniapp.models.responses.PolicyDetailResponse;
import eu.dickovadev.pojisteniapp.models.responses.PolicyEditResponse;
import eu.dickovadev.pojisteniapp.models.responses.PolicyIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PaginationService paginationService;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final SearchService searchService;
    private final AuditLogService auditLogService;

    @Autowired
    public PolicyServiceImpl(
            PolicyRepository policyRepository,
            PolicyMapper policyMapper,
            UserService userService,
            UserRepository userRepository,
            PaginationService paginationService,
            EventMapper eventMapper,
            EventRepository eventRepository,
            SearchService searchService,
            AuditLogService auditLogService) {
        this.policyRepository = policyRepository;
        this.policyMapper = policyMapper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.paginationService = paginationService;
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
        this.searchService = searchService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public void create(PolicyDTO policyDTO, long userId) {
        if (policyDTO.getEndDate().isBefore(policyDTO.getStartDate())) {
            throw new InvalidPolicyDatesException();
        }

        UserEntity insuredUserEntity = userService.getEntityByIdOrThrow(userId);
        UserEntity policyHolderEntity = userService.getEntityByIdOrThrow(policyDTO.getPolicyHolder().getUserId());

        PolicyEntity newPolicy = policyMapper.toEntity(policyDTO);
        newPolicy.setPolicyHolder(policyHolderEntity);
        newPolicy.setInsuredUser(insuredUserEntity);

        assignRoles(policyHolderEntity, insuredUserEntity);

        PolicyEntity savedPolicy = policyRepository.save(newPolicy);
        policyDTO.setPolicyId(savedPolicy.getPolicyId());

        auditLogService.logAction(
                "CREATE",
                "PolicyEntity",
                savedPolicy.getPolicyId(),
                "Policy with ID " + savedPolicy.getPolicyId() + " created."
        );
    }

    @Override
    @Transactional
    public long edit(long policyId, PolicyDTO policy) {

        if (policy.getEndDate().isBefore(policy.getStartDate())) {
            throw new InvalidPolicyDatesException();
        }

        // Fetch policy entity
        PolicyEntity fetchedPolicy = getEntityByIdOrThrow(policyId);

        // Fetch users
        UserEntity policyHolderEntity = userService.getEntityByIdOrThrow(policy.getPolicyHolder().getUserId());
        UserEntity insuredUserEntity = userService.getEntityByIdOrThrow(policy.getInsuredUser().getUserId());

        // Assign users to policy
        fetchedPolicy.setPolicyHolder(policyHolderEntity);
        fetchedPolicy.setInsuredUser(insuredUserEntity);

        // Update policy details
        policyMapper.updatePolicyEntity(policy, fetchedPolicy);

        // Ensure roles are properly assigned
        assignRoles(policyHolderEntity, insuredUserEntity);

        // Save the updated policy after all changes
        policyRepository.save(fetchedPolicy);

        auditLogService.logAction(
                "UPDATE",
                "PolicyEntity",
                policyId,
                "Policy with ID " + policyId + " edited."
        );

        return policyHolderEntity.getUserId();
    }

    @Override
    @Transactional
    public long remove(long policyId) {
        //get userId for redirect
        long userId = getPolicyHolderIdByPolicyId(policyId);

        PolicyEntity fetchedPolicy = getEntityByIdOrThrow(policyId);
        policyRepository.delete(fetchedPolicy);

        auditLogService.logAction(
                "DELETE",
                "PolicyEntity",
                policyId,
                "Policy with ID " + policyId + " deleted."
        );

        return userId;
    }

    @Override
    @Transactional
    public Page<PolicyDTO> getAll(Pageable pageable) {
        return policyRepository.findAll(pageable)
                .map(policy -> policyMapper.toDTO(policy));
    }

    @Override
    @Transactional
    public PolicyDTO getByIdOrElseThrow(long policyId) {
        // Fetch the policy and load associated policyHolder and insuredUser
        PolicyEntity fetchedPolicy = policyRepository.findByIdWithUserDetails(policyId)
                .orElseThrow(PolicyNotFoundException::new);

        UserEntity policyHolder = fetchedPolicy.getPolicyHolder();
        UserEntity insuredUser = fetchedPolicy.getInsuredUser();

        // Create PolicyUserDTO objects from UserEntity
        PolicyUserDTO policyHolderDTO = createPolicyUserDTO(policyHolder);
        PolicyUserDTO insuredUserDTO = createPolicyUserDTO(insuredUser);

        // Map the policy to a PolicyDTO
        PolicyDTO policyDTO = policyMapper.toDTO(fetchedPolicy);

        // Set the policyHolder and insuredUser in the PolicyDTO
        policyDTO.setPolicyHolder(policyHolderDTO);
        policyDTO.setInsuredUser(insuredUserDTO);
        policyDTO.setSameUser(arePolicyHolderAndInsuredUserSame(policyDTO));

        return policyDTO;
    }

    @Override
    public PolicyEntity getEntityByIdOrThrow(long policyId) {
        return policyRepository.findByIdWithUserDetails(policyId).orElseThrow(PolicyNotFoundException::new);
    }

    @Override
    public PolicyIndexResponse getPaginatedPolicies(String query, String searchField, int page, int pageSize) {
        Page<PolicyDTO> policyPage;
        Pageable pageable = paginationService.createPageable(page, pageSize);

        // search query
        if (query != null && !query.isEmpty()) {
            policyPage = searchService.search(query, searchField, PolicyDTO.class, pageable);
        } else {
            policyPage = getAll(pageable);  //if no query get all policies
        }

        List<PolicyDTO> paginatedList = policyPage.getContent();

        for (PolicyDTO policy : paginatedList) {
            policy.setSameUser(arePolicyHolderAndInsuredUserSame(policy));
        }

        Map<String, Integer> paginationMetadata = paginationService.getPaginationMetadata(policyPage);

        return new PolicyIndexResponse(paginatedList, paginationMetadata);
    }

    @Override
    public PolicyDetailResponse getPolicyWithPaginatedEvents(long policyId, int page, int pageSize, Authentication authentication) {
        // Get the authenticated user from the Authentication object
        UserEntity authenticatedUser = (UserEntity) authentication.getPrincipal();

        PolicyDTO policy = getByIdOrElseThrow(policyId);
        long policyHolderId = policy.getPolicyHolder().getUserId();
        long insuredUserId = policy.getInsuredUser().getUserId();

        if (authenticatedUser.getUserId() != policyHolderId
                && authenticatedUser.getUserId() != insuredUserId
                && !authenticatedUser.getRoles().contains(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException();
        }

        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<EventDTO> paginatedEvents = getEventsForPolicy(policyId, pageable);

        Map<String, Integer> paginationMetadata = paginationService.getPaginationMetadata(paginatedEvents);

        return new PolicyDetailResponse(policy, policyHolderId, paginatedEvents.getContent(), paginationMetadata);
    }

    @Override
    public PolicyEditResponse getPolicyEditData(long policyId) {
        PolicyDTO fetchedPolicy = getByIdOrElseThrow(policyId);
        long userId = getPolicyHolderIdByPolicyId(policyId);

        UserEntity fetchedInsured = userService.getEntityByIdOrThrow(fetchedPolicy.getInsuredUser().getUserId());
        PolicyUserDTO insuredUser = createPolicyUserDTO(fetchedInsured);

        List<UserDTO> users = userService.getUsersListExcludingAdmins();

        return new PolicyEditResponse(fetchedPolicy, users, userId, insuredUser);
    }

    @Override
    public PolicyCreateResponse getPolicyCreateData(long userId) {

        UserEntity insured = userService.getEntityByIdOrThrow(userId);
        List<UserDTO> users = userService.getUsersListExcludingAdmins();
        PolicyType[] policyTypes = PolicyType.values();

        // Create PolicyUserDTO for insured user
        PolicyUserDTO insuredUserDTO = createPolicyUserDTO(insured);

        return new PolicyCreateResponse(policyTypes, users, insuredUserDTO.getFirstName(), insuredUserDTO.getLastName());
    }

    public long getPolicyHolderIdByPolicyId(long policyId) {
        PolicyEntity policy = getEntityByIdOrThrow(policyId);
        return policy.getPolicyHolder().getUserId();
    }

    private Page<EventDTO> getEventsForPolicy(long policyId, Pageable pageable) {
        return eventRepository.findByPolicyId(policyId, pageable)
                .map(eventMapper::toDTO);
    }

    private boolean arePolicyHolderAndInsuredUserSame(PolicyDTO policy) {
        if (policy.getPolicyHolder() == null || policy.getInsuredUser() == null) {
            return false;
        }
        return policy.getPolicyHolder().getUserId() == policy.getInsuredUser().getUserId();
    }

    private void assignRoles(UserEntity policyHolder, UserEntity insuredUser) {
        boolean samePerson = policyHolder.getUserId() == insuredUser.getUserId();

        // Ensure the insured user has INSURED role
        insuredUser.getRoles().add(Role.ROLE_INSURED);

        // Ensure the policyholder has POLICYHOLDER role only if they are different
        if (!samePerson) {
            policyHolder.getRoles().add(Role.ROLE_POLICYHOLDER);
        }

        // Save updated users
        userRepository.save(insuredUser);
        userRepository.save(policyHolder);
    }

    private PolicyUserDTO createPolicyUserDTO(UserEntity user) {
        PolicyUserDTO userDTO = new PolicyUserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

}