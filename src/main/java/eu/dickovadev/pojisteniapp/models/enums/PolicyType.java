package eu.dickovadev.pojisteniapp.models.enums;

public enum PolicyType {
    LIFE_INSURANCE,
    ACCIDENT_INSURANCE,
    TRAVEL_INSURANCE,
    VEHICLE_INSURANCE,
    COLLISION_INSURANCE,
    PROPERTY_INSURANCE,
    LIABILITY_INSURANCE,
    PET_INSURANCE;

    public String getFormattedPolicyType() {
        switch (this) {
            case LIFE_INSURANCE:
                return "Životní pojištění";
            case ACCIDENT_INSURANCE:
                return "Úrazové pojištění";
            case TRAVEL_INSURANCE:
                return "Cestovní pojištění";
            case VEHICLE_INSURANCE:
                return "Povinné ručení";
            case COLLISION_INSURANCE:
                return "Havarijní pojištění";
            case PROPERTY_INSURANCE:
                return "Pojištění majetku";
            case LIABILITY_INSURANCE:
                return "Pojištění odpovědnosti";
            case PET_INSURANCE:
                return "Pojištění mazlíčka";
            default:
                return "Neznámý typ pojištění";
        }
    }

}
