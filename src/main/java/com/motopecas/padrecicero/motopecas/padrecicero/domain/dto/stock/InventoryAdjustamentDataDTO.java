package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InventoryAdjustamentDataDTO(
        @NotNull
        Long idParty,

        @NotNull
        Integer currentQuantity,

        @NotBlank
        String reason
) {
}
