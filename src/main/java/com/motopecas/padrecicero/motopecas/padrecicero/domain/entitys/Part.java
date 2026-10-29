package com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.partExceptions.InvalidPartPriceException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private String name;

    private String description;

    private String mark;

    @Enumerated(EnumType.STRING)
    private PartCategory category;

    @NotBlank
    @Column(unique = true)
    private String partCode;

    @NotNull
    private BigDecimal costPrice;

    @NotNull
    private BigDecimal sellingPrice;

    @NotNull
    @Min(0)
    private Integer minimumStock;

    private Boolean active;

    private LocalDate creationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id")
    @NotNull
    private Employee creator;

    @OneToOne(mappedBy = "part", fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    public Part(
            String name,
            String description,
            String mark,
            PartCategory category,
            String partCode,
            BigDecimal costPrice,
            BigDecimal sellingPrice,
            Integer minimumStock,
            Employee creator) {
        this.name = name;
        this.description = description;
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


}
