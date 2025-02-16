package eu.dickovadev.pojisteniapp.models.exceptions;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(){
        super("Tento email již patří jinému uživateli.");
    }
}
