package com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "items_for_sales")
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor
public class ItemForSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    private Integer quantity;

    @NotNull
    @Positive
    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    @NotNull
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    @NotNull
    private Part part;


    public ItemForSale(Integer quantity, BigDecimal unitPrice, Part part) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.sale = sale;
        this.part = part;
        this.subtotal = calculateSubtotal(quantity, unitPrice);
    }

    // CALCULAR SUBTOTAL
    public BigDecimal calculateSubtotal(Integer quanttity, BigDecimal valueUnitPrice){
        BigDecimal total = valueUnitPrice.multiply(BigDecimal.valueOf(quanttity));

        return total;
    }

    // ADICIONAR VENDA
    public void addSale(Sale sale){
        this.sale = sale;
    }
}
