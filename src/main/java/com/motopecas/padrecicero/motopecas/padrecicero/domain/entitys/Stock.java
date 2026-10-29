package com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys;

import com.motopecas.padrecicero.motopecas.padrecicero.exception.stockExceptions.TransactionAlreadyAddedException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stocks")
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(value = 0, message = "ERRO! Quantidade não pode ser menor que zero.")
    private Integer currentQuantity;

    private LocalDateTime lastUpdate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", unique = true)
    @NotNull
    private Part part;

    @OneToMany(mappedBy = "stock", fetch = FetchType.LAZY)
    private List<InventoryMovement> movements = new ArrayList<>();

    public Stock(Part part) {
        this.part = part;
        this.currentQuantity = 0;
        this.lastUpdate = LocalDateTime.now();
    }

    // ADICIONAR NOVA MOVMENTAÇÃO NO ESTOQUE
    public void addMovement(InventoryMovement movement){
        if (movements.contains(movement)){
            throw new TransactionAlreadyAddedException("ERRO! Movimentação ja adicionada");
        }

        movements.add(movement);
    }
}
