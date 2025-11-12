package eu.dickovadev.pojisteniapp.mappers;

import eu.dickovadev.pojisteniapp.entities.EventEntity;
import eu.dickovadev.pojisteniapp.models.dto.EventDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "policyId", source = "policy.policyId")  // Map the info from Policy object
    @Mapping(target = "policyType", source = "policy.type")
    @Mapping(target = "policyHolderId", source = "policy.policyHolder.userId")
    @Mapping(target = "insuredUserId", source = "policy.insuredUser.userId")
    EventDTO toDTO(EventEntity source);

    @Mapping(target = "policy", ignore = true)
    EventEntity toEntity(EventDTO source);

    void updateEventDTO(EventDTO source, @MappingTarget EventDTO target);

    void updateEventEntity(EventDTO source, @MappingTarget EventEntity target);
}


