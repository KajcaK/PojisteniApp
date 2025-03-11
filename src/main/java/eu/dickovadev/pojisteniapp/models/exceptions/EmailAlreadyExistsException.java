package eu.dickovadev.pojisteniapp.models.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("Tento email již patří jinému uživateli.");
    }
}
