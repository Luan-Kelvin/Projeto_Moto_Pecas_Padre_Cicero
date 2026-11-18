package com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PaymentStatus;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.StatusSale;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.salesException.ListOfSalesItemsIsEmptyException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @NotNull
    private LocalDate dateOfSale;

    @NotNull
    private BigDecimal totalValue;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusSale statusSale;

    @OneToMany(mappedBy = "sale", fetch = FetchType.LAZY)
    private List<ItemForSale> itemForSales = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    @NotNull
    private Employee employee;

    @OneToOne(mappedBy = "sale", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Payment paymentInfo;

    public Sale(
            String saleCode,
            LocalDate dateOfSale,
            Employee employee
    ) {
        this.saleCode = saleCode;
        this.dateOfSale = dateOfSale;
        this.employee = employee;
        this.statusSale = StatusSale.ABERTA;
        this.totalValue = BigDecimal.ZERO;
    }

    // ADICIONAR ITEM A LISTA DE ITENS DE VENDAS
    public void addItemSale(ItemForSale item){
        itemForSales.add(item);

        item.addSale(this);
    }

    // REMOVER ITEM DA LISTA DE VENDAS
    public void removeItemForSale(ItemForSale item){
        if (itemForSales.contains(item)){
            itemForSales.remove(item);
        }
    }

    // ADICIONAR FUNCIONARIO
    public void addEmployee(Employee employee){
        this.employee = employee;

        if (!employee.getSales().contains(this)){
            employee.addSale(this);
        }
    }

    // ADICIONAR FORMA DE PAGAMENTO
    public void addPayment(Payment payment){
        this.paymentInfo = payment;
    }

    // CALCULAR VALOR TOTAL DA COMPRA
    public void calculatePurchaseAmount(){
        BigDecimal total = BigDecimal.ZERO;

        for (ItemForSale i : this.itemForSales){
            total = total.add(i.getSubtotal());
        }

        this.totalValue = total;
    }

    public void complete() {

        if (this.statusSale == StatusSale.CANCELADA) {
            throw new IllegalStateException(
                    "Não é possível concluir uma venda cancelada."
            );
        }

        if (this.paymentInfo == null ||
                this.paymentInfo.getPaymentStatus() != PaymentStatus.APROVADO) {
            throw new IllegalStateException("Pagamento ainda não aprovado.");
        }

        if (this.itemForSales.isEmpty()) {
            throw new IllegalStateException(
                    "Não é possível concluir uma venda sem itens."
            );
        }

        this.statusSale = StatusSale.CONCLUIDA;
    }

    public void cancel() {

        if (this.statusSale == StatusSale.CANCELADA) {
            throw new IllegalStateException(
                    "Não é possível cancelar uma venda cancelada."
            );
        }

        if (this.itemForSales.isEmpty()) {
            throw new IllegalStateException(
                    "Não é possível cancelar uma venda sem itens."
            );
        }

        this.statusSale = StatusSale.CANCELADA;
    }
}
