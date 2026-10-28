package com.motopecas.padrecicero.motopecas.padrecicero.exception.stockExceptions;

public class TransactionAlreadyAddedException extends RuntimeException {
    public TransactionAlreadyAddedException(String message) {
        super(message);
    }
}
