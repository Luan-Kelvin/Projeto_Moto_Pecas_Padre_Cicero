package com.motopecas.padrecicero.motopecas.padrecicero.persistence;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Sale;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Stock;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Testa se estoque está sendo salvo no banco.")
    void shouldSaveStock(){
        Employee employee = new Employee(
                "Joaquim Freitas", "jojo@gmail.com", "Joaquim12345", JobType.ADMIN
        );
        entityManager.persistAndFlush(employee);

        Part part = new Part(
                "Patim De Freio",
                null, "Cobreq",
                PartCategory.FREIO,
                "12345678",
                BigDecimal.valueOf(30.0),
                BigDecimal.valueOf(55.5),
                10,
                employee
        );
        entityManager.persistAndFlush(part);

        Stock stock = new Stock(part);

        Stock stockSaved = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        assertThat(stockSaved.getId()).isNotNull();
        assertThat(stockSaved.getPart()).isEqualTo(part);
    }

    @Test
    @DisplayName("Testa se Estoque é retornado quando é solicitado pelo ID.")
    void shouldReturnStockById(){
        Employee employee = new Employee(
                "Joaquim Freitas", "jojo@gmail.com", "Joaquim12345", JobType.ADMIN
        );
        entityManager.persistAndFlush(employee);

        Part part = new Part(
                "Patim De Freio",
                null, "Cobreq",
                PartCategory.FREIO,
                "12345678",
                BigDecimal.valueOf(30.0),
                BigDecimal.valueOf(55.5),
                10,
                employee
        );
        entityManager.persistAndFlush(part);

        Stock stock = new Stock(part);
        stockRepository.save(stock);
        entityManager.flush();

        Long id = stock.getId();

        Optional<Stock> result = stockRepository.findById(id);

        assertThat(result)
                .isPresent()
                .get()
                .extracting(Stock::getPart)
                .isEqualTo(part);
    }

    @Test
    @DisplayName("Testa se Estoque está sendo deletado do banco.")
    void shouldDeleteStock(){
        Employee employee = new Employee(
                "Joaquim Freitas", "jojo@gmail.com", "Joaquim12345", JobType.ADMIN
        );
        entityManager.persistAndFlush(employee);

        Part part = new Part(
                "Patim De Freio",
                null, "Cobreq",
                PartCategory.FREIO,
                "12345678",
                BigDecimal.valueOf(30.0),
                BigDecimal.valueOf(55.5),
                10,
                employee
        );
        entityManager.persistAndFlush(part);

        Stock stock = new Stock(part);
        stockRepository.save(stock);
        entityManager.flush();

        Long id = stock.getId();

        stockRepository.delete(stock);
        entityManager.flush();

        Optional<Stock> result = stockRepository.findById(id);

        assertThat(result).isNotPresent();
    }
}
