package com.motopecas.padrecicero.motopecas.padrecicero.services.relatorios;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.DesempenhoFuncionarioDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.EstoqueBaixoItemDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.PecaMaisVendidaDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.ResumoEstoqueBaixoDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.TotalVendidoDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.relatorios.PeriodoRelatorioInvalidoException;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.RelatorioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RelatoriosService {

    private final RelatorioRepository relatorioRepository;

    public RelatoriosService(RelatorioRepository relatorioRepository) {
        this.relatorioRepository = relatorioRepository;
    }

    @Transactional(readOnly = true)
    public TotalVendidoDTO totalVendido(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);
        return relatorioRepository.totalVendidoNoPeriodo(dataInicio, dataFim);
    }

    @Transactional(readOnly = true)
    public List<PecaMaisVendidaDTO> rankingPecasMaisVendidas() {
        return relatorioRepository.rankingPecasMaisVendidas();
    }

    @Transactional(readOnly = true)
    public ResumoEstoqueBaixoDTO resumoEstoqueBaixo() {
        List<EstoqueBaixoItemDTO> pecas = relatorioRepository.listarPecasComEstoqueBaixo();
        long quantidadeTotalEmEstoque = pecas.stream()
                .mapToLong(EstoqueBaixoItemDTO::quantidadeAtual)
                .sum();

        return new ResumoEstoqueBaixoDTO(
                (long) pecas.size(),
                quantidadeTotalEmEstoque,
                pecas
        );
    }

    @Transactional(readOnly = true)
    public List<DesempenhoFuncionarioDTO> desempenhoFuncionarios(LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);
        return relatorioRepository.desempenhoFuncionariosNoPeriodo(dataInicio, dataFim);
    }

    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null || dataInicio.isAfter(dataFim)) {
            throw new PeriodoRelatorioInvalidoException(
                    "Informe um período válido: a data inicial deve ser anterior ou igual à data final."
            );
        }
    }
}
