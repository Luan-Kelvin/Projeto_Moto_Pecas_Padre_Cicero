package com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions;

public class EmployeeAlreadyExistsException extends RuntimeException {
    public EmployeeAlreadyExistsException(String message) {
        super(message);
    }
}
