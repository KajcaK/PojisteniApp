package eu.dickovadev.pojisteniapp.models.responses;

import eu.dickovadev.pojisteniapp.models.dto.UserDTO;

import java.util.List;
import java.util.Map;

public class AdminUsersResponse {
    private List<UserDTO> adminUsersList;
    private Map<String, Integer> paginationMetadata;

    public AdminUsersResponse(List<UserDTO> adminUsersList, Map<String, Integer> paginationMetadata) {
        this.adminUsersList = adminUsersList;
        this.paginationMetadata = paginationMetadata;
    }

    public List<UserDTO> getAdminUsersList() {
        return adminUsersList;
    }

    public Map<String, Integer> getPaginationMetadata() {
        return paginationMetadata;
    }

    @Override
    public String toString() {
        return "AdminUsersResponse{" +
                "adminUsersList=" + adminUsersList +
                ", paginationMetadata=" + paginationMetadata +
                '}';
    }
}
