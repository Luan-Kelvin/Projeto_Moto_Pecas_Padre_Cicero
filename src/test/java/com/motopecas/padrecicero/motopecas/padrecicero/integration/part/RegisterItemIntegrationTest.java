package com.motopecas.padrecicero.motopecas.padrecicero.integration.part;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartPostDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.EmployeeRepository;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.PartRepository;
import com.motopecas.padrecicero.motopecas.padrecicero.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class RegisterItemIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    String adminToken;
    String sellerToken;

    @BeforeEach
    void setup(){

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS part_code_sequence (id BIGINT PRIMARY KEY, `value` BIGINT NOT NULL)");
        jdbcTemplate.update("MERGE INTO part_code_sequence KEY(id) VALUES (1, 1000)");

        // CRIA FUNCIONARIO PARA CADASTRAR PEÇA
        Employee employee = new Employee(
                "Luan Kelvin",
                "luan@gmail.com",
                passwordEncoder.encode("12345"),
                JobType.ADMIN
        );

        ReflectionTestUtils.setField(employee, "verified", true);
        employee.activate();

        employeeRepository.save(employee);

        adminToken = tokenService.generateToken(employee);

        Employee seller = new Employee(
                "Marcio Victor",
                "mama@gmail.com",
                passwordEncoder.encode("112233"),
                JobType.VENDEDOR
        );

        ReflectionTestUtils.setField(seller, "verified", true);
        seller.activate();

        employeeRepository.save(seller);

        sellerToken = tokenService.generateToken(seller);
    }

    @Test
    @DisplayName("Deve cadastrar nova peça")
    void registerNewPart() throws Exception {
        PartPostDTO partPostDTO = new PartPostDTO(
                "Patim Freio",
                "Titan 150 todas",
                "Cobreq",
                PartCategory.FREIO,
                BigDecimal.valueOf(13.5),
                BigDecimal.valueOf(33.9),
                5,
                30
        );

        MvcResult result =  mvc.perform(
                post("/part/register")
                        .header("Authorization", "Bearer "+adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(partPostDTO))
        ).andExpect(status().isCreated()).andReturn();

        PartGetDTO partGetDTO = mapper.readValue(result.getResponse().getContentAsString(), PartGetDTO.class);

        Part part = partRepository.findByPartCode(partGetDTO.partCode()).orElseThrow();

        assertThat(part.getPartCode()).isEqualTo(partGetDTO.partCode());
        assertThat(part.getName()).isEqualTo(partGetDTO.name());
        assertThat(part.getApplication()).isEqualTo(partGetDTO.application());
    }

    @Test
    @DisplayName("Proibir funcionario sem autorização de cadastrar peça")
    void registerPartWithoutAuthorization() throws Exception {
        PartPostDTO partPostDTO = new PartPostDTO(
                "Patim Freio",
                "Titan 150 todas",
                "Cobreq",
                PartCategory.FREIO,
                BigDecimal.valueOf(13.5),
                BigDecimal.valueOf(33.9),
                5,
                30
        );

        mvc.perform(
                post("/part/register")
                        .header("Authorization", "Bearer "+sellerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(partPostDTO))
        ).andExpect(status().isForbidden());
    }
}
