package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.MovementType;

import java.time.LocalDateTime;

public record PartMovementDTO(
        Long id,
        MovementType movementType,
        Integer quantity,
        String reason,
        Long employeeId,
        String employeeName,
        LocalDateTime transactionDate
) {
}
