package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.responses.UserDetailResponse;
import eu.dickovadev.pojisteniapp.models.responses.UserIndexResponse;

import java.util.List;

public interface UserService {

    long create(UserDTO user);

    void editByAdmin(UserDTO user, long userId);

    void editByCustomer(UserDTO user, long userId);

    void remove(long userId);

    List<UserDTO> getUsersListExcludingAdmins();

    UserDTO getByIdOrElseThrow(long userId);

    UserIndexResponse getPaginatedUsers(String query, String searchField, int page, int pageSize);

    UserDetailResponse getUserWithPaginatedPolicies(long userId, int page, int pageSize);

    UserDTO getUserEditData(long userId, UserDTO user);

    UserEntity getEntityByIdOrThrow(long userId);
}
