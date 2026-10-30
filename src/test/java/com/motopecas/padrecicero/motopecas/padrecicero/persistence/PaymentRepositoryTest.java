package com.motopecas.padrecicero.motopecas.padrecicero.persistence;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Payment;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Sale;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PaymentMethod;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.PaymentRepository;
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
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Testa se Pagamento está sendo salvo no banco.")
    void shouldSavePayment(){
        Employee employee = new Employee(
                "Joaquim Freitas", "jojo@gmail.com", "Joaquim12345", JobType.ADMIN
        );
        entityManager.persistAndFlush(employee);

        Sale sale = new Sale("12345", LocalDate.now(), BigDecimal.valueOf(300), employee);
        entityManager.persistAndFlush(sale);

        Payment payment = new Payment(BigDecimal.valueOf(300), PaymentMethod.PIX, null, sale);

        Payment paymentSaved = paymentRepository.save(payment);

        assertThat(paymentSaved.getSale()).isNotNull();
        assertThat(paymentSaved.getPaymentMethod()).isEqualTo(PaymentMethod.PIX);
    }

    @Test
    @DisplayName("Testa se Pagamento é retornado quando é solicitado pelo ID.")
    void shouldReturnPaymentById(){
        Employee employee = new Employee(
                "Joaquim Freitas", "jojo@gmail.com", "Joaquim12345", JobType.ADMIN
        );
        entityManager.persistAndFlush(employee);

        Sale sale = new Sale("12345", LocalDate.now(), BigDecimal.valueOf(300), employee);
        entityManager.persistAndFlush(sale);

        Payment payment = new Payment(BigDecimal.valueOf(300), PaymentMethod.PIX, null, sale);
        paymentRepository.save(payment);

        Long id = payment.getId();

        Optional<Payment> result = paymentRepository.findById(id);

        assertThat(result)
                .isPresent()
                .get()
                .extracting(Payment::getPaymentMethod)
                .isEqualTo(PaymentMethod.PIX);


    }

    @Test
    @DisplayName("Testa se Pagamento está sendo deletado do banco.")
    void shouldDeletePayment(){
        Employee employee = new Employee(
                "Joaquim Freitas", "jojo@gmail.com", "Joaquim12345", JobType.ADMIN
        );
        entityManager.persistAndFlush(employee);

        Sale sale = new Sale("12345", LocalDate.now(), BigDecimal.valueOf(300), employee);
        entityManager.persistAndFlush(sale);

        Payment payment = new Payment(BigDecimal.valueOf(300), PaymentMethod.PIX, null, sale);
        paymentRepository.save(payment);

        Long id = payment.getId();

        paymentRepository.delete(payment);
        entityManager.flush();

        Optional<Payment> result = paymentRepository.findById(id);

        assertThat(result).isNotPresent();
    }
}
