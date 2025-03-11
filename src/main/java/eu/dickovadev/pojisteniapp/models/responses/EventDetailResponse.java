package eu.dickovadev.pojisteniapp.models.responses;

import eu.dickovadev.pojisteniapp.models.dto.EventDTO;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;

import java.util.Arrays;

public class EventDetailResponse {
    EventDTO event;
    PolicyDTO policy;

    public EventDetailResponse(EventDTO event, PolicyDTO policy) {
        this.event = event;
        this.policy = policy;
    }

    public EventDTO getEvent() {
        return event;
    }

    public PolicyDTO getPolicy() {
        return policy;
    }

    @Override
    public String toString() {
        return "EventDetailResponse{" +
                "event=" + event +
                ", policy=" + policy +
                '}';
    }
}
