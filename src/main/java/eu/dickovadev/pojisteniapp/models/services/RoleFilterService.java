package eu.dickovadev.pojisteniapp.models.services;

import eu.dickovadev.pojisteniapp.models.dto.UserProfileDTO;
import eu.dickovadev.pojisteniapp.models.enums.Role;

import java.util.List;

public interface RoleFilterService {

    List<UserProfileDTO> filterUsersByRole(List<UserProfileDTO> userList, List<Role> allowedRoles);

    List<UserProfileDTO> filterUsersExcludingAdmins(List<UserProfileDTO> userList);
}
