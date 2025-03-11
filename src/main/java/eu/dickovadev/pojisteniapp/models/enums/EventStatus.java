package eu.dickovadev.pojisteniapp.models.enums;

public enum EventStatus {
    PENDING_APPROVAL,
    NOT_PAID,
    FULLY_PAID,
    PARTIALLY_PAID,
    CANCELED,
    UNDER_INVESTIGATION,
    CLOSED;

    public String getFormattedEventStatus() {
        switch (this) {
            case PENDING_APPROVAL:
                return "čeká na schválení";
            case NOT_PAID:
                return "nevyplaceno";
            case FULLY_PAID:
                return "vyplaceno v celové hodnotě";
            case PARTIALLY_PAID:
                return "vyplaceno částečně";
            case CANCELED:
                return "zrušeno";
            case UNDER_INVESTIGATION:
                return "probíhá vyšetřování";
            case CLOSED:
                return "pojistná událost uzavřena";
            default:
                return "neznámý typ statusu události";
        }
    }
}
