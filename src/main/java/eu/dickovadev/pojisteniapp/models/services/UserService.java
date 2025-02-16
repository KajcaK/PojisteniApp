package eu.dickovadev.pojisteniapp.models.services;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;

import java.util.List;

public interface UserService {

    List<UserDTO> getAll();

    UserDTO getById(long userId);

    void edit(UserDTO user);

    void remove(long userId);

    void create(UserDTO user);

    UserEntity getEntityById(long userId);
}
