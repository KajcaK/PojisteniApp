package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.entities.EventEntity;
import eu.dickovadev.pojisteniapp.entities.PolicyEntity;
import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.mappers.EventMapper;
import eu.dickovadev.pojisteniapp.mappers.PolicyMapper;
import eu.dickovadev.pojisteniapp.mappers.UserMapper;
import eu.dickovadev.pojisteniapp.models.dto.EventDTO;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.enums.EventStatus;
import eu.dickovadev.pojisteniapp.repositories.EventRepository;
import eu.dickovadev.pojisteniapp.repositories.PolicyRepository;
import eu.dickovadev.pojisteniapp.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {

    private final EventRepository eventRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final PolicyMapper policyMapper;
    private final UserMapper userMapper;

    public SearchServiceImpl(EventRepository eventRepository, PolicyRepository policyRepository,
                             UserRepository userRepository, EventMapper eventMapper,
                             PolicyMapper policyMapper, UserMapper userMapper) {
        this.eventRepository = eventRepository;
        this.policyRepository = policyRepository;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
        this.policyMapper = policyMapper;
        this.userMapper = userMapper;
    }

    @Override
    public <T> Page<T> search(String query, String searchField, Class<T> dtoClass, Pageable pageable) {
        if (dtoClass.equals(EventDTO.class)) {
            return searchEvents(query, searchField, pageable).map(dtoClass::cast);
        } else if (dtoClass.equals(PolicyDTO.class)) {
            return searchPolicies(query, searchField, pageable).map(dtoClass::cast);
        } else if (dtoClass.equals(UserDTO.class)) {
            return searchUsers(query, searchField, pageable).map(dtoClass::cast);
        }
        return Page.empty();
    }

    private Page<EventDTO> searchEvents(String query, String searchField, Pageable pageable) {
        Page<EventEntity> pageResult = Page.empty();

        if ("eventStatus".equals(searchField)) {
            try {
                EventStatus status = EventStatus.valueOf(query);
                pageResult = eventRepository.findByEventStatus(status, pageable);
            } catch (IllegalArgumentException e) {
                return Page.empty(); // Invalid enum value
            }
        } else if ("eventId".equals(searchField)) {
            try {
                long eventId = Long.parseLong(query);
                pageResult = eventRepository.findByEventId(eventId, pageable);
            } catch (NumberFormatException e) {
                return Page.empty(); // Invalid number
            }
        }

        return pageResult.map(eventMapper::toDTO);
    }

    private Page<PolicyDTO> searchPolicies(String query, String searchField, Pageable pageable) {
        Page<PolicyEntity> pageResult = Page.empty();

        if ("userId".equals(searchField)) {
            try {
                long userId = Long.parseLong(query);
                pageResult = policyRepository.findByUserId(userId, pageable);
            } catch (NumberFormatException e) {
                return Page.empty(); // Invalid number
            }
        } else if ("email".equals(searchField)) {
            pageResult = policyRepository.findByEmail(query, pageable);
        }

        return pageResult.map(policyMapper::toDTO);
    }

    private Page<UserDTO> searchUsers(String query, String searchField, Pageable pageable) {
        Page<UserEntity> pageResult = Page.empty();

        if ("userId".equals(searchField)) {
            try {
                long userId = Long.parseLong(query);
                pageResult = userRepository.findByUserId(userId, pageable);
            } catch (NumberFormatException e) {
                return Page.empty(); // Invalid number
            }
        } else if ("email".equals(searchField)) {
            pageResult = userRepository.findByEmail(query, pageable);
        }

        return pageResult.map(userMapper::toDTO);
    }
}
