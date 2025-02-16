package eu.dickovadev.pojisteniapp.models.exceptions;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(){
        super("Tento email je již registrován. Zkuste jiný.");
    }
}
