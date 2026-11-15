package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock.InventoryAdjustamentDataDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock.MovementAdjustmnetResponseDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock.MovementDataDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.InventoryMovement;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Stock;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.InputStatus;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.MovementType;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.stockExceptions.InvalidNumberOfPiecesException;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private PartService partService;

    @Mock
    private InvetoryMovementService invetoryMovementService;

    @InjectMocks
    private StockService stockService;

    @Test
    @DisplayName("Deve consultar a quantidade atual do estoque")
    void shouldReturnCurrentStockQuantity() {
        when(stockRepository.findByCurrentQuantity(1L)).thenReturn(12);

        Integer quantity = stockService.checkQuantity(1L);

        assertThat(quantity).isEqualTo(12);
        verify(stockRepository).findByCurrentQuantity(1L);
    }

    @Test
    @DisplayName("Deve registrar entrada, atualizar quantidade e salvar movimentação")
    void shouldRegisterEntryAndUpdateStock() {
        Long partId = 1L;
        Long employeeId = 2L;
        Employee employee = createEmployee();
        Stock stock = createStock(10, 3L);
        Part part = createPart(stock);
        MovementDataDTO input = new MovementDataDTO(partId, 5);

        when(partService.findById(partId)).thenReturn(part);
        when(employeeService.findById(employeeId)).thenReturn(employee);

        stockService.registerEntry(input, employeeId);

        ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
        verify(invetoryMovementService).saveMovement(movementCaptor.capture());
        InventoryMovement movement = movementCaptor.getValue();

        assertThat(stock.getCurrentQuantity()).isEqualTo(15);
        assertThat(stock.getLastUpdate()).isNotNull();
        assertThat(movement.getStock()).isSameAs(stock);
        assertThat(movement.getMovementType()).isEqualTo(MovementType.ENTRADA);
        assertThat(movement.getQuantity()).isEqualTo(5);
        assertThat(movement.getReason()).isNull();
        assertThat(movement.getEmployee()).isSameAs(employee);

        verify(partService).findById(partId);
        verify(employeeService).findById(employeeId);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    @DisplayName("Deve rejeitar quantidade nula, zero ou negativa ao registrar entrada")
    void shouldRejectInvalidEntryQuantity(Integer quantity) {
        assertThrows(IllegalArgumentException.class,
                () -> stockService.registerEntry(new MovementDataDTO(1L, quantity), 2L));

        verifyNoInteractions(partService, employeeService, invetoryMovementService);
    }

    @Test
    @DisplayName("Deve registrar saída, atualizar quantidade e salvar movimentação")
    void shouldRecordDepartureAndUpdateStock() {
        Long partId = 1L;
        Long employeeId = 2L;
        Employee employee = createEmployee();
        Stock stock = createStock(10, 3L);
        Part part = createPart(stock);
        MovementDataDTO input = new MovementDataDTO(partId, 4);

        when(partService.findById(partId)).thenReturn(part);
        when(employeeService.findById(employeeId)).thenReturn(employee);

        stockService.recordDeparture(input, employeeId);

        ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
        verify(invetoryMovementService).saveMovement(movementCaptor.capture());
        InventoryMovement movement = movementCaptor.getValue();

        assertThat(stock.getCurrentQuantity()).isEqualTo(6);
        assertThat(stock.getLastUpdate()).isNotNull();
        assertThat(movement.getStock()).isSameAs(stock);
        assertThat(movement.getMovementType()).isEqualTo(MovementType.SAIDA);
        assertThat(movement.getQuantity()).isEqualTo(4);
        assertThat(movement.getReason()).isNull();
        assertThat(movement.getEmployee()).isSameAs(employee);
        verify(partService).findById(partId);
        verify(employeeService).findById(employeeId);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    @DisplayName("Deve rejeitar quantidade nula, zero ou negativa ao registrar saída")
    void shouldRejectInvalidDepartureQuantity(Integer quantity) {
        assertThrows(IllegalArgumentException.class,
                () -> stockService.recordDeparture(new MovementDataDTO(1L, quantity), 2L));

        verifyNoInteractions(partService, employeeService, invetoryMovementService);
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar saída maior que o estoque disponível")
    void shouldRejectDepartureGreaterThanAvailableStock() {
        Long partId = 1L;
        Long employeeId = 2L;
        Stock stock = createStock(3, 3L);
        Part part = createPart(stock);

        when(partService.findById(partId)).thenReturn(part);
        when(employeeService.findById(employeeId)).thenReturn(createEmployee());

        assertThrows(InvalidNumberOfPiecesException.class,
                () -> stockService.recordDeparture(new MovementDataDTO(partId, 4), employeeId));

        assertThat(stock.getCurrentQuantity()).isEqualTo(3);
        verify(invetoryMovementService, never()).saveMovement(any(InventoryMovement.class));
    }

    @Test
    @DisplayName("Deve ajustar quantidade e retornar os dados da movimentação")
    void shouldPerformStockAdjustment() {
        Long partId = 1L;
        Long employeeId = 2L;
        Employee employee = createEmployee();
        Stock stock = createStock(4, 3L);
        Part part = createPart(stock);
        InventoryAdjustamentDataDTO input = new InventoryAdjustamentDataDTO(partId, 9, "Inventário físico");

        when(partService.findById(partId)).thenReturn(part);
        when(employeeService.findById(employeeId)).thenReturn(employee);
        doAnswer(invocation -> {
            InventoryMovement movement = invocation.getArgument(0);
            ReflectionTestUtils.setField(movement, "id", 7L);
            return null;
        }).when(invetoryMovementService).saveMovement(any(InventoryMovement.class));

        MovementAdjustmnetResponseDTO response = stockService.performAdjustament(input, employeeId);

        ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
        verify(invetoryMovementService).saveMovement(movementCaptor.capture());
        InventoryMovement movement = movementCaptor.getValue();

        assertThat(stock.getCurrentQuantity()).isEqualTo(9);
        assertThat(stock.getLastUpdate()).isNotNull();
        assertThat(movement.getStock()).isSameAs(stock);
        assertThat(movement.getMovementType()).isEqualTo(MovementType.AJUSTE);
        assertThat(movement.getQuantity()).isEqualTo(9);
        assertThat(movement.getReason()).isEqualTo("Inventário físico");
        assertThat(movement.getEmployee()).isSameAs(employee);
        assertThat(response.status()).isEqualTo(InputStatus.SUCCESS);
        assertThat(response.type()).isEqualTo(MovementType.AJUSTE);
        assertThat(response.quantity()).isEqualTo(9);
        assertThat(response.reason()).isEqualTo("Inventário físico");
        assertThat(response.idStock()).isEqualTo(3L);
        assertThat(response.part()).isEqualTo("Pastilha de freio");
        assertThat(response.currentQuantity()).isEqualTo(9);
        assertThat(response.idMovement()).isEqualTo(7L);
        verify(partService).findById(partId);
        verify(employeeService).findById(employeeId);
    }

    @Test
    @DisplayName("Deve informar disponibilidade quando há peças em estoque")
    void shouldReturnAvailableWhenStockHasItems() {
        Stock stock = createStock(1, 3L);
        Part part = createPart(stock);
        when(partService.findById(1L)).thenReturn(part);

        Boolean available = stockService.validateAvailability(1L);

        assertThat(available).isTrue();
        verify(partService).findById(1L);
    }

    @Test
    @DisplayName("Deve informar indisponibilidade quando o estoque está vazio")
    void shouldReturnUnavailableWhenStockIsEmpty() {
        Stock stock = createStock(0, 3L);
        Part part = createPart(stock);
        when(partService.findById(1L)).thenReturn(part);

        Boolean available = stockService.validateAvailability(1L);

        assertThat(available).isFalse();
        verify(partService).findById(1L);
    }

    private Employee createEmployee() {
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345678", JobType.VENDEDOR);
        ReflectionTestUtils.setField(employee, "id", 2L);
        return employee;
    }

    private Stock createStock(Integer quantity, Long id) {
        Stock stock = new Stock();
        stock.setCurrentQuantity(quantity);
        ReflectionTestUtils.setField(stock, "id", id);
        return stock;
    }

    private Part createPart(Stock stock) {
        Employee creator = createEmployee();
        Part part = new Part(
                "Pastilha de freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "10201",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(15),
                5,
                creator
        );
        ReflectionTestUtils.setField(part, "id", 1L);
        part.addStock(stock);
        return part;
    }
}
