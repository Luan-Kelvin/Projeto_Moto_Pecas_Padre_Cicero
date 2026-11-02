package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions.EmployeeNoFoundException;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    // Quando criar metodo pra salvar Employee lembre de verificar emial no metodo e salvar a a entidade antes de chamar essa verificação

    private final EmployeeRepository employeeRepository;

    @Transactional
    public void verifyEmail(String token){
        Employee employee = employeeRepository.findByVerificationToken(token)
                .orElseThrow(() -> new EmployeeNoFoundException("ERRO! Não foi encontrado nenhum funcionário com token informado."));

        employee.toCheck();
    }
}
