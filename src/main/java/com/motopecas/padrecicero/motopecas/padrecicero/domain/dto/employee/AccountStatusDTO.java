package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee;

import jakarta.validation.constraints.NotNull;

public record AccountStatusDTO(
        @NotNull
        Long id,

        @NotNull
        String status
) {
}
