package eu.dickovadev.pojisteniapp.repositories;

import eu.dickovadev.pojisteniapp.entities.EventEntity;
import eu.dickovadev.pojisteniapp.models.enums.EventStatus;
import eu.dickovadev.pojisteniapp.models.enums.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface EventRepository extends CrudRepository<EventEntity, Long> {

    @Query("SELECT e FROM EventEntity e WHERE e.policy.policyId = :policyId")
    Page<EventEntity> findByPolicyId(@Param("policyId") long policyId, Pageable pageable);

    @Query("SELECT e FROM EventEntity e WHERE e.eventStatus = :status")
    Page<EventEntity> findByEventStatus(@Param("status") EventStatus status, Pageable pageable);

    @Query("SELECT e FROM EventEntity e WHERE e.eventId = :eventId")
    Page<EventEntity> findByEventId(@Param("eventId") long eventId, Pageable pageable);

    Page<EventEntity> findAll(Pageable pageable);

    //Statistics
    @Query("SELECT COUNT(e) FROM EventEntity e")
    Long countTotalEvents();

    @Query("SELECT COUNT(e) FROM EventEntity e WHERE e.eventStatus = :status")
    Long countTotalByStatus(@Param("status") EventStatus status);

    @Query("SELECT COUNT(e) FROM EventEntity e WHERE e.eventType = :type")
    Long countTotalByType(@Param("type") EventType type);

    @Query("SELECT SUM(e.originalClaimAmount) FROM EventEntity e")
    Long countTotalOriginalClaimAmount();

    @Query("SELECT SUM(e.amountPaid) FROM EventEntity e")
    Long totalAmountPaid();

    @Query("SELECT AVG(e.originalClaimAmount) FROM EventEntity e")
    Double averageEventClaimAmount();
}
