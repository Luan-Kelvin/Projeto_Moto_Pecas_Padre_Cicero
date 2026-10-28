package com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.StatusSale;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.salesException.ListOfSalesItemsIsEmptyException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String saleCode;

    private LocalDate dateOfSale;

    @NotNull
    private BigDecimal totalValue;

    private StatusSale statusSale;

    @OneToMany(mappedBy = "sale")
    private List<ItemForSale> itemForSales = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment paymentInfo;

    public Sale(
            String saleCode,
            LocalDate dateOfSale,
            BigDecimal totalValue,
            List<ItemForSale> itemForSales,
            Employee employee,
            Payment paymentInfo
    ) {
        this.saleCode = saleCode;
        this.dateOfSale = dateOfSale;
        this.totalValue = totalValue;
        verifiyListItems(itemForSales);
        this.employee = employee;
        this.statusSale = StatusSale.ABERTA;
        this.paymentInfo = paymentInfo;
    }

    // VERIFICAR SE LISTA DE ITENS ESTÁ VAZIA
    public void verifiyListItems(List<ItemForSale> items){
        if (items.isEmpty()){
            throw new ListOfSalesItemsIsEmptyException("ERRO! Lista com itens de compra está vazia.");
        }

        this.itemForSales = items;
    }
}
