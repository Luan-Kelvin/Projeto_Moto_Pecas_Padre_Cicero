package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale;

import java.math.BigDecimal;

public record ResponseItemForSaleDTO(
        Integer quantity,
        BigDecimal unitPrice,
        Long id_part,
        Long id_sale,
        BigDecimal subTotal
) {
}
