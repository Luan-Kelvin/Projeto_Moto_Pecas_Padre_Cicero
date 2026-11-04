package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.InputStatus;

public record MovementResponseDTO(
        InputStatus status,
        Integer quantity,
        Long idStock,
        String part,
        Integer currentQuantity,
        Long idMovement

) {
}
