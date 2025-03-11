package eu.dickovadev.pojisteniapp.models.exceptions;

public class PasswordsDoNotEqualException extends RuntimeException {
    public PasswordsDoNotEqualException() {
        super("Hesla se neshodují.");
    }
}
