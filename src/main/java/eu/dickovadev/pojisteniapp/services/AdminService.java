package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.models.responses.AdminUsersResponse;
import eu.dickovadev.pojisteniapp.models.responses.AuditLogResponse;
import eu.dickovadev.pojisteniapp.models.responses.NullUsersResponse;
import eu.dickovadev.pojisteniapp.models.responses.StatisticsResponse;

public interface AdminService {

    NullUsersResponse getPaginatedNullUsers(int page, int pageSize);

    AdminUsersResponse getPaginatedAdminUsers(int page, int pageSize);

    AuditLogResponse getAuditLogs(int page, int pageSize);

    void setAdminRole(long userId);

    void removeAdminRole(long userId);
}
