package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginDTO(

        @NotBlank
        @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "E-mail inválido")
        String email,

        @NotBlank
        String password
) {
}
