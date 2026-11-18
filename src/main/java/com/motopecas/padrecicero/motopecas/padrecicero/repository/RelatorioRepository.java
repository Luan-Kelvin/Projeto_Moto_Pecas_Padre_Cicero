package com.motopecas.padrecicero.motopecas.padrecicero.repository;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.DesempenhoFuncionarioDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.EstoqueBaixoItemDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.PecaMaisVendidaDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.TotalVendidoDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Sale;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.Repository;

import java.time.LocalDate;
import java.util.List;

public interface RelatorioRepository extends Repository<Sale, Long> {

    @Query("""
            SELECT new com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.TotalVendidoDTO(
                :dataInicio,
                :dataFim,
                COALESCE(SUM(s.totalValue), 0),
                COUNT(s.id)
            )
            FROM Sale s
            JOIN s.paymentInfo payment
            WHERE s.dateOfSale BETWEEN :dataInicio AND :dataFim
                AND s.statusSale = 'CONCLUIDA'
                AND payment.paymentStatus = 'APROVADO'
            """)
    TotalVendidoDTO totalVendidoNoPeriodo(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    @Query("""
            SELECT new com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.PecaMaisVendidaDTO(
                part.id,
                part.name,
                SUM(item.quantity),
                COALESCE(SUM(item.subtotal), 0)
            )
            FROM ItemForSale item
            JOIN item.sale s
            JOIN s.paymentInfo payment
            JOIN item.part part
            WHERE s.statusSale = 'CONCLUIDA'
                AND payment.paymentStatus = 'APROVADO'
            GROUP BY part.id, part.name
            ORDER BY SUM(item.quantity) DESC, part.name ASC
            """)
    List<PecaMaisVendidaDTO> rankingPecasMaisVendidas();

    @Query("""
            SELECT new com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.EstoqueBaixoItemDTO(
                part.id,
                part.name,
                stock.currentQuantity,
                part.minimumStock
            )
            FROM Part part
            JOIN part.stock stock
            WHERE stock.currentQuantity < part.minimumStock
            ORDER BY stock.currentQuantity ASC, part.name ASC
            """)
    List<EstoqueBaixoItemDTO> listarPecasComEstoqueBaixo();

    @Query("""
            SELECT new com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.DesempenhoFuncionarioDTO(
                employee.id,
                employee.name,
                COUNT(s.id),
                COALESCE(SUM(s.totalValue), 0)
            )
            FROM Sale s
            JOIN s.employee employee
            JOIN s.paymentInfo payment
            WHERE s.dateOfSale BETWEEN :dataInicio AND :dataFim
                AND s.statusSale = 'CONCLUIDA'
                AND payment.paymentStatus = 'APROVADO'
            GROUP BY employee.id, employee.name
            ORDER BY SUM(s.totalValue) DESC, employee.name ASC
            """)
    List<DesempenhoFuncionarioDTO> desempenhoFuncionariosNoPeriodo(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );
}
