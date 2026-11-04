package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.InputStatus;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.MovementType;

public record MovementResponseDTO(
        InputStatus status,
        MovementType type,
        Integer quantity,
        Long idStock,
        String part,
        Integer currentQuantity,
        Long idMovement

) {
}
