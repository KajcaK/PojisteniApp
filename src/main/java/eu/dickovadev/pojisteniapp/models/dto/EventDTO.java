package eu.dickovadev.pojisteniapp.models.dto;

import eu.dickovadev.pojisteniapp.models.enums.EventStatus;
import eu.dickovadev.pojisteniapp.models.enums.EventType;
import eu.dickovadev.pojisteniapp.models.enums.PolicyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class EventDTO {

    private long eventId;

    @NotNull(message = "Vyberte datum")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    private EventType eventType;

    @NotBlank(message = "Vyplňte popis pojistné události.")
    private String eventDescription;

    private EventStatus eventStatus;

    private long policyId;

    private PolicyType policyType;

    private long policyHolderId;

    private long insuredUserId;

    private long originalClaimAmount;

    private long amountPaid;

    public long getRemainingClaimAmount() {
        return originalClaimAmount - amountPaid;
    }


    //region: getters and setters
    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
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

    public long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(long policyId) {
        this.policyId = policyId;
    }

    public PolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType(PolicyType policyType) {
        this.policyType = policyType;
    }

    public long getPolicyHolderId() {
        return policyHolderId;
    }

    public void setPolicyHolderId(long policyHolderId) {
        this.policyHolderId = policyHolderId;
    }

    public long getInsuredUserId() {
        return insuredUserId;
    }

    public void setInsuredUserId(long insuredUserId) {
        this.insuredUserId = insuredUserId;
    }

    public long getOriginalClaimAmount() {
        return originalClaimAmount;
    }

    public void setOriginalClaimAmount(long originalClaimAmount) {
        this.originalClaimAmount = originalClaimAmount;
    }

    //endregion

    @Override
    public String toString() {
        return "EventDTO{" +
                "eventId=" + eventId +
                ", eventType=" + eventType +
                ", policyId=" + policyId +
                ", policyHolderId=" + policyHolderId +
                ", insuredUserId=" + insuredUserId +
                '}';
    }
}
