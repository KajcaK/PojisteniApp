package eu.dickovadev.pojisteniapp.models.exceptions;

public class InvalidPolicyDatesException extends RuntimeException {
    public InvalidPolicyDatesException() {
        super("Konečné datum nemůže být před začátkem");
    }
}
