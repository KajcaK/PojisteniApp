package eu.dickovadev.pojisteniapp.mappers;

import eu.dickovadev.pojisteniapp.entities.AuditLog;
import eu.dickovadev.pojisteniapp.models.dto.AuditLogDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    AuditLog toEntity(AuditLogDTO source);

    AuditLogDTO toDTO(AuditLog source);

}
