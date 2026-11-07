package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record SaleConclusionDTO(

        @NotNull
        Long id_sale,

        @NotNull
        PaymentMethod paymentMethod,

        String externalReference
) {
}
