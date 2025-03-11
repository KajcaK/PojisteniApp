package eu.dickovadev.pojisteniapp.repositories;

import eu.dickovadev.pojisteniapp.entities.PolicyEntity;
import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.enums.PolicyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PolicyRepository extends CrudRepository<PolicyEntity, Long> {
    Optional<PolicyEntity> findById(long policyId);

    void deleteByPolicyHolderOrInsuredUser(UserEntity policyholder, UserEntity insuredUser);

    Set<PolicyEntity> findByPolicyHolderOrInsuredUser(UserEntity policyHolder, UserEntity insuredUser);

    @Query("SELECT p FROM PolicyEntity p LEFT JOIN FETCH p.policyHolder LEFT JOIN FETCH p.insuredUser WHERE p.policyId = :policyId")
    Optional<PolicyEntity> findByIdWithUserDetails(@Param("policyId") long policyId);

    @Query("SELECT p FROM PolicyEntity p WHERE p.policyHolder.email = :email OR p.insuredUser.email = :email")
    Page<PolicyEntity> findByEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT p FROM PolicyEntity p WHERE p.policyHolder.userId = :userId OR p.insuredUser.userId = :userId")
    Page<PolicyEntity> findByUserId(@Param("userId") long userId, Pageable pageable);

    Page<PolicyEntity> findAll(Pageable pageable);

    //Statistics
    @Query("SELECT COUNT(p) FROM PolicyEntity p")
    Long countTotalPolicies();

    @Query("SELECT COUNT(p) FROM PolicyEntity p WHERE p.type = :type")
    Long countTotalPoliciesByType(@Param("type") PolicyType type);

    @Query("SELECT COUNT(p) FROM PolicyEntity p WHERE p.startDate <= CURRENT_DATE AND p.endDate >= CURRENT_DATE")
    Long countTotalActivePolicies();

    @Query("SELECT AVG(p.coverageAmount) FROM PolicyEntity p")
    Double averagePolicyCoverage();

    @Query("SELECT AVG(p.coverageAmount) FROM PolicyEntity p WHERE p.type = :type")
    Double averagePolicyCoverageByType(@Param("type") PolicyType type);

    @Query("SELECT AVG(DATEDIFF(p.endDate, p.startDate)) FROM PolicyEntity p")
    Double averagePolicyDuration();

}

