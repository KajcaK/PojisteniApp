package eu.dickovadev.pojisteniapp.models.exceptions;

public class AmountPaidExceedException extends RuntimeException {
    public AmountPaidExceedException() {
        super("Vyplacená částka nemůže překročit původní částku");
    }
}
