package eu.dickovadev.pojisteniapp.models.responses;

import eu.dickovadev.pojisteniapp.models.dto.UserDTO;

import java.util.List;
import java.util.Map;

public class UserIndexResponse {
    private List<UserDTO> paginatedList;
    private Map<String, Integer> paginationMetadata;

    public UserIndexResponse(List<UserDTO> paginatedList, Map<String, Integer> paginationMetadata) {
        this.paginatedList = paginatedList;
        this.paginationMetadata = paginationMetadata;
    }

    public List<UserDTO> getPaginatedList() {
        return paginatedList;
    }

    public Map<String, Integer> getPaginationMetadata() {
        return paginationMetadata;
    }

    @Override
    public String toString() {
        return "UserIndexResponse{" +
                "paginatedList=" + paginatedList +
                ", paginationMetadata=" + paginationMetadata +
                '}';
    }
}
