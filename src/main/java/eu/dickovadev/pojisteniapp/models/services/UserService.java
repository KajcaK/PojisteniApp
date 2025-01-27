package eu.dickovadev.pojisteniapp.models.services;

import eu.dickovadev.pojisteniapp.models.dto.UserProfileDTO;

import java.util.List;

public interface UserService {

    List<UserProfileDTO> getAll();

    UserProfileDTO getById(long userId);

    void edit(UserProfileDTO user);

    void remove(long userId);

    void create(UserProfileDTO user);
}
