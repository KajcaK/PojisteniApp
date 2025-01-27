package eu.dickovadev.pojisteniapp.models.mappers;

import eu.dickovadev.pojisteniapp.data.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.UserProfileDTO;
import org.mapstruct.Mapper;

import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserProfileDTO source);

    UserProfileDTO toDTO(UserEntity source);

    void updateUserProfileDTO(UserProfileDTO source, @MappingTarget UserProfileDTO target);

    void updateUserEntity(UserProfileDTO source, @MappingTarget UserEntity target);

}
