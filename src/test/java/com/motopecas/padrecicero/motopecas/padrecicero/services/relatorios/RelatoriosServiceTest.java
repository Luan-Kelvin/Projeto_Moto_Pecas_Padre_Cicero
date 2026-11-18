package com.motopecas.padrecicero.motopecas.padrecicero.services.relatorios;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.EstoqueBaixoItemDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.ResumoEstoqueBaixoDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.relatorios.PeriodoRelatorioInvalidoException;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.RelatorioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatoriosServiceTest {

    @Mock
    private RelatorioRepository relatorioRepository;

    @InjectMocks
    private RelatoriosService relatoriosService;

    @Test
    void summarizesLowStockPartCountAndAvailableQuantity() {
        when(relatorioRepository.listarPecasComEstoqueBaixo()).thenReturn(List.of(
                new EstoqueBaixoItemDTO(1L, "Pastilha", 2, 5),
                new EstoqueBaixoItemDTO(2L, "Filtro", 1, 3)
        ));

        ResumoEstoqueBaixoDTO result = relatoriosService.resumoEstoqueBaixo();

        assertThat(result.totalPecasAbaixoDoMinimo()).isEqualTo(2);
        assertThat(result.quantidadeTotalEmEstoque()).isEqualTo(3);
        assertThat(result.pecas()).hasSize(2);
        verify(relatorioRepository).listarPecasComEstoqueBaixo();
    }

    @Test
    void rejectsPeriodWhenStartDateIsAfterEndDate() {
        assertThatThrownBy(() -> relatoriosService.totalVendido(
                LocalDate.of(2026, 11, 2),
                LocalDate.of(2026, 11, 1)
        )).isInstanceOf(PeriodoRelatorioInvalidoException.class);

        verifyNoInteractions(relatorioRepository);
    }
}
