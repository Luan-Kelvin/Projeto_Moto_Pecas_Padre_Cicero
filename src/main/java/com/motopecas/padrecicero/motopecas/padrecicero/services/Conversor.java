package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeeGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
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

    public PartGetDTO converterPart(Part part){
        return new PartGetDTO(
                part.getId(),
                part.getPartCode(),
                part.getName(),
                part.getDescription(),
                part.getMark(),
                part.getCategory(),
                part.getCostPrice(),
                part.getSellingPrice(),
                part.getActive(),
                part.getCreationDate(),
                part.getCreator().getId(),
                part.getStock().getId()
        );
    }
}
