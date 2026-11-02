package com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions;

public class VerificationTokenExpiredException extends RuntimeException {
    public VerificationTokenExpiredException(String message) {
        super(message);
    }
}
