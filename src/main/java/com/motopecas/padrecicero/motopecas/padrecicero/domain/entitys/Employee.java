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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "employees")
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor
public class Employee implements UserDetails {

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
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    private JobType jobType;

    @NotNull
    private Boolean active;

    @NotNull
    private Boolean verified;

    private String verificationToken;

    private LocalDateTime verificationTokenExpiration;

    private LocalDate creationDate;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<Sale> sales = new ArrayList<>();

    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    private List<Part> registredItems = new ArrayList<>();

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<InventoryMovement> movements = new ArrayList<>();

    public Employee(String name, String email, String password, JobType jobType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.jobType = jobType;
        this.active = true;
        this.verified = false;
        this.verificationToken = null;
        this.verificationTokenExpiration = null;
        this.creationDate = LocalDate.now();
    }

    // ADICIONAR NOVA VENDA
    public void addSale(Sale sale){
        if (sales.contains(sale)){
            throw new SaleAlreadyRegistredException(
                    String.format("ERRO! Venda com código Nº %s, já está registrada", sale.getSaleCode())
            );
        }

        sales.add(sale);

        if (sale.getEmployee() != this){
            sale.addEmployee(this);
        }
    }

    // ADICIONAR NOVA PEÇA REGISTRADA/CRIADA
    public void addPart(Part part){
        if (registredItems.contains(part)){
            throw new ItemAlreadyRegistredException("ERRO! Peça já foi adicionado na lista de criação d funcionário.");
        }

        registredItems.add(part);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
