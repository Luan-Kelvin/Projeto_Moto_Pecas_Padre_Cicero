package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios;

public record EstoqueBaixoItemDTO(
        Long idPeca,
        String nome,
        Integer quantidadeAtual,
        Integer estoqueMinimo
) {
}
