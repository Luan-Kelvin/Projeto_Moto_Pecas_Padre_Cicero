package com.motopecas.padrecicero.motopecas.padrecicero.persistence;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.*;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.MovementType;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.InventoryMovementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class InventoryMovementRepositoryTest {

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Testa se movimentação de estoque está sendo salvo no banco.")
    void shouldSaveInventoryMovement(){
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

        Sale sale = new Sale("12345", LocalDate.now(),  employee);
        entityManager.persistAndFlush(sale);

        Stock stock = new Stock(part);
        entityManager.persistAndFlush(stock);
        ReflectionTestUtils.setField(stock, "currentQuantity", 20);

        InventoryMovement inventoryMovement = new InventoryMovement(
                stock, MovementType.AJUSTE, 20, 15, "Divergência na contagem física", employee
        );
        ReflectionTestUtils.setField(stock, "currentQuantity", 15);

        InventoryMovement movementSaved = inventoryMovementRepository.save(inventoryMovement);
        entityManager.flush();

        assertThat(movementSaved.getId()).isNotNull();
        entityManager.clear();
        Part persistedPart = entityManager.find(Part.class, part.getId());
        assertThat(persistedPart.getMovements().size()).isEqualTo(1);
        InventoryMovement persistedMovement = persistedPart.getMovements().get(0);
        assertThat(persistedMovement.getPart().getId()).isEqualTo(part.getId());
        assertThat(persistedMovement.getQuantity()).isEqualTo(-5);
        assertThat(persistedMovement.getPreviousQuantity()).isEqualTo(20);
        assertThat(persistedMovement.getNewQuantity()).isEqualTo(15);
    }

    @Test
    @DisplayName("Testa se movimentação de estoque é retornado quando é solicitado pelo ID.")
    void shouldReturninventoryMovementById(){
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

        Sale sale = new Sale("12345", LocalDate.now(),  employee);
        entityManager.persistAndFlush(sale);

        Stock stock = new Stock(part);
        entityManager.persistAndFlush(stock);

        InventoryMovement inventoryMovement = new InventoryMovement(
                stock, MovementType.SAIDA, 10, 7, "Envio para garantia", employee
        );

        InventoryMovement movementSaved = inventoryMovementRepository.save(inventoryMovement);
        entityManager.flush();

        Long id = inventoryMovement.getId();

        entityManager.clear();
        Optional<InventoryMovement> result = inventoryMovementRepository.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getQuantity()).isEqualTo(3);
        assertThat(result.get().getPreviousQuantity()).isEqualTo(10);
        assertThat(result.get().getNewQuantity()).isEqualTo(7);
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

        Sale sale = new Sale("12345", LocalDate.now(), employee);
        entityManager.persistAndFlush(sale);

        Stock stock = new Stock(part);
        entityManager.persistAndFlush(stock);

        InventoryMovement inventoryMovement = new InventoryMovement(
                stock, MovementType.SAIDA, 10, 7, "Envio para garantia", employee
        );

        inventoryMovementRepository.save(inventoryMovement);
        entityManager.flush();

        Long id = inventoryMovement.getId();

        inventoryMovementRepository.delete(inventoryMovement);
        entityManager.flush();

        Optional<InventoryMovement> result = inventoryMovementRepository.findById(id);

        assertThat(result).isNotPresent();
    }
}
