package com.motopecas.padrecicero.motopecas.padrecicero.exception.relatorios;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PeriodoRelatorioInvalidoException extends RuntimeException {

    public PeriodoRelatorioInvalidoException(String message) {
        super(message);
    }
}
