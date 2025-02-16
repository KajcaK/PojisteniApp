package eu.dickovadev.pojisteniapp.models.services;

import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.enums.Role;

import java.util.List;

public interface RoleFilterService {

    List<UserDTO> filterUsersByRole(List<UserDTO> userList, List<Role> allowedRoles);

    List<UserDTO> filterUsersExcludingAdmins(List<UserDTO> userList);
}
