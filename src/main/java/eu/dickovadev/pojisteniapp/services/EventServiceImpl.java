package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.entities.EventEntity;
import eu.dickovadev.pojisteniapp.entities.PolicyEntity;
import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.enums.EventStatus;
import eu.dickovadev.pojisteniapp.models.enums.EventType;
import eu.dickovadev.pojisteniapp.models.enums.PolicyType;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import eu.dickovadev.pojisteniapp.models.exceptions.AccessDeniedException;
import eu.dickovadev.pojisteniapp.models.exceptions.AmountPaidExceedException;
import eu.dickovadev.pojisteniapp.models.responses.EventCreateResponse;
import eu.dickovadev.pojisteniapp.models.responses.EventDetailResponse;
import eu.dickovadev.pojisteniapp.models.responses.EventEditResponse;
import eu.dickovadev.pojisteniapp.models.responses.EventIndexResponse;
import eu.dickovadev.pojisteniapp.repositories.EventRepository;
import eu.dickovadev.pojisteniapp.repositories.PolicyRepository;
import eu.dickovadev.pojisteniapp.models.dto.EventDTO;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.exceptions.EventNotFoundException;
import eu.dickovadev.pojisteniapp.models.exceptions.PolicyNotFoundException;
import eu.dickovadev.pojisteniapp.mappers.EventMapper;
import eu.dickovadev.pojisteniapp.mappers.PolicyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final PolicyService policyService;
    private final EventMapper eventMapper;
    private final EventMappingService eventMappingService;
    private final SearchService searchService;
    private final PaginationService paginationService;
    private final AuditLogService auditLogService;

    @Autowired
    public EventServiceImpl(
            EventRepository eventRepository,
            PolicyService policyService,
            EventMapper eventMapper,
            EventMappingService eventMappingService,
            SearchService searchService,
            PaginationService paginationService,
            AuditLogService auditLogService) {
        this.eventRepository = eventRepository;
        this.policyService = policyService;
        this.eventMapper = eventMapper;
        this.eventMappingService = eventMappingService;
        this.searchService = searchService;
        this.paginationService = paginationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public void create(
            EventDTO event,
            long policyId
    ) {

        // Fetch policy entity by ID
        PolicyEntity policy = policyService.getEntityByIdOrThrow(policyId);
        event.setAmountPaid(0);

        // Map the EventDTO to EventEntity
        EventEntity newEvent = eventMapper.toEntity(event);

        // Set the policy on the event
        newEvent.setPolicy(policy);

        //Save the new event
        EventEntity savedEvent = eventRepository.save(newEvent);

        //Set generated ID for redirect
        event.setEventId(savedEvent.getEventId());

        auditLogService.logAction(
                "CREATE",
                "EventEntity",
                savedEvent.getEventId(),
                "Event with ID " + savedEvent.getEventId() + " created"
        );
    }

    @Override
    @Transactional
    public void edit(EventDTO event, long eventId) {

        //Fetch the event entity
        EventEntity fetchedEvent = getEntityByIdOrThrow(eventId);

        if (event.getAmountPaid() > fetchedEvent.getOriginalClaimAmount()) {
            throw new AmountPaidExceedException();
        }

        event.setAmountPaid(fetchedEvent.getAmountPaid() + event.getAmountPaid());

        // Update event entity and save
        eventMapper.updateEventEntity(event, fetchedEvent);
        eventRepository.save(fetchedEvent);

        auditLogService.logAction(
                "UPDATE",
                "EventEntity",
                fetchedEvent.getEventId(),
                "Event edited"
        );
    }

    @Override
    @Transactional
    public void report(
            EventDTO event,
            long policyId,
            Authentication authentication
    ) {
        // Get the authenticated user from the Authentication object
        UserEntity authenticatedUser = (UserEntity) authentication.getPrincipal();

        // Fetch policy entity by ID
        PolicyEntity policy = policyService.getEntityByIdOrThrow(policyId);

        if (authenticatedUser.getUserId() != policy.getPolicyHolder().getUserId()
                && authenticatedUser.getUserId() != policy.getInsuredUser().getUserId()) {
            throw new AccessDeniedException();
        }

        event.setAmountPaid(0);
        event.setOriginalClaimAmount(0);
        event.setEventStatus(EventStatus.PENDING_APPROVAL);

        // Map the EventDTO to EventEntity
        EventEntity newEvent = eventMapper.toEntity(event);

        // Set the policy on the event
        newEvent.setPolicy(policy);

        //Save the new event
        EventEntity savedEvent = eventRepository.save(newEvent);

        //Set generated ID for redirect
        event.setEventId(savedEvent.getEventId());

        auditLogService.logAction(
                "CREATE",
                "EventEntity",
                savedEvent.getEventId(),
                "Event with ID " + savedEvent.getEventId() + " reported"
        );
    }

    @Override
    @Transactional
    public long remove(long eventId) {

        EventEntity fetchedEntity = getEntityByIdOrThrow(eventId);

        // get policyId for redirect
        long policyId = fetchedEntity.getPolicy().getPolicyId();

        //delete from DB
        eventRepository.delete(fetchedEntity);

        auditLogService.logAction(
                "DELETE",
                "EventEntity",
                fetchedEntity.getEventId(),
                "Event with ID " + eventId + " removed"
        );

        return policyId;
    }

    @Override
    public Page<EventDTO> getAll(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(event -> eventMapper.toDTO(event));
    }

    @Override
    public EventDTO getById(long eventId) {
        // fetch the event entity from db
        EventEntity fetchedEvent = getEntityByIdOrThrow(eventId);

        // map the event entity to event DTO
        EventDTO eventDTO = eventMapper.toDTO(fetchedEvent);

        long policyId = eventDTO.getPolicyId();

        PolicyDTO policyDTO = policyService.getByIdOrElseThrow(policyId);


        // Set the id and type of policy
        eventDTO.setPolicyId(policyDTO.getPolicyId());
        eventDTO.setPolicyType(policyDTO.getType());
        eventDTO.setPolicyHolderId(policyDTO.getPolicyHolder().getUserId());
        eventDTO.setInsuredUserId(policyDTO.getInsuredUser().getUserId());

        return eventDTO;
    }

    @Override
    public EventIndexResponse getEventIndexData(String queryId, String queryStatus, String searchField, int page, int pageSize) {
        Page<EventDTO> eventPage;
        Pageable pageable = paginationService.createPageable(page, pageSize);

        String query = null;
        if (queryId != null && !queryId.isEmpty()) {
            query = queryId;  // prioritize queryId if it's set
        } else if (queryStatus != null && !queryStatus.isEmpty()) {
            query = queryStatus;  // fallback to queryStatus if queryId is not set
        }

        // search query
        if (query != null && !query.isEmpty()) {
            eventPage = searchService.search(query, searchField, EventDTO.class, pageable);
        } else {
            eventPage = getAll(pageable);
        }

        List<EventDTO> paginatedList = eventPage.getContent();
        Map<String, Integer> paginationMetadata = paginationService.getPaginationMetadata(eventPage);

        return new EventIndexResponse(paginatedList, paginationMetadata);
    }

    @Override
    public EventCreateResponse getEventCreateData(long policyId, Authentication authentication) {
        // Get the authenticated user from the Authentication object
        UserEntity authenticatedUser = (UserEntity) authentication.getPrincipal();

        // Get available event types from linked policy for dropdown menu
        PolicyDTO policy = policyService.getByIdOrElseThrow(policyId);
        PolicyType policyType = policy.getType();
        List<EventType> availableEventTypes = eventMappingService.getEventsForPolicyType(policyType);

        // Check if the user is authorized to access the policy
        if (!isUserAuthorizedToAccessPolicy(authenticatedUser, policy)) {
            throw new AccessDeniedException();
        }

        // Get event statuses for dropdown menu
        EventStatus[] eventStatuses = EventStatus.values();

        return new EventCreateResponse(availableEventTypes, eventStatuses);
    }

    @Override
    public EventDetailResponse getEventDetailData(long eventId, Authentication authentication) {
        // Get the authenticated user from the Authentication object
        UserEntity authenticatedUser = (UserEntity) authentication.getPrincipal();

        EventDTO event = getById(eventId);
        PolicyDTO policy = policyService.getByIdOrElseThrow(event.getPolicyId());

        // Check if the user is authorized to access the event
        if (!isUserAuthorizedToAccessPolicy(authenticatedUser, policy)) {
            throw new AccessDeniedException();
        }

        return new EventDetailResponse(event, policy);
    }

    @Override
    public EventEditResponse getEventEditResponse(long eventId) {
        EventDTO fetchedEvent = getById(eventId);

        // Get available event types from policy type
        PolicyType policyType = fetchedEvent.getPolicyType();
        List<EventType> availableEventTypes = eventMappingService.getEventsForPolicyType(policyType);

        // Get event statuses for dropdown menu
        EventStatus[] eventStatuses = EventStatus.values();

        return new EventEditResponse(fetchedEvent, availableEventTypes, eventStatuses);
    }

    private EventEntity getEntityByIdOrThrow(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(EventNotFoundException::new);
    }

    private boolean isUserAuthorizedToAccessPolicy(UserEntity authenticatedUser, PolicyDTO policy) {
        return authenticatedUser.getUserId() == policy.getPolicyHolder().getUserId()
                || authenticatedUser.getUserId() == policy.getInsuredUser().getUserId()
                || authenticatedUser.getRoles().contains(Role.ROLE_ADMIN);
    }

}
