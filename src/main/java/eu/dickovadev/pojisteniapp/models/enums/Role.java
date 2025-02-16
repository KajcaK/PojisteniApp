package eu.dickovadev.pojisteniapp.models.enums;

public enum Role {
    ROLE_ADMIN,
    ROLE_POJISTENEC,
    ROLE_POJISTNIK,
    ROLE_USER;

    public String getFormattedRole() {
        switch (this) {
            case ROLE_ADMIN:
                return "Admin";
            case ROLE_POJISTENEC:
                return "Pojištěnec";
            case ROLE_POJISTNIK:
                return "Pojistník";
            case ROLE_USER:
                return "User";
            default:
                return "Unknown Role";
        }
    }
}