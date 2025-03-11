package eu.dickovadev.pojisteniapp.models.responses;

import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.dto.PolicyUserDTO;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.enums.PolicyType;

import java.util.Arrays;
import java.util.List;

public class PolicyEditResponse {
    private PolicyDTO policy;
    private List<UserDTO> users;
    private PolicyType[] policyTypes;
    private long userId;
    private PolicyUserDTO insuredUser;

    public PolicyEditResponse(PolicyDTO policy, List<UserDTO> users, long userId, PolicyUserDTO insuredUser) {
        this.policy = policy;
        this.users = users;
        this.policyTypes = PolicyType.values();
        this.userId = userId;
        this.insuredUser = insuredUser;
    }

    //region: getters and setters
    public PolicyDTO getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyDTO policy) {
        this.policy = policy;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    public PolicyType[] getPolicyTypes() {
        return policyTypes;
    }

    public void setPolicyTypes(PolicyType[] policyTypes) {
        this.policyTypes = policyTypes;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public PolicyUserDTO getInsuredUser() {
        return insuredUser;
    }

    public void setInsuredUser(PolicyUserDTO insuredUser) {
        this.insuredUser = insuredUser;
    }
    //endregion

    @Override
    public String toString() {
        return "PolicyEditResponse{" +
                "policy=" + policy +
                ", users=" + users +
                ", policyTypes=" + Arrays.toString(policyTypes) +
                ", userId=" + userId +
                ", insuredUser=" + insuredUser +
                '}';
    }
}
