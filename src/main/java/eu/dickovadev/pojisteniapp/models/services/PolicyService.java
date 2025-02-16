package eu.dickovadev.pojisteniapp.models.services;

import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;

import java.util.List;

public interface PolicyService {

    List<PolicyDTO> getAll();

    PolicyDTO getById(long policyId);

    void edit(PolicyDTO policy);

    void remove(long policyId);

    void create(PolicyDTO policy);

    long getPolicyHolderIdByPolicyId(long policyId);
}
