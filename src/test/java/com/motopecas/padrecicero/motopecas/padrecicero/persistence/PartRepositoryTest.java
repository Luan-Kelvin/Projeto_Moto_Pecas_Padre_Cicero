package com.motopecas.padrecicero.motopecas.padrecicero.persistence;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.PartRepository;
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
public class PartRepositoryTest {

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Testa se Peça está sendo salvo no banco.")
    void shouldSavePart(){
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

        Part partSaved = partRepository.save(part);

        assertThat(partSaved.getId()).isNotNull();
        assertThat(partSaved.getPartCode()).isEqualTo("12345678");
        assertThat(partSaved.getName()).isEqualTo("Patim De Freio");
        assertThat(partSaved.getMark()).isEqualTo("Cobreq");
    }

    @Test
    @DisplayName("Testa se Peça é retornado quando é solicitado pelo ID.")
    void shouldReturnPartById(){
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

        partRepository.save(part);

        Long id = part.getId();;

        Optional<Part> result = partRepository.findById(id);

        assertThat(result)
                .isPresent()
                .get()
                .extracting(Part::getPartCode)
                .isEqualTo("12345678");
    }

    @Test
    @DisplayName("Testa se Peça está sendo deletada do banco.")
    void shouldDeletePart(){
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

        partRepository.save(part);
        entityManager.flush();

        Long id = part.getId();
        partRepository.delete(part);
        entityManager.flush();

        Optional<Part> result = partRepository.findById(id);

        assertThat(result).isNotPresent();
    }
}
