package com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PaymentMethod;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Positive
    private BigDecimal valuePayment;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @NotNull
    private LocalDateTime paymentDate;

    private String externalReference;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    public Payment(BigDecimal value, PaymentMethod paymentMethod, String externalReference, Sale sale) {
        this.valuePayment = value;
        this.paymentMethod = paymentMethod;
        this.externalReference = externalReference;
        this.sale = sale;
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PENDENTE;
    }

    // APROVAR PAGAMENTO
    public void approve() {
        if (this.paymentStatus != PaymentStatus.PENDENTE) {
            throw new IllegalStateException(
                    "Somente pagamentos pendentes podem ser aprovados."
            );
        }

        this.paymentStatus = PaymentStatus.APROVADO;
    }

    // REJEITAR PAGAMENTO
    public void reject() {
        if (this.paymentStatus != PaymentStatus.PENDENTE) {
            throw new IllegalStateException(
                    "Somente pagamentos pendentes podem ser recusados."
            );
        }

        this.paymentStatus = PaymentStatus.RECUSADO;
    }

    // ESTORNAR PAGAMENTO
    public void refund() {
        if (this.paymentStatus != PaymentStatus.APROVADO) {
            throw new IllegalStateException(
                    "Somente pagamentos aprovados podem ser estornados."
            );
        }

        this.paymentStatus = PaymentStatus.ESTORNADO;
    }
}
