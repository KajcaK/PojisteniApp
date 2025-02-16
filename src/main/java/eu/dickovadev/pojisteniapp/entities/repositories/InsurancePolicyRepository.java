package eu.dickovadev.pojisteniapp.entities.repositories;

import eu.dickovadev.pojisteniapp.entities.InsurancePolicyEntity;
import eu.dickovadev.pojisteniapp.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface InsurancePolicyRepository extends CrudRepository<InsurancePolicyEntity, Long> {

    Optional<InsurancePolicyEntity> findByPolicyId(long policyId);

    Optional<InsurancePolicyEntity> findByPolicyHolder(UserEntity policyHolder);

    Optional<InsurancePolicyEntity> findByInsuredUser(UserEntity insuredUser);

    void deleteByPolicyHolderOrInsuredUser(UserEntity policyholder, UserEntity insuredUser);

    Set<InsurancePolicyEntity> findByPolicyHolderOrInsuredUser(UserEntity policyHolder, UserEntity insuredUser);
}
