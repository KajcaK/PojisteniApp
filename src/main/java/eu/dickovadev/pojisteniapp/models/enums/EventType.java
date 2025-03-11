package eu.dickovadev.pojisteniapp.models.enums;

public enum EventType {
    // PROPERTY_INSURANCE
    FIRE,
    FLOOD,
    // LIFE_INSURANCE
    DEATH,
    SERIOUS_ILLNESS,
    // ACCIDENT_INSURANCE
    ACCIDENT,
    PERMANENT_INJURY,
    // TRAVEL_INSURANCE
    FLIGHT_DELAY,
    LUGGAGE_LOSS,
    // VEHICLE_INSURANCE
    CAR_ACCIDENT,
    DAMAGE_TO_THIRD_PARTY_PROPERTY,
    // COLLISION_INSURANCE
    VEHICLE_DAMAGE,
    VEHICLE_THEFT,
    // LIABILITY_INSURANCE
    DAMAGE_TO_THIRD_PARTY_HEALTH,
    VANDALISM,
    // PET_INSURANCE
    PET_INJURY,
    PET_ILLNESS;

    public String getFormattedEventType() {
        switch (this) {
            case FIRE:
                return "Požár";
            case FLOOD:
                return "Povodeň";
            case DEATH:
                return "Umrtí";
            case SERIOUS_ILLNESS:
                return "Vážná nemoc";
            case ACCIDENT:
                return "Úraz";
            case PERMANENT_INJURY:
                return "Travelé následky";
            case FLIGHT_DELAY:
                return "Zpoždění letu";
            case LUGGAGE_LOSS:
                return "Ztráta zavazadel";
            case CAR_ACCIDENT:
                return "Autonehoda";
            case DAMAGE_TO_THIRD_PARTY_PROPERTY:
                return "Poškození cizího majetku";
            case VEHICLE_DAMAGE:
                return "Poškození vozidla";
            case VEHICLE_THEFT:
                return "Krádež vozidla";
            case DAMAGE_TO_THIRD_PARTY_HEALTH:
                return "Poškození cizího zdraví";
            case VANDALISM:
                return "Vandalismus";
            case PET_INJURY:
                return "Úraz zvířete";
            case PET_ILLNESS:
                return "Nemoc zvířete";
            default:
                return "Neznámý typ události";
        }
    }
}
