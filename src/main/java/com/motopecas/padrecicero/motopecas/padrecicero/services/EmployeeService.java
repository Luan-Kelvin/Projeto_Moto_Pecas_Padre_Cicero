package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.ChangeEmployeeJobDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeeGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeePostDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeePutDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions.EmployeeAlreadyExistsException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions.EmployeeInactiveException;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    @Transactional
    public Page<EmployeeGetDTO> listEmployee(Pageable pageable){
        Page<Employee> list = employeeRepository.findAll(pageable);

        return list.map(conversor::converterEmployee);
    }

    // BUSCAR FUNCIONÁRIO ATRAVÉS DO ID (CONTROLLER)
    @Transactional
    public EmployeeGetDTO findByIdController(Long id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(
                        () -> new EmployeeNoFoundException(String.format("ERRO! Funcionário com ID = %s, não foi encontrado.", id))
                );

        return conversor.converterEmployee(employee);
    }

    // BUSCAR FUNCIONÁRIO ATRAVÉS DO ID (DEMAIS CLASSES DE SERVIÇO)
    @Transactional
    public Employee findById(Long id){
        return employeeRepository.findById(id)
                .orElseThrow(
                        () -> new EmployeeNoFoundException(String.format("ERRO! Funcionário com ID = %s, não foi encontrado.", id))
                );
    }

    // ATUALIZAR PERFIL DO FUNCIONÁRIO
    @Transactional
    public EmployeeGetDTO updateEmployee(EmployeePutDTO dto) throws MessagingException {
        Employee employee = employeeRepository.findById(dto.id())
                .orElseThrow(() -> new EmployeeNoFoundException(String.format("ERRO! Funcionário com ID = %s, não foi encontrado.", dto.id())
                ));

        if (dto.name() != null){
            employee.setName(dto.name());
        }

        boolean emailChanged = dto.email() != null && !dto.email().equalsIgnoreCase(employee.getEmail());

        if (emailChanged){

            if (employeeRepository.existsByEmailAndIdNot(dto.email(), dto.id())){
                throw new EmployeeAlreadyExistsException(
                        String.format("ERRO! Email = %s, já esta cadastrado.", dto.email()
                        ));
            }

            employee.changeEmail(dto.email());
        }

        if (dto.jobType() != null){
            employee.setJobType(dto.jobType());
        }

        if (emailChanged){
            emailService.sendVerificationEmail(employee);
        }

        return conversor.converterEmployee(employee);

    }

    // ALTERAR CARGO
    @Transactional
    public EmployeeGetDTO changeJobTitle(ChangeEmployeeJobDTO dto){
        Employee employee = employeeRepository.findById(dto.id())
                .orElseThrow(() -> new EmployeeNoFoundException(String.format("ERRO! Funcionário com ID = %s, não foi encontrado.", dto.id())
                ));

        if (!Boolean.TRUE.equals(employee.getActive()) || !Boolean.TRUE.equals(employee.getVerified())){
            throw new EmployeeInactiveException("ERRO! Funcionário inátivo ou não verificado.");
        }

        boolean changedJob = !dto.newJob().equals(employee.getJobType());

        if (changedJob){
            employee.setJobType(dto.newJob());
        }

        return conversor.converterEmployee(employee);
    }

    // MARCAR FUNCIONÁRIO COMO VERIFICADO
    @Transactional
    public void verifyEmail(String token){
        Employee employee = employeeRepository.findByVerificationToken(token)
                .orElseThrow(() -> new EmployeeNoFoundException("ERRO! Não foi encontrado nenhum funcionário com token informado."));

        employee.toCheck();
    }


}
