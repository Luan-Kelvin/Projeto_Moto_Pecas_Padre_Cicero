package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import jakarta.validation.constraints.NotNull;

public record EmployeePutDTO(
        @NotNull
        Long id,

        String name,

        String email,

        JobType jobType
) {
}
