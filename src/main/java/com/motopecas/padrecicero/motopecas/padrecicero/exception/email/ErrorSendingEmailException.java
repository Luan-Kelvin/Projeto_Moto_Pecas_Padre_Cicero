package com.motopecas.padrecicero.motopecas.padrecicero.exception.email;

public class ErrorSendingEmailException extends RuntimeException {
    public ErrorSendingEmailException(String message) {
        super(message);
    }

    public ErrorSendingEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
