package eu.dickovadev.pojisteniapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.dickovadev.pojisteniapp.models.enums.EventStatus;
import eu.dickovadev.pojisteniapp.models.enums.EventType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long eventId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "policy_id", nullable = false)
    private PolicyEntity policy;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String eventDescription;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    private long originalClaimAmount;

    private long amountPaid;

    // region: Getters and Setters
    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public PolicyEntity getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyEntity policy) {
        this.policy = policy;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public long getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(long amountPaid) {
        this.amountPaid = amountPaid;
    }

    public long getOriginalClaimAmount() {
        return originalClaimAmount;
    }

    public void setOriginalClaimAmount(long originalClaimAmount) {
        this.originalClaimAmount = originalClaimAmount;
    }

    // endregion

    @Override
    public String toString() {
        return "EventEntity{" +
                "eventId=" + eventId +
                ", eventType=" + eventType +
                ", policyId=" + policy.getPolicyHolder().getUserId() +
                ", policyHolderId=" + policy.getPolicyHolder().getUserId() +
                ", insuredUserId=" + policy.getInsuredUser().getUserId() +
                '}';
    }
}
