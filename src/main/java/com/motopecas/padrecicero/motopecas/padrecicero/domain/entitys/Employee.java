package com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions.ItemAlreadyRegistredException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions.SaleAlreadyRegistredException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "E-mail inválido")
    @Column(unique = true)
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$",
            message = "A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, uma minúscula e um número")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    private JobType jobType;

    private Boolean active;

    private Boolean verified;

    private String verificationToken;

    private LocalDateTime verificationTokenExpiration;

    private LocalDate creationDate;

    @OneToMany(mappedBy = "emplyee")
    private List<Sale> sales = new ArrayList<>();

    @OneToMany(mappedBy = "creator")
    private List<Part> registredItems = new ArrayList<>();

    public Employee(Long id, String name, String email, String password, JobType jobType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.jobType = jobType;
        this.active = true;
        this.verified = null;
        this.verificationToken = null;
        this.verificationTokenExpiration = null;
        this.creationDate = LocalDate.now();
    }

    // ADICIONAR NOVA VENDA
    public void addSale(Sale sale){
        if (sales.contains(sale)){
            throw new SaleAlreadyRegistredException(
                    String.format("ERRO! Venda com código Nº %s, já está registrada", sale.getSalesCode())
            );
        }

        sales.add(sale);
        sale.addEmplyee(this);
    }

    // ADICIONAR NOVA PEÇA REGISTRADA/CRIADA
    public void addPart(Part part){
        if (registredItems.contains(part)){
            throw new ItemAlreadyRegistredException("ERRO! Peça já foi adicionado na lista de criação d funcionário.");
        }

        registredItems.add(part);
    }
}
