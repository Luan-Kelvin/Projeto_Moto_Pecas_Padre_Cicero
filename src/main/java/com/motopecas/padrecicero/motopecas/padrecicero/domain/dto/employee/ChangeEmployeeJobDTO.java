package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import jakarta.validation.constraints.NotNull;

public record ChangeEmployeeJobDTO(

        @NotNull
        Long id,

        @NotNull
        JobType newJob
) {
}
