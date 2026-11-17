package com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.partExceptions.InvalidPartPriceException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.stockExceptions.TransactionAlreadyAddedException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
@Table(name = "parts")
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Setter
    private String name;

    @Setter
    private String application;

    @Setter
    private String mark;

    @Enumerated(EnumType.STRING)
    @Setter
    private PartCategory category;

    @NotBlank
    @Column(unique = true)
    private String partCode;

    @NotNull
    @Setter
    private BigDecimal costPrice;

    @NotNull
    @Setter
    private BigDecimal sellingPrice;

    @NotNull
    @Min(0)
    @Setter
    private Integer minimumStock;

    private Boolean active;

    private LocalDate creationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id")
    @NotNull
    private Employee creator;

    @OneToOne(mappedBy = "part", fetch = FetchType.EAGER)
    private Stock stock;

    @OneToMany(mappedBy = "part", fetch = FetchType.LAZY)
    @OrderBy("transactionDate DESC, id DESC")
    private List<InventoryMovement> movements = new ArrayList<>();

    public Part(
            String name,
            String application,
            String mark,
            PartCategory category,
            String partCode,
            BigDecimal costPrice,
            BigDecimal sellingPrice,
            Integer minimumStock,
            Employee creator) {
        this.name = name;
        this.application = application;
        this.mark = mark;
        this.category = category;
        this.partCode = partCode;
        this.costPrice = costPrice;

        checkPartPrice(sellingPrice);

        this.minimumStock = minimumStock;
        this.creator = creator;
        this.active = true;
        this.creationDate = LocalDate.now();
    }

    // VERIFICAR VALOR MÍNIMO DA PEÇA
    public void checkPartPrice(BigDecimal value){
        if (value == null || value.compareTo(BigDecimal.valueOf(0.10)) < 0){
            throw new InvalidPartPriceException("ERRO! Valor mínimo de preço de venda da peça é R$0,10.");
        }

        if (value.compareTo(this.costPrice) <= 0){
            throw new InvalidPartPriceException(
                    "ERRO! Valor do preço de venda deve ser maior que o valor do preço de custo."
            );
        }

        this.sellingPrice = value;
    }

    // ADICIONAR ESTOQUE
    public void addStock(Stock stock){
        this.stock = stock;
    }

    // ADICIONAR MOVIMENTAÇÃO DA PEÇA
    public void addMovement(InventoryMovement movement){
        if (movement == null){
            throw new IllegalArgumentException("A movimentação não pode ser nula.");
        }

        if (movements.contains(movement)){
            throw new TransactionAlreadyAddedException("ERRO! Movimentação já adicionada.");
        }

        movements.add(movement);
    }

    // DESATIVAR PEÇA
    public void deactivate(){
        if (!this.active){
            throw new IllegalAccessError("ERRO! Peça já desativada.");
        }
        this.active = false;
    }

    // ATIVAR PEÇA
    public void activate(){
        if (this.active){
            throw new IllegalAccessError("ERRO! Peça já ativada.");
        }
        this.active = true;
    }

}
