package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.InputStatus;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.MovementType;

public record MovementAdjustmnetResponseDTO(
        InputStatus status,
        MovementType type,
        Integer quantity,
        String reason,
        Long idStock,
        String part,
        Integer currentQuantity,
        Long idMovement
) {
}
