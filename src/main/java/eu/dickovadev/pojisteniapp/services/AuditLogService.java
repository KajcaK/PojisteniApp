package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.entities.AuditLog;
import eu.dickovadev.pojisteniapp.repositories.AuditLogRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(String actionType, String entityName, long entityId, String description) {
        try {
            AuditLog log = new AuditLog();
            // TODO: add performedBy later
            log.setActionType(actionType);
            log.setEntityName(entityName);
            log.setEntityId(entityId);
            log.setTimestamp(LocalDateTime.now());
            log.setDescription(description);
            auditLogRepository.save(log);
        } catch (Exception ex) {
            LoggerFactory.getLogger(AuditLogService.class)
                    .warn("Failed to persist audit log: [{} {} {}]", actionType, entityName, entityId, ex);
        }
    }
}
