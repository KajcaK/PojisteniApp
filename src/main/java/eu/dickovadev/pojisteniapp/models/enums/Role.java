package eu.dickovadev.pojisteniapp.models.enums;

public enum Role {
    ROLE_ADMIN,
    ROLE_INSURED,
    ROLE_POLICYHOLDER,
    ROLE_REGISTERED;

    public String getFormattedRole() {
        switch (this) {
            case ROLE_ADMIN:
                return "Admin";
            case ROLE_INSURED:
                return "Pojištěný";
            case ROLE_POLICYHOLDER:
                return "Pojistník";
            case ROLE_REGISTERED:
                return "Registrovaný";
            default:
                return "Unknown Role";
        }
    }
}