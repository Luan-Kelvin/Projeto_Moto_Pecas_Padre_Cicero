package com.motopecas.padrecicero.motopecas.padrecicero.controller.login;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.login.LoginDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.login.RefreshTokeDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.login.TokenAcessDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.login.TokenResponseDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.security.TokenService;
import com.motopecas.padrecicero.motopecas.padrecicero.services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/auth/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginDTO login){
        return ResponseEntity.ok().body(tokenService.authenticate(login));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<TokenAcessDTO> refresh(@RequestParam RefreshTokeDTO refreshTokeDTO){
        Long getSubject = Long.valueOf(tokenService.validateRefreshToken(refreshTokeDTO.refreshToken()));

        Employee employee = employeeService.findById(getSubject);

        return ResponseEntity.ok().body(new TokenAcessDTO(tokenService.generateToken(employee)));
    }
}
