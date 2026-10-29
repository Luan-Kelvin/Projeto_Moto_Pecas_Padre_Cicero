package com.motopecas.padrecicero.motopecas.padrecicero.repository;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
