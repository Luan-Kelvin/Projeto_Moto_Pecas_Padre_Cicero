package com.motopecas.padrecicero.motopecas.padrecicero.controller.employee;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeeGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.services.EmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
@SecurityRequirement(name = "bearer-key")
public class EmployeeControllerGet {

    @Autowired
    private EmployeeService employeeService;

    // BUSCAR FUNCIONARIO POR ID
    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<EmployeeGetDTO> findById(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(employeeService.findByIdController(id));
    }

    //LISTAR TODOS OS FUNCIONARIOS CADASTRADOS
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<EmployeeGetDTO>> listEmployees(Pageable pageable){
        return ResponseEntity.ok().body(employeeService.listEmployee(pageable));
    }

    // CONSULTAR A PRÓPRIA CONTA
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'FUNCIONARIO')")
    public ResponseEntity<EmployeeGetDTO> findMyProfile(@AuthenticationPrincipal Employee employee){
        return ResponseEntity.ok().body(employeeService.findByIdController(employee.getId()));
    }

}
