package eu.dickovadev.pojisteniapp.models.responses;

import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.enums.PolicyType;

import java.util.Arrays;
import java.util.List;

public class PolicyCreateResponse {
    private PolicyType[] policyTypes;
    private List<UserDTO> users;
    private String insuredFirstName;
    private String insuredLastName;

    public PolicyCreateResponse(PolicyType[] policyTypes, List<UserDTO> users, String insuredFirstName, String insuredLastName) {
        this.policyTypes = policyTypes;
        this.users = users;
        this.insuredFirstName = insuredFirstName;
        this.insuredLastName = insuredLastName;
    }

    public PolicyType[] getPolicyTypes() {
        return policyTypes;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public String getInsuredFirstName() {
        return insuredFirstName;
    }

    public String getInsuredLastName() {
        return insuredLastName;
    }

    @Override
    public String toString() {
        return "PolicyCreateResponse{" +
                "policyTypes=" + Arrays.toString(policyTypes) +
                ", users=" + users +
                ", insuredFullName='" + insuredFirstName + " " + insuredLastName + '\'' +
                '}';
    }
}
