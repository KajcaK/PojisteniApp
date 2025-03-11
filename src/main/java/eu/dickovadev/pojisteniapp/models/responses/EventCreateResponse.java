package eu.dickovadev.pojisteniapp.models.responses;

import eu.dickovadev.pojisteniapp.models.enums.EventStatus;
import eu.dickovadev.pojisteniapp.models.enums.EventType;

import java.util.Arrays;
import java.util.List;

public class EventCreateResponse {
    List<EventType> availableEventTypes;
    EventStatus[] eventStatuses;

    public EventCreateResponse(List<EventType> availableEventTypes, EventStatus[] eventStatuses) {
        this.availableEventTypes = availableEventTypes;
        this.eventStatuses = eventStatuses;
    }

    public List<EventType> getAvailableEventTypes() {
        return availableEventTypes;
    }

    public EventStatus[] getEventStatuses() {
        return eventStatuses;
    }

    @Override
    public String toString() {
        return "EventCreateResponse{" +
                "availableEventTypes=" + availableEventTypes +
                ", eventStatuses=" + Arrays.toString(eventStatuses) +
                '}';
    }
}
