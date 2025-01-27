package eu.dickovadev.pojisteniapp.models.enums;

public enum Role {
    ROLE_ADMIN,
    ROLE_POJISTENEC,
    ROLE_POJISTNIK,
    ROLE_USER;

    public static Role fromString(String role) {
        switch (role.toUpperCase()) {
            case "ROLE_ADMIN":
                return ROLE_ADMIN;
            case "ROLE_POJISTENEC":
                return ROLE_POJISTENEC;
            case "ROLE_POJISTNIK":
                return ROLE_POJISTNIK;
            case "ROLE_USER":
                return ROLE_USER;
            default:
                throw new IllegalArgumentException("Unexpected value: " + role);
        }
    }

    public String getAuthority() {
        return this.name();
    }

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