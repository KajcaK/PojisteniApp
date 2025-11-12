package eu.dickovadev.pojisteniapp.models.responses;

import eu.dickovadev.pojisteniapp.models.dto.EventDTO;
import eu.dickovadev.pojisteniapp.models.enums.EventStatus;
import eu.dickovadev.pojisteniapp.models.enums.EventType;

import java.util.Arrays;
import java.util.List;

public class EventEditResponse {
    EventDTO event;
    List<EventType> availableEventTypes;
    EventStatus[] eventStatuses;

    public EventEditResponse(EventDTO event, List<EventType> availableEventTypes, EventStatus[] eventStatuses) {
        this.event = event;
        this.availableEventTypes = availableEventTypes;
        this.eventStatuses = eventStatuses;
    }

    public EventDTO getEvent() {
        return event;
    }

    public List<EventType> getAvailableEventTypes() {
        return availableEventTypes;
    }

    public EventStatus[] getEventStatuses() {
        return eventStatuses;
    }

    @Override
    public String toString() {
        return "EventEditResponse{" +
                "event=" + event +
                ", availableEventTypes=" + availableEventTypes +
                ", eventStatuses=" + Arrays.toString(eventStatuses) +
                '}';
    }
}
