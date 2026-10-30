package com.motopecas.padrecicero.motopecas.padrecicero.persistence;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Sale;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.SaleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class SaleRepositoryTest {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Testa se Venda está sendo salvo no banco.")
    void shouldSaveSale(){
        Employee employee = new Employee(
                "Joaquim Freitas", "jojo@gmail.com", "Joaquim12345", JobType.ADMIN
        );
        entityManager.persistAndFlush(employee);

        Sale sale = new Sale("12345", LocalDate.now(), BigDecimal.valueOf(300), employee);

        Sale saleSaved = saleRepository.save(sale);
        entityManager.flush();
        entityManager.clear();

        assertThat(saleSaved.getId()).isNotNull();
        assertThat(saleSaved.getSaleCode()).isEqualTo("12345");

    }

    @Test
    @DisplayName("Testa se venda é retornado quando é solicitado pelo ID.")
    void shouldReturnSaleById(){
        Employee employee = new Employee(
                "Joaquim Freitas", "jojo@gmail.com", "Joaquim12345", JobType.ADMIN
        );
        entityManager.persistAndFlush(employee);

        Sale sale = new Sale("12345", LocalDate.now(), BigDecimal.valueOf(300), employee);
        saleRepository.save(sale);
        entityManager.flush();

        Long id = sale.getId();

        Optional<Sale> result = saleRepository.findById(id);

        assertThat(result)
                .isPresent()
                .get()
                .extracting(Sale::getSaleCode)
                .isEqualTo("12345");


    }

    @Test
    @DisplayName("Testa se Venda está sendo deletado do banco.")
    void shouldDeleteSale(){
        Employee employee = new Employee(
                "Joaquim Freitas", "jojo@gmail.com", "Joaquim12345", JobType.ADMIN
        );
        entityManager.persistAndFlush(employee);

        Sale sale = new Sale("12345", LocalDate.now(), BigDecimal.valueOf(300), employee);
        saleRepository.save(sale);
        entityManager.flush();

        Long id = sale.getId();

        saleRepository.delete(sale);
        entityManager.flush();

        Optional<Sale> result = saleRepository.findById(id);

        assertThat(result).isNotPresent();
    }
}
