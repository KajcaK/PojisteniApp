package eu.dickovadev.pojisteniapp.models.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Uživatel nenalezen.");
    }
}
