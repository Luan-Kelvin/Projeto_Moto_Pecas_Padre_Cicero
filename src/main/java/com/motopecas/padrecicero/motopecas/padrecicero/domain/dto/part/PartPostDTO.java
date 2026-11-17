package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Stock;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PartPostDTO(
        @NotBlank
        String name,

        @NotBlank
        String application,

        @NotBlank
        String mark,


        @NotNull
        PartCategory category,

        @NotNull
        BigDecimal costPrice,

        @NotNull
        BigDecimal sellingPrice,

        @NotNull
        Integer minimumStock,

        @NotNull
        Integer quantity
) {
}
