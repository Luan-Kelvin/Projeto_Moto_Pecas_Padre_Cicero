package com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "itemsForSales")
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor
public class ItemForSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne()
    @JoinColumn(name = "part_id")
    private Part part;


    public ItemForSale(Integer quantity, BigDecimal unitPrice, Sale sale, Part part) {
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
}
