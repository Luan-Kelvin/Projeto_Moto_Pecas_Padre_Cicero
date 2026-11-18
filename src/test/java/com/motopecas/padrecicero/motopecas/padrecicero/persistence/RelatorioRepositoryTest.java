package com.motopecas.padrecicero.motopecas.padrecicero.persistence;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.DesempenhoFuncionarioDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.PecaMaisVendidaDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.relatorios.TotalVendidoDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.ItemForSale;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Payment;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Sale;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Stock;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PaymentMethod;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PaymentStatus;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.StatusSale;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.RelatorioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RelatorioRepositoryTest {

    @Autowired
    private RelatorioRepository relatorioRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void reportsOnlyCompletedSalesWithApprovedPayments() {
        Employee employee = new Employee("Ana", "ana.relatorios@example.com", "password", JobType.ADMIN);
        entityManager.persist(employee);

        Part part = new Part(
                "Pastilha de freio",
                null,
                "Marca",
                PartCategory.FREIO,
                "REL-001",
                BigDecimal.TEN,
                BigDecimal.valueOf(20),
                5,
                employee
        );
        Stock stock = new Stock(part);
        ReflectionTestUtils.setField(stock, "currentQuantity", 2);
        entityManager.persist(part);
        entityManager.persist(stock);

        saveSale("VALIDA", employee, part, LocalDate.of(2026, 11, 10), StatusSale.CONCLUIDA,
                PaymentStatus.APROVADO, 2, BigDecimal.valueOf(20));
        saveSale("CANCELADA", employee, part, LocalDate.of(2026, 11, 11), StatusSale.CANCELADA,
                PaymentStatus.ESTORNADO, 5, BigDecimal.valueOf(20));
        saveSale("ESTORNADA", employee, part, LocalDate.of(2026, 11, 12), StatusSale.CONCLUIDA,
                PaymentStatus.ESTORNADO, 7, BigDecimal.valueOf(20));
        entityManager.flush();

        TotalVendidoDTO total = relatorioRepository.totalVendidoNoPeriodo(
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30)
        );
        List<PecaMaisVendidaDTO> ranking = relatorioRepository.rankingPecasMaisVendidas();
        List<DesempenhoFuncionarioDTO> desempenho = relatorioRepository.desempenhoFuncionariosNoPeriodo(
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30)
        );

        assertThat(total.totalVendido()).isEqualByComparingTo(BigDecimal.valueOf(40));
        assertThat(total.quantidadeVendas()).isEqualTo(1);
        assertThat(ranking).hasSize(1);
        assertThat(ranking.getFirst().quantidadeVendida()).isEqualTo(2);
        assertThat(ranking.getFirst().valorTotalVendido()).isEqualByComparingTo(BigDecimal.valueOf(40));
        assertThat(desempenho).hasSize(1);
        assertThat(desempenho.getFirst().quantidadeVendas()).isEqualTo(1);
        assertThat(desempenho.getFirst().totalVendido()).isEqualByComparingTo(BigDecimal.valueOf(40));

        var estoqueBaixo = relatorioRepository.listarPecasComEstoqueBaixo();
        assertThat(estoqueBaixo).hasSize(1);
        assertThat(estoqueBaixo.getFirst().quantidadeAtual()).isEqualTo(2);
    }

    private void saveSale(
            String code,
            Employee employee,
            Part part,
            LocalDate date,
            StatusSale status,
            PaymentStatus paymentStatus,
            int quantity,
            BigDecimal unitPrice
    ) {
        Sale sale = new Sale(code, date, employee);
        ReflectionTestUtils.setField(sale, "statusSale", status);

        ItemForSale item = new ItemForSale(quantity, unitPrice, part, sale);
        sale.addItemSale(item);
        sale.calculatePurchaseAmount();

        entityManager.persist(sale);
        entityManager.persist(item);

        Payment payment = new Payment(sale.getTotalValue(), PaymentMethod.PIX, null, sale);
        ReflectionTestUtils.setField(payment, "paymentStatus", paymentStatus);
        sale.addPayment(payment);
        entityManager.persist(payment);
    }
}
