package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PartGetDTO(
        Long id,

        String partCode,

        String name,

        String description,

        String mark,

        PartCategory category,

        BigDecimal costPrice,

        BigDecimal sellingPrice,

        boolean active,

        LocalDate creationDate,

        Long creatorId,

        Long stockId
) {
}
