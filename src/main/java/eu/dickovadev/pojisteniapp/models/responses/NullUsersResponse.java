package eu.dickovadev.pojisteniapp.models.responses;

import eu.dickovadev.pojisteniapp.models.dto.UserDTO;

import java.util.List;
import java.util.Map;

public class NullUsersResponse {
    private List<UserDTO> nullUsersList;
    private Map<String, Integer> paginationMetadata;

    public NullUsersResponse(List<UserDTO> nullUsersList, Map<String, Integer> paginationMetadata) {
        this.nullUsersList = nullUsersList;
        this.paginationMetadata = paginationMetadata;
    }

    public List<UserDTO> getNullUsersList() {
        return nullUsersList;
    }

    public Map<String, Integer> getPaginationMetadata() {
        return paginationMetadata;
    }

    @Override
    public String toString() {
        return "NullUsersResponse{" +
                "nullUsersList=" + nullUsersList +
                ", paginationMetadata=" + paginationMetadata +
                '}';
    }
}
