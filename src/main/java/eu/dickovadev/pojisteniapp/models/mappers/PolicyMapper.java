package eu.dickovadev.pojisteniapp.models.mappers;

import eu.dickovadev.pojisteniapp.entities.InsurancePolicyEntity;
import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PolicyMapper {

    InsurancePolicyEntity toEntity(PolicyDTO source);

    PolicyDTO toDTO(InsurancePolicyEntity source);

    void updatePolicyDTO(PolicyDTO source, @MappingTarget PolicyDTO target);

    void updateInsurancePolicyEntity(PolicyDTO source, @MappingTarget InsurancePolicyEntity target);

    UserEntity userDTOToEntity(UserDTO source);
    UserDTO userEntityToDTO(UserEntity source);
}
