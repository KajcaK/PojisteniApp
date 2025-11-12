package eu.dickovadev.pojisteniapp.models.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Přístup zamítnut.");
    }
}
