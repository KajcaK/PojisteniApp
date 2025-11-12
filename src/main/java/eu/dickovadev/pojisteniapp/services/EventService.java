package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.models.dto.EventDTO;
import eu.dickovadev.pojisteniapp.models.responses.EventCreateResponse;
import eu.dickovadev.pojisteniapp.models.responses.EventDetailResponse;
import eu.dickovadev.pojisteniapp.models.responses.EventEditResponse;
import eu.dickovadev.pojisteniapp.models.responses.EventIndexResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface EventService {

    void create(EventDTO event, long policyId);

    void edit(EventDTO event, long eventId);

    void report(EventDTO event, long policyId, Authentication authentication);

    long remove(long eventId);

    Page<EventDTO> getAll(Pageable pageable);

    EventDTO getById(long eventId);

    EventIndexResponse getEventIndexData(String queryId, String queryStatus, String searchField, int page, int pageSize);

    EventCreateResponse getEventCreateData(long policyId, Authentication authentication);

    EventDetailResponse getEventDetailData(long eventId, Authentication authentication);

    EventEditResponse getEventEditResponse(long eventId);
}
