package eu.dickovadev.pojisteniapp.models.exceptions;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException() {
        super("Událost nenalezena.");
    }
}
