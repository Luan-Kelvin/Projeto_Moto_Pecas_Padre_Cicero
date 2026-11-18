package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios;

import java.math.BigDecimal;

public record DesempenhoFuncionarioDTO(
        Long idFuncionario,
        String nomeFuncionario,
        Long quantidadeVendas,
        BigDecimal totalVendido
) {
}
