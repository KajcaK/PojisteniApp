package eu.dickovadev.pojisteniapp.repositories;

import eu.dickovadev.pojisteniapp.entities.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface AuditLogRepository extends CrudRepository<AuditLog, Long> {

    Page<AuditLog> findAll(Pageable pageable);
}
