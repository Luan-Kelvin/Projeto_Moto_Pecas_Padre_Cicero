package com.motopecas.padrecicero.motopecas.padrecicero.controller.employee;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.AccountStatusDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.ChangeEmployeeJobDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeeGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
public class EmployeeControllerPatch {

    @Autowired
    private EmployeeService employeeService;

    @PatchMapping("/update/cargo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<EmployeeGetDTO> updateEmployeeCargo(@RequestBody ChangeEmployeeJobDTO dto) {
        return ResponseEntity.ok().body(employeeService.changeJobTitle(dto));
    }

    @PatchMapping("/update/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<EmployeeGetDTO> updateEmployeeStatus(@RequestBody AccountStatusDTO dto) {
        return ResponseEntity.ok().body(employeeService.activateAndDesactivateEmployee(dto));
    }
}
