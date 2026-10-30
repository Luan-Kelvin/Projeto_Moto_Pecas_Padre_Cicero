package com.motopecas.padrecicero.motopecas.padrecicero.persistence;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Testa se funcionário está sendo salvo no banco.")
    void shouldSaveEmpolyee(){
        Employee employee = new Employee(
                "Joaquim Freitas", "jojo@gmail.com", "Joaquim12345", JobType.ADMIN
        );

        Employee employeeSaved = employeeRepository.save(employee);

        entityManager.flush();
        entityManager.clear();

        assertThat(employeeSaved.getId()).isNotNull();
        assertThat(employeeSaved.getName()).isEqualTo(employee.getName());
        assertThat(employeeSaved.getEmail()).isEqualTo(employee.getEmail());
        assertThat(employeeSaved.getPassword()).isEqualTo(employee.getPassword());
        assertThat(employeeSaved.getJobType()).isEqualTo(employee.getJobType());
    }

    @Test
    @DisplayName("Testa se Funcionário é retornado quando é solicitado pelo ID.")
    void shouldReturnEmployeeById(){
        Employee employee = new Employee(
                "Joaquim Freitas", "jojo@gmail.com", "Joaquim12345", JobType.ADMIN
        );

        employeeRepository.save(employee);
        entityManager.flush();

        Long id = employee.getId();

        Optional<Employee> result = employeeRepository.findById(id);

        assertThat(result)
                .isPresent()
                .get()
                .extracting(Employee::getName)
                .isEqualTo("Joaquim Freitas");


    }

    @Test
    @DisplayName("Testa se Funcionário está sendo deletado do banco.")
    void shouldDeleteEmployee(){
        Employee employee = new Employee(
                "Joaquim Freitas", "jojo@gmail.com", "Joaquim12345", JobType.ADMIN
        );

        employeeRepository.save(employee);
        entityManager.flush();

        Long id = employee.getId();

        employeeRepository.delete(employee);
        entityManager.flush();

        Optional<Employee> result = employeeRepository.findById(id);

        assertThat(result).isNotPresent();
    }
}
