package com.motopecas.padrecicero.motopecas.padrecicero.exception.partExceptions;

public class InsufficientStockQuantityForSaleException extends RuntimeException {
    public InsufficientStockQuantityForSaleException(String message) {
        super(message);
    }
}
