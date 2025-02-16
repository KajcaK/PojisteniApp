package eu.dickovadev.pojisteniapp.models.services;

import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleFilterServiceImpl implements RoleFilterService{

    // Method to filter user profiles based on allowed roles
    public List<UserDTO> filterUsersByRole(List<UserDTO> userList, List<Role> allowedRoles) {
        if (userList == null || allowedRoles == null || allowedRoles.isEmpty()) {
            return Collections.emptyList(); // Return empty if no users or roles provided
        }

        // Filter users by matching any of their roles with allowed roles
        return userList.stream()
                .filter(userProfile -> userProfile.getRoles() != null && userProfile.getRoles().stream()
                        .anyMatch(allowedRoles::contains)) // Check if any of the user's roles are in allowedRoles
                .collect(Collectors.toList());
    }

    // Method to filter users with specific roles (admins excluded)
    public List<UserDTO> filterUsersExcludingAdmins(List<UserDTO> userList) {
        // Define roles allowed for users (excluding admin roles)
        List<Role> allowedRoles = List.of(Role.ROLE_POJISTENEC, Role.ROLE_POJISTNIK, Role.ROLE_USER);
        return filterUsersByRole(userList, allowedRoles);
    }
}