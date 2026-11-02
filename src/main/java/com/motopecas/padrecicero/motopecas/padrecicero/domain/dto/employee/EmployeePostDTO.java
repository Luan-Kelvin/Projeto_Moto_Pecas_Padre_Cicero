package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmployeePostDTO(
        @NotBlank
        String name,

        @NotBlank
        @Pattern(
                regexp = "^[A-Za-zA-ÿ,. -&%$_0-9]+@[A-Za-zA-ÿ0-9]+\\.[a-zA-Z]{2,}$",
                message = "Email informado tem o formato inválido."
        )
        String email,

        @NotBlank
        @Min(value = 5, message = "Senha deve ter no mínimo 5 caractere.")
        String password,

        @NotBlank
        JobType jobType
) {
}
