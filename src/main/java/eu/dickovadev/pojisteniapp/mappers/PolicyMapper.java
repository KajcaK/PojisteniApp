package eu.dickovadev.pojisteniapp.mappers;

import eu.dickovadev.pojisteniapp.entities.PolicyEntity;
import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.dto.PolicyUserDTO;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PolicyMapper {

    @Mapping(target = "policyHolder.userId", source = "policyHolder.userId")
    @Mapping(target = "policyHolder.firstName", source = "policyHolder.firstName")
    @Mapping(target = "policyHolder.lastName", source = "policyHolder.lastName")
    @Mapping(target = "insuredUser.userId", source = "insuredUser.userId")
    @Mapping(target = "insuredUser.firstName", source = "insuredUser.firstName")
    @Mapping(target = "insuredUser.lastName", source = "insuredUser.lastName")
    PolicyDTO toDTO(PolicyEntity source);

    @Mapping(target = "policyHolder.userId", source = "policyHolder.userId")
    @Mapping(target = "policyHolder.firstName", source = "policyHolder.firstName")
    @Mapping(target = "policyHolder.lastName", source = "policyHolder.lastName")
    @Mapping(target = "insuredUser.userId", source = "insuredUser.userId")
    @Mapping(target = "insuredUser.firstName", source = "insuredUser.firstName")
    @Mapping(target = "insuredUser.lastName", source = "insuredUser.lastName")
    PolicyEntity toEntity(PolicyDTO source);

    void updatePolicyDTO(PolicyDTO source, @MappingTarget PolicyDTO target);

    void updatePolicyEntity(PolicyDTO source, @MappingTarget PolicyEntity target);

    @Mapping(target = "userId", ignore = true)
        // Prevents ID overwrite
    UserEntity userDTOToEntity(UserDTO source, @MappingTarget UserEntity target);

    @Mapping(target = "userId", ignore = true)
        // Prevents ID overwrite
    UserEntity policyUserDTOToEntity(PolicyUserDTO source, @MappingTarget UserEntity target);
}
