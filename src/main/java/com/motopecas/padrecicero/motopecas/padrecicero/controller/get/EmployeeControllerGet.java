package com.motopecas.padrecicero.motopecas.padrecicero.controller.get;

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

    @GetMapping("/verify-account")
    public ResponseEntity<String> verifyEmail(@RequestParam String token){
        employeeService.verifyEmail(token);

        return ResponseEntity.ok().body("Funcionário verificado com sucesso!");
    }

}
