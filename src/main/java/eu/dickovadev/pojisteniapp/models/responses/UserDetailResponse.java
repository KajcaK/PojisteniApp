package eu.dickovadev.pojisteniapp.models.responses;

import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;

import java.util.List;
import java.util.Map;

public class UserDetailResponse {

    private UserDTO user;
    private List<PolicyDTO> paginatedPolicies;
    private Map<String, Integer> paginationMetadata;

    public UserDetailResponse(
            UserDTO user,
            List<PolicyDTO> paginatedPolicies,
            Map<String, Integer> paginationMetadata
    ) {
        this.user = user;
        this.paginatedPolicies = paginatedPolicies;
        this.paginationMetadata = paginationMetadata;
    }

    public UserDTO getUser() {
        return user;
    }

    public List<PolicyDTO> getPaginatedPolicies() {
        return paginatedPolicies;
    }

    public Map<String, Integer> getPaginationMetadata() {
        return paginationMetadata;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserDetailResponse{" +
                "user=" + user +
                ", paginatedPolicies=" + paginatedPolicies +
                ", paginationMetadata=" + paginationMetadata +
                '}';
    }
}
