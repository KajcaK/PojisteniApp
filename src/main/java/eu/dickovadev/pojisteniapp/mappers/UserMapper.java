package eu.dickovadev.pojisteniapp.mappers;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import org.mapstruct.Mapper;

import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserDTO source);

    UserDTO toDTO(UserEntity source);

    void updateUserDTO(UserDTO source, @MappingTarget UserDTO target);

    void updateUserEntity(UserDTO source, @MappingTarget UserEntity target);
}
