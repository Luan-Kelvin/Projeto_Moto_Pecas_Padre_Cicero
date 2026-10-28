package com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.MovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invetory_movement")
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor
public class InvetoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MovementType movementType;

    @NotNull
    private Integer quantity;

    private String reason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @NotNull
    private LocalDateTime transactionDate;

    public InvetoryMovement(
            Stock stock,
            MovementType movementType,
            Integer quantity,
            String reason,
            Employee employee,
            Sale sale
    ) {
        this.stock = stock;
        this.movementType = movementType;
        this.quantity = quantity;
        this.reason = reason;
        this.employee = employee;
        this.sale = sale;
        this.transactionDate = LocalDateTime.now();
    }
}
