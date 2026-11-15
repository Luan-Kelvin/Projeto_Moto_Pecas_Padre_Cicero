package com.motopecas.padrecicero.motopecas.padrecicero.controller.employee;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeeGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
public class EmployeeControllerGet {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/id/{id}")
    public ResponseEntity<EmployeeGetDTO> findById(@RequestParam("id") Long id){
        return ResponseEntity.ok().body(employeeService.findByIdController(id));
    }
}
