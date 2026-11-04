package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;

import java.math.BigDecimal;

public record PartPostDTO(
        String partCode,

        String name,

        String description,

        String mark,

        PartCategory category,

        BigDecimal costPrice,

        BigDecimal sellingPrice,

        Integer minimumStock
) {
}
