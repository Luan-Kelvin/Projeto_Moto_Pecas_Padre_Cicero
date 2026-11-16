package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;

import java.math.BigDecimal;

public record PartPostDTO(
        String name,

        String application,

        String mark,

        PartCategory category,

        BigDecimal costPrice,

        BigDecimal sellingPrice,

        Integer minimumStock
) {
}
