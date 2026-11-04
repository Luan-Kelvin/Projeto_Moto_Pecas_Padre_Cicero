package com.motopecas.padrecicero.motopecas.padrecicero.exception.stockExceptions;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(String message) {
        super(message);
    }
}
