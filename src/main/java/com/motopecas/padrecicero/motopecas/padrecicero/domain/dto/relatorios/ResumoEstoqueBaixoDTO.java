package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios;

import java.util.List;

public record ResumoEstoqueBaixoDTO(
        Long totalPecasAbaixoDoMinimo,
        Long quantidadeTotalEmEstoque,
        List<EstoqueBaixoItemDTO> pecas
) {
}
