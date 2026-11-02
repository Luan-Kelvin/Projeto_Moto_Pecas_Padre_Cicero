package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeeGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import org.springframework.stereotype.Service;

@Service
public class Conversor {

    public EmployeeGetDTO converterEmployee(Employee employee){
        return new EmployeeGetDTO(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getJobType(),
                employee.getActive(),
                employee.getVerified(),
                employee.getCreationDate()
        );
    }
}
