package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.StatusSale;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ResponseCreateSaleDTO(
        Long id,
        String saleCode,
        LocalDate dateOfSale,
        Long id_employee,
        StatusSale status,
        BigDecimal totalValue
) {
}
