package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.models.enums.EventType;
import eu.dickovadev.pojisteniapp.models.enums.PolicyType;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EventMappingService {

    private final Map<PolicyType, List<EventType>> eventMapping;

    public EventMappingService() {
        eventMapping = new HashMap<>();

        // Initialize the mapping between PolicyType and EventType
        eventMapping.put(PolicyType.PROPERTY_INSURANCE, Arrays.asList(
                EventType.FIRE, EventType.FLOOD
        ));
        eventMapping.put(PolicyType.LIFE_INSURANCE, Arrays.asList(
                EventType.DEATH, EventType.SERIOUS_ILLNESS
        ));
        eventMapping.put(PolicyType.ACCIDENT_INSURANCE, Arrays.asList(
                EventType.ACCIDENT, EventType.PERMANENT_INJURY
        ));
        eventMapping.put(PolicyType.TRAVEL_INSURANCE, Arrays.asList(
                EventType.FLIGHT_DELAY, EventType.LUGGAGE_LOSS
        ));
        eventMapping.put(PolicyType.VEHICLE_INSURANCE, Arrays.asList(
                EventType.CAR_ACCIDENT, EventType.DAMAGE_TO_THIRD_PARTY_PROPERTY
        ));
        eventMapping.put(PolicyType.COLLISION_INSURANCE, Arrays.asList(
                EventType.VEHICLE_DAMAGE, EventType.VEHICLE_THEFT
        ));
        eventMapping.put(PolicyType.LIABILITY_INSURANCE, Arrays.asList(
                EventType.DAMAGE_TO_THIRD_PARTY_HEALTH, EventType.VANDALISM
        ));
        eventMapping.put(PolicyType.PET_INSURANCE, Arrays.asList(
                EventType.PET_INJURY, EventType.PET_ILLNESS
        ));
    }

    // Method to get events for a given policy type
    public List<EventType> getEventsForPolicyType(PolicyType policyType) {
        return eventMapping.getOrDefault(policyType, Collections.emptyList());
    }
}

