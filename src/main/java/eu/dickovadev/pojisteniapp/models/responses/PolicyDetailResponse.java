package eu.dickovadev.pojisteniapp.models.responses;

import eu.dickovadev.pojisteniapp.models.dto.EventDTO;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;

import java.util.List;
import java.util.Map;

public class PolicyDetailResponse {

    private PolicyDTO policy;
    private long userId;
    private List<EventDTO> paginatedEvents;
    private Map<String, Integer> paginationMetadata;

    public PolicyDetailResponse(PolicyDTO policy, long userId, List<EventDTO> paginatedEvents, Map<String, Integer> paginationMetadata) {
        this.policy = policy;
        this.userId = userId;
        this.paginatedEvents = paginatedEvents;
        this.paginationMetadata = paginationMetadata;
    }

    //region: getters and setters
    public PolicyDTO getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyDTO policy) {
        this.policy = policy;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<EventDTO> getPaginatedEvents() {
        return paginatedEvents;
    }

    public Map<String, Integer> getPaginationMetadata() {
        return paginationMetadata;
    }
    //endregion

    @Override
    public String toString() {
        return "PolicyDetailResponse{" +
                "policy=" + policy +
                ", userId=" + userId +
                ", paginatedEvents=" + paginatedEvents +
                ", paginationMetadata=" + paginationMetadata +
                '}';
    }
}
