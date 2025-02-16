package eu.dickovadev.pojisteniapp.entities;

import eu.dickovadev.pojisteniapp.models.enums.PolicyType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class InsurancePolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long policyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyType type;

    @Column(nullable = false)
    private String coverageAmount;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "policy_holder_id", nullable = false)
    private UserEntity policyHolder;

    @ManyToOne
    @JoinColumn(name = "insured_user_id")
    private UserEntity insuredUser;

    // region: Getters and Setters
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

    public UserEntity getPolicyHolder() {
        return policyHolder;
    }

    public void setPolicyHolder(UserEntity policyHolder) {
        this.policyHolder = policyHolder;
    }

    public UserEntity getInsuredUser() {
        return insuredUser;
    }

    public void setInsuredUser(UserEntity insuredUser) {
        this.insuredUser = insuredUser;
    }
    // endregion

}
