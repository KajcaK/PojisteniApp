package eu.dickovadev.pojisteniapp.models.responses;

import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;

import java.util.List;
import java.util.Map;

public class PolicyIndexResponse {
    private List<PolicyDTO> paginatedList;
    private Map<String, Integer> paginationMetadata;

    public PolicyIndexResponse(List<PolicyDTO> paginatedList, Map<String, Integer> paginationMetadata) {
        this.paginatedList = paginatedList;
        this.paginationMetadata = paginationMetadata;
    }

    public List<PolicyDTO> getPaginatedList() {
        return paginatedList;
    }

    public Map<String, Integer> getPaginationMetadata() {
        return paginationMetadata;
    }

    @Override
    public String toString() {
        return "PolicyIndexResponse{" +
                "paginatedList=" + paginatedList +
                ", paginationMetadata=" + paginationMetadata +
                '}';
    }
}

