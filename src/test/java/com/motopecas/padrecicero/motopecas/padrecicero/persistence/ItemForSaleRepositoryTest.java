package com.motopecas.padrecicero.motopecas.padrecicero.persistence;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.ItemForSale;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.ItemForSaleRepository;
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
public class ItemForSaleRepositoryTest {

    @Autowired
    private ItemForSaleRepository itemForSaleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Testa se Item de Venda está sendo salvo no banco.")
    void shouldSaveItemForSale(){
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

        ItemForSale item = new ItemForSale(5, BigDecimal.valueOf(22.0) ,part);

        ItemForSale itemSaved = itemForSaleRepository.save(item);

        assertThat(itemSaved.getId()).isNotNull();
    }

    @Test
    @DisplayName("Testa se Funcionário é retornado quando é solicitado pelo ID.")
    void shouldReturnEmployeeById(){
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

        ItemForSale item = new ItemForSale(5, BigDecimal.valueOf(22.0) ,part);

        Long id = item.getId();

        Optional<ItemForSale> result = itemForSaleRepository.findById(id);

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("Testa se Funcionário está sendo deletado do banco.")
    void shouldDeleteEmployee(){
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

        ItemForSale item = new ItemForSale(5, BigDecimal.valueOf(22.0) ,part);

        itemForSaleRepository.save(item);
        entityManager.flush();

        Long id = item.getId();

        itemForSaleRepository.delete(item);
        entityManager.flush();

        Optional<ItemForSale> result = itemForSaleRepository.findById(id);

        assertThat(result).isNotPresent();
    }
}
