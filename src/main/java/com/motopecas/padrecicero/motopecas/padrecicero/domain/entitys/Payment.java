package com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PaymentMethod;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private BigDecimal value;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @NotNull
    private LocalDateTime paymentDate;

    private String externalReference;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    public Payment(BigDecimal value, PaymentMethod paymentMethod, String externalReference, Sale sale) {
        this.value = value;
        this.paymentMethod = paymentMethod;
        this.externalReference = externalReference;
        this.sale = sale;
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PENDENTE;
    }
}
