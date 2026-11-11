package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.ChangeEmployeeJobDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeeGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeePostDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.employee.EmployeePutDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions.EmployeeAlreadyExistsException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions.EmployeeInactiveException;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.EmployeeRepository;
import com.motopecas.padrecicero.motopecas.padrecicero.security.EmailVerificationService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private Conversor conversor;

    @Mock
    private PasswordEncoder passwordEncoder;


    @InjectMocks
    private EmployeeService employeeService;

    @Test
    @DisplayName("Deve criar um novo funcionário e enviar email de verificação")
    void testRegisterEmployee() throws MessagingException {
        String password = "aaabbb111";

        EmployeePostDTO postDTO = new EmployeePostDTO(
                "John Doe",
                "Jonh@gmail.com",
                "123456",
                JobType.VENDEDOR
        );

        EmployeeGetDTO getDto = new EmployeeGetDTO(
                1L,
                postDTO.name(),
                postDTO.email(),
                postDTO.jobType(),
                true,
                true,
                LocalDate.now()
        );

        when(employeeRepository.findByEmail(postDTO.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(postDTO.password())).thenReturn(password);
        when(conversor.converterEmployee(any(Employee.class))).thenReturn(getDto);

        EmployeeGetDTO employeeGetDTO = employeeService.registerEmployee(postDTO);

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);

        verify(employeeRepository).save(captor.capture());

        Employee employeeSaved = captor.getValue();

        assertEquals(postDTO.name(), employeeSaved.getName());
        assertEquals(postDTO.email(), employeeSaved.getEmail());
        assertEquals(postDTO.jobType(), employeeSaved.getJobType());

        assertEquals(employeeGetDTO.name(), employeeSaved.getName());
        assertEquals(employeeGetDTO.email(), employeeSaved.getEmail());
        assertEquals(employeeGetDTO.jobType(), employeeSaved.getJobType());

        verify(employeeRepository).findByEmail(postDTO.email());
        verify(emailVerificationService).sendVerificationEmail(any(Employee.class));
        verify(passwordEncoder).encode(any(String.class));
        verify(conversor).converterEmployee(any(Employee.class));
    }

    @Test
    @DisplayName("Deve lançar exeção quando o email já estiver cadastrado")
    void testRegisterEmployeeWithExistingEmail() {
        EmployeePostDTO postDTO = new EmployeePostDTO(
                "John Doe",
                "john@gmail.com",
                "123456",
                JobType.VENDEDOR
        );

        when(employeeRepository.findByEmail(postDTO.email())).thenReturn(Optional.of(new Employee()));

        assertThrows(EmployeeAlreadyExistsException.class, () -> employeeService.registerEmployee(postDTO));

        verify(employeeRepository).findByEmail(postDTO.email());
        verifyNoInteractions(emailVerificationService);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(conversor);
    }

    @Test
    @DisplayName("Deve retornar lista com funcionarios cadastrados.")
    void mustListEmployees(){
        Employee employee = new Employee("Mariana", "mama@gmail.com", "12345", JobType.VENDEDOR);
        List<Employee> list = List.of(employee);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(list);

        EmployeeGetDTO employeeGetDTO = new EmployeeGetDTO(
                1L,
                "Mariana",
                "mama@gmail.com",
                JobType.VENDEDOR,
                true,
                true,
                LocalDate.now()
        );

        when(employeeRepository.findAll(pageable)).thenReturn(page);
        when(conversor.converterEmployee(any(Employee.class))).thenReturn(employeeGetDTO);

        Page<EmployeeGetDTO> pageReturn = employeeService.listEmployee(pageable);

        verify(employeeRepository).findAll(pageable);
        verify(conversor).converterEmployee(any(Employee.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia, quando não houver funcionrios cadastrados.")
    void shouldReturnAnEmptyList(){

        Pageable pageable = PageRequest.of(0, 10);

        EmployeeGetDTO employeeGetDTO = new EmployeeGetDTO(
                1L,
                "Mariana",
                "mama@gmail.com",
                JobType.VENDEDOR,
                true,
                true,
                LocalDate.now()
        );

        when(employeeRepository.findAll(pageable)).thenReturn(Page.empty());

        Page<EmployeeGetDTO> pageReturn = employeeService.listEmployee(pageable);

        verify(employeeRepository).findAll(pageable);
        verify(conversor, never()).converterEmployee(any(Employee.class));
    }

    @Test
    @DisplayName("Deve retornar o funcionario de acordo com o ID passado.")
    void shouldReturnEmployeeByID(){
        Employee employee = new Employee("Mariana", "mama@gmail.com", "12345", JobType.VENDEDOR);
        EmployeeGetDTO employeeGetDTO = new EmployeeGetDTO(
                1L,
                "Mariana",
                "mama@gmail.com",
                JobType.VENDEDOR,
                true,
                true,
                LocalDate.now()
        );

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(conversor.converterEmployee(any(Employee.class))).thenReturn(employeeGetDTO);

        EmployeeGetDTO employeeReturn = employeeService.findByIdController(1L);

        assertThat(employee.getName()).isEqualTo(employeeReturn.name());
        assertThat(employee.getEmail()).isEqualTo(employeeReturn.email());
        assertThat(employee.getJobType()).isEqualTo(employeeReturn.jobType());
        assertThat(employeeReturn).isEqualTo(employeeGetDTO);
    }

    @Test
    @DisplayName("Deve atualizar os dados do funcionário.")
    void updateEmployeeData() throws MessagingException {
        Employee employee = new Employee("John Doe", "Jonh@gmail.com", "123456", JobType.VENDEDOR);

        EmployeePutDTO putDTO = new EmployeePutDTO(
                1L,
                "Luan Kelvin",
                "Jonh@gmail.com"
        );

        EmployeeGetDTO getDto = new EmployeeGetDTO(
                1L,
                putDTO.name(),
                putDTO.email(),
                JobType.VENDEDOR,
                true,
                true,
                LocalDate.now()
        );

        when(employeeRepository.findById(putDTO.id())).thenReturn(Optional.of(employee));
        when(conversor.converterEmployee(any(Employee.class))).thenReturn(getDto);

        EmployeeGetDTO employeeReturn = employeeService.updateEmployee(putDTO);

        assertThat(getDto).isEqualTo(employeeReturn);
        assertThat(employeeReturn.name()).isEqualTo(putDTO.name());
    }

    @Test
    @DisplayName("Deve atualizar cargo do funcionário")
    void EmployeeJobTitleMustBeUpdated(){
        ChangeEmployeeJobDTO jobDTO = new ChangeEmployeeJobDTO(1L, JobType.GERENTE);

        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.VENDEDOR);
        employee.toCheck();

        EmployeeGetDTO getDTO = new EmployeeGetDTO(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                jobDTO.newJob(),
                employee.getActive(),
                employee.getVerified(),
                employee.getCreationDate()
        );

        when(employeeRepository.findById(any(Long.class))).thenReturn(Optional.of(employee));
        when(conversor.converterEmployee(any(Employee.class))).thenReturn(getDTO);

        EmployeeGetDTO employeeReturn = employeeService.changeJobTitle(jobDTO);

        assertThat(employeeReturn).isEqualTo(getDTO);
        assertThat(employee.getJobType()).isEqualTo(jobDTO.newJob());
    }

    @Test
    @DisplayName("Deve lançar exceção se Funcionario não estiver ativo.")
    void throwsAnExceptionForAnInactiveEmployee(){
        ChangeEmployeeJobDTO jobDTO = new ChangeEmployeeJobDTO(1L, JobType.GERENTE);

        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.VENDEDOR);
        employee.deActive();

        when(employeeRepository.findById(any(Long.class))).thenReturn(Optional.of(employee));

        assertThrows(EmployeeInactiveException.class,() ->  employeeService.changeJobTitle(jobDTO));

        verify(employeeRepository).findById(any(Long.class));
        verify(conversor, never()).converterEmployee(any(Employee.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se Funcionario não estiver verificado.")
    void throwsAnExceptionForAnUnverifiedEmployee(){
        ChangeEmployeeJobDTO jobDTO = new ChangeEmployeeJobDTO(1L, JobType.GERENTE);

        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.VENDEDOR);

        when(employeeRepository.findById(any(Long.class))).thenReturn(Optional.of(employee));

        assertThrows(EmployeeInactiveException.class,() ->  employeeService.changeJobTitle(jobDTO));

        verify(employeeRepository).findById(any(Long.class));
        verify(conversor, never()).converterEmployee(any(Employee.class));
    }

    @Test
    @DisplayName("Deve chamar metodo toCheck de Employee para marcar Employee como verificado")
    void shouldMarkAsVerified(){
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.VENDEDOR);
        String token = "123456789";

        when(employeeRepository.findByVerificationToken(token)).thenReturn(Optional.of(employee));

        employeeService.verifyEmail(token);

        verify(employeeRepository).findByVerificationToken(token);

        assertThat(employee.getVerified()).isTrue();
    }




}
