package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeeGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeePostDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions.EmployeeAlreadyExistsException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions.EmployeeNoFoundException;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.EmployeeRepository;
import com.motopecas.padrecicero.motopecas.padrecicero.security.EmailVerificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    // Quando criar metodo pra salvar Employee lembre de verificar emial no metodo e salvar a a entidade antes de chamar essa verificação

    private final EmployeeRepository employeeRepository;
    private final EmailVerificationService emailService;
    private final PasswordEncoder passwordEncoder;
    private final Conversor conversor;

    // CADASTRAR NOVO FUNCIONÁRIO
    @Transactional
    public EmployeeGetDTO registerEmployee(EmployeePostDTO employeDto) throws MessagingException {
        Optional<Employee> employeeRep = employeeRepository.findByEmail(employeDto.email());

        if (employeeRep.isPresent()){
            throw new EmployeeAlreadyExistsException(
                    String.format("ERRO! Funcionário com email %s, já está cadastrado.", employeDto.email())
            );
        }

        String passwordCryp = passwordEncoder.encode(employeDto.password());

        Employee employee = new Employee(employeDto.name(), employeDto.email(), passwordCryp, employeDto.jobType());

        employeeRepository.save(employee);

        emailService.sendVerificationEmail(employee);

        return conversor.converterEmployee(employee);
    }

    // LISTAR FUNCIONÁRIOS
    public Page<EmployeeGetDTO> listEmployee(Pageable pageable){
        Page<Employee> list = employeeRepository.findAll(pageable);

        return list.map(conversor::converterEmployee);
    }

    // MARCAR FUNCIONÁRIO COMO VERIFICADO
    @Transactional
    public void verifyEmail(String token){
        Employee employee = employeeRepository.findByVerificationToken(token)
                .orElseThrow(() -> new EmployeeNoFoundException("ERRO! Não foi encontrado nenhum funcionário com token informado."));

        employee.toCheck();
    }


}
