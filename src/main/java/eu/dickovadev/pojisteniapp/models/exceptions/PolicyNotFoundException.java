package eu.dickovadev.pojisteniapp.models.exceptions;

public class PolicyNotFoundException extends RuntimeException {
    public PolicyNotFoundException() {
        super("Pojistka nenalezena.");
    }
}
