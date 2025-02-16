package eu.dickovadev.pojisteniapp.models.services;

import eu.dickovadev.pojisteniapp.entities.InsurancePolicyEntity;
import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.entities.repositories.InsurancePolicyRepository;
import eu.dickovadev.pojisteniapp.entities.repositories.UserRepository;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.exceptions.InvalidPolicyDatesException;
import eu.dickovadev.pojisteniapp.models.exceptions.PolicyNotFoundException;
import eu.dickovadev.pojisteniapp.models.exceptions.UserNotFoundException;
import eu.dickovadev.pojisteniapp.models.mappers.PolicyMapper;
import eu.dickovadev.pojisteniapp.models.mappers.UserMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class PolicyServiceImpl implements PolicyService{

    @Autowired
    private InsurancePolicyRepository policyRepository;

    @Autowired
    private PolicyMapper policyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<PolicyDTO> getAll() {
        return StreamSupport.stream(policyRepository.findAll().spliterator(), false)
                .map(i -> policyMapper.toDTO(i))
                .toList();
    }

    @Override
    public PolicyDTO getById(long policyId) {
        InsurancePolicyEntity fetchedPolicy = getPolicyByIdOrThrow(policyId);

        return policyMapper.toDTO(fetchedPolicy);
    }

    @Override
    public void edit(PolicyDTO policy) {
        InsurancePolicyEntity fetchedPolicy = getPolicyByIdOrThrow(policy.getPolicyId());

        // set policyholder from form
        UserEntity policyHolderEntity = userMapper.toEntity(policy.getPolicyHolder());
        fetchedPolicy.setPolicyHolder(policyHolderEntity);

        // set insuredUser from userId from form
        UserEntity insuredUserEntity = userRepository.findById(policy.getInsuredUser().getUserId())
                .orElseThrow(UserNotFoundException::new);
        fetchedPolicy.setInsuredUser(insuredUserEntity);

        // Ensure startDate is before endDate
        if (policy.getEndDate().isBefore(policy.getStartDate())) {
            throw new InvalidPolicyDatesException();
        }

        policyMapper.updateInsurancePolicyEntity(policy, fetchedPolicy);
        policyRepository.save(fetchedPolicy);
    }

    @Override
    public void remove(long policyId) {
        InsurancePolicyEntity fetchedPolicy = getPolicyByIdOrThrow(policyId);
        policyRepository.delete(fetchedPolicy);
    }

    @Override
    public void create(
            @Valid @ModelAttribute PolicyDTO policyDTO
    ) {

        // Convert the PolicyDTO to an InsurancePolicyEntity using the mapper
        InsurancePolicyEntity newPolicy = policyMapper.toEntity(policyDTO);

        // Ensure startDate is before endDate
        if (newPolicy.getEndDate().isBefore(newPolicy.getStartDate())) {
            throw new InvalidPolicyDatesException();
        }

        // Save the new policy
        InsurancePolicyEntity savedPolicy = policyRepository.save(newPolicy);

        // Set the generated ID for redirect
        policyDTO.setPolicyId(savedPolicy.getPolicyId());

    }

    private InsurancePolicyEntity getPolicyByIdOrThrow(long policyId){
        return policyRepository.findById(policyId).orElseThrow(PolicyNotFoundException::new);
    }

    @Override
    public long getPolicyHolderIdByPolicyId(long policyId) {
        InsurancePolicyEntity policy = getPolicyByIdOrThrow(policyId);
        return policy.getPolicyHolder().getUserId();
    }
}
