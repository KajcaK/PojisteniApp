package eu.dickovadev.pojisteniapp.models.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import eu.dickovadev.pojisteniapp.models.enums.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

public class PolicyDTO {
    private long policyId;

    private PolicyType type;

    @NotNull(message = "Vyplňte částku")
    private long coverageAmount;

    @NotBlank(message = "Vyplňte předmět pojištění")
    private String subject;

    @NotNull(message = "Vyberte datum")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "Vyberte datum")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "Vyberte pojistníka")
    private PolicyUserDTO policyHolder;

    private PolicyUserDTO insuredUser;

    private boolean sameUser;

    @JsonManagedReference
    private Set<EventDTO> events;

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

    public long getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(long coverageAmount) {
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

    public PolicyUserDTO getPolicyHolder() {
        return policyHolder;
    }

    public void setPolicyHolder(PolicyUserDTO policyHolder) {
        this.policyHolder = policyHolder;
    }

    public PolicyUserDTO getInsuredUser() {
        return insuredUser;
    }

    public void setInsuredUser(PolicyUserDTO insuredUser) {
        this.insuredUser = insuredUser;
    }

    public Set<EventDTO> getEvents() {
        return events;
    }

    public void setEvents(Set<EventDTO> events) {
        this.events = events;
    }

    public boolean isSameUser() {
        return sameUser;
    }

    public void setSameUser(boolean sameUser) {
        this.sameUser = sameUser;
    }
    //endregion

    @Override
    public String toString() {
        return "PolicyDTO{" +
                "policyId=" + policyId +
                ", policyType=" + type +
                ", policyHolder=" + policyHolder.getUserId() + " " + policyHolder.getEmail() +
                ", insuredUser=" + insuredUser.getUserId() + " " + insuredUser.getEmail() +
                '}';
    }
}
