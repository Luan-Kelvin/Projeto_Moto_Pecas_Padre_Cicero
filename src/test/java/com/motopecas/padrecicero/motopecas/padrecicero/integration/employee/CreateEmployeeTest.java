package com.motopecas.padrecicero.motopecas.padrecicero.integration.employee;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.EmployeeRepository;
import com.motopecas.padrecicero.motopecas.padrecicero.security.EmailVerificationService;
import com.motopecas.padrecicero.motopecas.padrecicero.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CreateEmployeeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @MockitoBean
    private EmailVerificationService emailService;

    String adminToken;
    String sellerToken;

    @BeforeEach
    void setup(){

        // CRIAR ADMINISTRADOR
        Employee employeeAdmin = new Employee(
                "Joaquim",
                "Jojo@gmail.com",
                passwordEncoder.encode("12345"),
                JobType.ADMIN
        );

        ReflectionTestUtils.setField(employeeAdmin, "verified", true);
        employeeAdmin.activate();

        employeeRepository.save(employeeAdmin);
        adminToken = tokenService.generateToken(employeeAdmin);

        // CRIAR VENDEDOR
        Employee employeeSeller = new Employee(
                "Marcio",
                "mama@gmail.com",
                passwordEncoder.encode("12345"),
                JobType.VENDEDOR
        );

        ReflectionTestUtils.setField(employeeSeller, "verified", true);
        employeeSeller.activate();

        employeeRepository.save(employeeSeller);
        sellerToken = tokenService.generateToken(employeeSeller);
    }

    @Test
    @DisplayName("Deve cadastrar um novo funcionário.")
    void createNewEmployee() throws Exception {
        String json = String.format("""
                {
                    "name": "%s",
                    "email": "%s",
                    "password": "%s",
                    "jobType": "%s"
                }
                """, "Gustavo Guanabara", "gustavo.guanabara@email.com", "123456", "VENDEDOR");

        mockMvc.perform(
                post("/employee/register")
                        .header("Authorization", "Bearer " +adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Gustavo Guanabara"))
                .andExpect(jsonPath("$.email").value("gustavo.guanabara@email.com"));

        Employee employee = employeeRepository.findByEmail("gustavo.guanabara@email.com").orElseThrow();

        assertThat(passwordEncoder.matches("123456", employee.getPassword())).isTrue();

        verify(emailService, times(1)).sendVerificationEmail(employee);

    }

    @Test
    @DisplayName("Deve impedir que VENDEDOR cadastre novo funcionário")
    void preventRegistrationMadeByASalesperson() throws Exception {
        String json = String.format("""
                {
                    "name": "%s",
                    "email": "%s",
                    "password": "%s",
                    "jobType": "%s"
                }
                """, "Gustavo Guanabara", "gustavo.guanabara@email.com", "123456", "VENDEDOR");

        mockMvc.perform(
                post("/employee/register")
                        .header("Authorization", "Bearer " +sellerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve permitir cadastro sem token")
    void preventRegistrationWithoutAToken() throws Exception {
        String json = String.format("""
                {
                    "name": "%s",
                    "email": "%s",
                    "password": "%s",
                    "jobType": "%s"
                }
                """, "Gustavo Guanabara", "gustavo.guanabara@email.com", "123456", "VENDEDOR");

        mockMvc.perform(
                        post("/employee/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                )
                .andExpect(status().isForbidden());
    }
}
