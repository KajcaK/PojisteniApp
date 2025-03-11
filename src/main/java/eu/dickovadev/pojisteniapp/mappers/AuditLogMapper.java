package eu.dickovadev.pojisteniapp.mappers;

import eu.dickovadev.pojisteniapp.entities.AuditLog;
import eu.dickovadev.pojisteniapp.models.dto.AuditLogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    AuditLog toEntity(AuditLogDTO source);

    AuditLogDTO toDTO(AuditLog source);

    void updateAuditLogDTO(AuditLogDTO source, @MappingTarget AuditLogDTO target);

    void updateAuditLogEntity(AuditLogDTO source, @MappingTarget AuditLog target);
}
