package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.entities.PolicyEntity;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.responses.PolicyCreateResponse;
import eu.dickovadev.pojisteniapp.models.responses.PolicyDetailResponse;
import eu.dickovadev.pojisteniapp.models.responses.PolicyEditResponse;
import eu.dickovadev.pojisteniapp.models.responses.PolicyIndexResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface PolicyService {

    void create(PolicyDTO policy, long userId);

    long edit(long policyId, PolicyDTO policy);

    long remove(long policyId);

    Page<PolicyDTO> getAll(Pageable pageable);

    PolicyDTO getByIdOrElseThrow(long policyId);

    PolicyEntity getEntityByIdOrThrow(long policyId);

    PolicyDetailResponse getPolicyWithPaginatedEvents(long policyId, int page, int pageSize, Authentication authentication);

    PolicyEditResponse getPolicyEditData(long policyId);

    PolicyIndexResponse getPaginatedPolicies(String query, String searchField, int page, int pageSize);

    PolicyCreateResponse getPolicyCreateData(long userId);

}
