package com.motopecas.padrecicero.motopecas.padrecicero.controller.employee;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeeGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeePostDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.services.EmployeeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/employee")
public class EmployeeControllerPost {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/auth/verify-account")
    public ResponseEntity<String> verifyEmail(@RequestParam String token){
        employeeService.verifyEmail(token);

        return ResponseEntity.ok().body("Funcionário verificado com sucesso!");
    }

    // APAGUE ESSE ENDPOIJT QUANDO FINALIZAR PROJETO
    @PostMapping("/auth/create")
    public ResponseEntity<EmployeeGetDTO> createEmployeeTest(@RequestBody @Valid EmployeePostDTO dto) throws MessagingException {
        EmployeeGetDTO employeeGetDTO = employeeService.registerEmployee(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("id/{id}")
                .buildAndExpand(employeeGetDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(employeeGetDTO);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<EmployeeGetDTO> registerEmployee(@RequestBody @Valid EmployeePostDTO dto) throws MessagingException {
        EmployeeGetDTO employeeGetDTO = employeeService.registerEmployee(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(employeeGetDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(employeeGetDTO);
    }

}
