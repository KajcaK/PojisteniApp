package eu.dickovadev.pojisteniapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import eu.dickovadev.pojisteniapp.models.enums.PolicyType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class PolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long policyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyType type;

    @Column(nullable = false)
    private long coverageAmount;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_holder_id", nullable = false)
    private UserEntity policyHolder;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insured_user_id", nullable = false)
    private UserEntity insuredUser;

    @JsonManagedReference
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<EventEntity> events = new HashSet<>();

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

    public Set<EventEntity> getEvents() {
        return events;
    }

    public void setEvents(Set<EventEntity> events) {
        this.events = events;
    }

    // endregion

    @Override
    public String toString() {
        return "PolicyEntity{" +
                "policyId=" + policyId +
                ", policyType=" + type +
                ", policyHolder=" + policyHolder.getUserId() + " " + policyHolder.getEmail() +
                ", insuredUser=" + insuredUser.getUserId() + " " + insuredUser.getEmail() +
                '}';
    }
}
