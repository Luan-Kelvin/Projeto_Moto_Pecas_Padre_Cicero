package com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @OneToOne
    @JoinColumn(name = "part_id")
    private Part part;

    public Stock(Part part) {
        this.part = part;
        this.currentQuantity = 0;
        this.lastUpdate = LocalDateTime.now();
    }
}
