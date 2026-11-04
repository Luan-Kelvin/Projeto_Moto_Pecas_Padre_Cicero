package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.InputStatus;

public record InputResponseDTO(
        InputStatus status,
        Integer quantity,
        Long idStock,
        String part,
        Integer currentQuantity,
        Long idMovement

) {
}
