package eu.dickovadev.pojisteniapp.models.dto;

import eu.dickovadev.pojisteniapp.models.enums.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class PolicyDTO {
    private long policyId;

    private PolicyType type;

    @NotBlank(message = "Vyplňte částku")
    private String coverageAmount;

    @NotBlank(message = "Vyplňte předmět pojištění")
    private String subject;

    @NotNull(message = "Vyberte datum")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "Vyberte datum")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "Vyberte pojistníka")
    private UserDTO policyHolder;

    private UserDTO insuredUser;

    //region: getters and setters
    public long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(long policyId) {
        this.policyId = policyId;
    }

    public PolicyType getType() {
        return type;
    }

    public void setType(PolicyType type) {
        this.type = type;
    }

    public String getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(String coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public UserDTO getPolicyHolder() {
        return policyHolder;
    }

    public void setPolicyHolder(UserDTO policyHolder) {
        this.policyHolder = policyHolder;
    }

    public UserDTO getInsuredUser() {
        return insuredUser;
    }

    public void setInsuredUser(UserDTO insuredUser) {
        this.insuredUser = insuredUser;
    }
    //endregion
}
