package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PartPutDTO(
        @NotNull
        Long id,

        String name,

        String application,

        String mark,

        PartCategory category,

        BigDecimal costPrice,

        BigDecimal sellingPrice,

        Integer minimumStock
) {
}
