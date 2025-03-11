package eu.dickovadev.pojisteniapp.models.responses;

import eu.dickovadev.pojisteniapp.models.dto.AuditLogDTO;

import java.util.List;
import java.util.Map;

public class AuditLogResponse {

    private List<AuditLogDTO> auditLogDTOList;
    private Map<String, Integer> paginationMetadata;

    public AuditLogResponse(List<AuditLogDTO> auditLogDTOList, Map<String, Integer> paginationMetadata) {
        this.auditLogDTOList = auditLogDTOList;
        this.paginationMetadata = paginationMetadata;
    }

    public List<AuditLogDTO> getAuditLogDTOList() {
        return auditLogDTOList;
    }

    public Map<String, Integer> getPaginationMetadata() {
        return paginationMetadata;
    }

    @Override
    public String toString() {
        return "AuditLogResponse{" +
                "auditLogDTOList=" + auditLogDTOList +
                ", paginationMetadata=" + paginationMetadata +
                '}';
    }
}
