package com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;

import java.time.LocalDate;

public record EmployeeGetDTO(
        Long id,
        String name,
        String email,
        JobType jobType,
        Boolean active,
        Boolean verified,
        LocalDate creationDate
) {
}
