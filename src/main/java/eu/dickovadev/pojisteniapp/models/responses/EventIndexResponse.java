package eu.dickovadev.pojisteniapp.models.responses;

import eu.dickovadev.pojisteniapp.models.dto.EventDTO;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;
import java.util.Map;

public class EventIndexResponse {

    private List<EventDTO> paginatedList;
    private Map<String, Integer> paginationMetadata;

    public EventIndexResponse(List<EventDTO> paginatedList, Map<String, Integer> paginationMetadata) {
        this.paginatedList = paginatedList;
        this.paginationMetadata = paginationMetadata;
    }

    public List<EventDTO> getPaginatedList() {
        return paginatedList;
    }

    public Map<String, Integer> getPaginationMetadata() {
        return paginationMetadata;
    }

    @Override
    public String toString() {
        return "EventIndexResponse{" +
                "paginatedList=" + paginatedList +
                ", paginationMetadata=" + paginationMetadata +
                '}';
    }
}
