package eu.dickovadev.pojisteniapp.models.services;

import eu.dickovadev.pojisteniapp.models.dto.UserProfileDTO;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleFilterServiceImpl implements RoleFilterService{

    // Method to filter user profiles based on allowed roles
    public List<UserProfileDTO> filterUsersByRole(List<UserProfileDTO> userList, List<Role> allowedRoles) {
        if (userList == null || allowedRoles == null || allowedRoles.isEmpty()) {
            return Collections.emptyList(); // Return empty if no users or roles provided
        }

        return userList.stream()
                .filter(userProfile -> allowedRoles.contains(userProfile.getRole()))
                .collect(Collectors.toList());
    }

    // Method to filter users with specific roles (admins excluded)
    public List<UserProfileDTO> filterUsersExcludingAdmins(List<UserProfileDTO> userList) {
        // Define roles allowed for users
        List<Role> allowedRoles = List.of(Role.ROLE_POJISTENEC, Role.ROLE_POJISTNIK, Role.ROLE_USER);
        return filterUsersByRole(userList, allowedRoles);
    }
}