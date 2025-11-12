package eu.dickovadev.pojisteniapp.models.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Zadané heslo neodpovídá původnímu heslu.");
    }
}
