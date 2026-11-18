package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TotalVendidoDTO(
        LocalDate dataInicio,
        LocalDate dataFim,
        BigDecimal totalVendido,
        Long quantidadeVendas
) {
}
