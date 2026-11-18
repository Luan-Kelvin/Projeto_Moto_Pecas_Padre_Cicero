package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios;

import java.math.BigDecimal;

public record PecaMaisVendidaDTO(
        Long idPeca,
        String nome,
        Long quantidadeVendida,
        BigDecimal valorTotalVendido
) {
}
