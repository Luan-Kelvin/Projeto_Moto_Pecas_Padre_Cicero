package com.motopecas.padrecicero.motopecas.padrecicero.controller.relatorios;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.DesempenhoFuncionarioDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.PecaMaisVendidaDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.ResumoEstoqueBaixoDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.TotalVendidoDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.services.relatorios.RelatoriosService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
@SecurityRequirement(name = "bearer-key")
public class RelatoriosController {

    private final RelatoriosService relatoriosService;

    public RelatoriosController(RelatoriosService relatoriosService) {
        this.relatoriosService = relatoriosService;
    }

    @GetMapping("/total-vendido")
    public ResponseEntity<TotalVendidoDTO> totalVendido(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        return ResponseEntity.ok(relatoriosService.totalVendido(dataInicio, dataFim));
    }

    @GetMapping("/pecas-mais-vendidas")
    public ResponseEntity<List<PecaMaisVendidaDTO>> rankingPecasMaisVendidas() {
        return ResponseEntity.ok(relatoriosService.rankingPecasMaisVendidas());
    }

    @GetMapping("/estoque-baixo")
    public ResponseEntity<ResumoEstoqueBaixoDTO> resumoEstoqueBaixo() {
        return ResponseEntity.ok(relatoriosService.resumoEstoqueBaixo());
    }

    @GetMapping("/desempenho-funcionarios")
    public ResponseEntity<List<DesempenhoFuncionarioDTO>> desempenhoFuncionarios(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        return ResponseEntity.ok(relatoriosService.desempenhoFuncionarios(dataInicio, dataFim));
    }
}
