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
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MovementType movementType;

    @NotNull
    private Integer quantity;

    private Integer previousQuantity;

    private Integer newQuantity;

    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @NotNull
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @NotNull
    private LocalDateTime transactionDate;

    public InventoryMovement(
            Stock stock,
            MovementType movementType,
            Integer previousQuantity,
            Integer newQuantity,
            String reason,
            Employee employee
    ) {
        this.stock = stock;
        this.part = stock.getPart();
        stock.addMovement(this);

        this.movementType = movementType;
        this.previousQuantity = previousQuantity;
        this.newQuantity = newQuantity;
        this.quantity = movementType == MovementType.AJUSTE
                ? newQuantity - previousQuantity
                : Math.abs(newQuantity - previousQuantity);
        this.reason = reason;

        this.employee = employee;
        employee.addMovement(this);

        this.transactionDate = LocalDateTime.now();
    }

    // ADICIONAR VENDA
    public void addSale(Sale sale){
        if (sale != null){
            this.sale = sale;
        }
    }
}
