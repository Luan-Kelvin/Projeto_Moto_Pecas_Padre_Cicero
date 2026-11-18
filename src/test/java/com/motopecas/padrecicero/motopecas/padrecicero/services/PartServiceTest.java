package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartMovementDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartPostDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartPutDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.InventoryMovement;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Stock;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.JobType;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.MovementType;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PartCategory;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.partExceptions.PartAlreadyExistsException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.partExceptions.PartNotFoundException;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.InventoryMovementRepository;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.PartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PartServiceTest {

    @Mock
    private PartRepository partRepository;

    @Mock
    private InventoryMovementRepository inventoryMovementRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private StockService stockService;

    @Mock
    private Conversor conversor;

    @InjectMocks
    private PartService partService;

    @Test
    @DisplayName("Deve registrar nova peça")
    void registerNewPart(){
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);

        PartPostDTO postDTO = new PartPostDTO(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                BigDecimal.valueOf(17.9 ),
                BigDecimal.valueOf(33.9),
                10,
                25
        );

        PartGetDTO partGetDto = new PartGetDTO(
                1L,
                "12345",
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                true,
                LocalDate.now(),
                1L,
                25
        );

        when(partRepository.existsByNameAndMark(postDTO.name(), postDTO.mark())).thenReturn(false);
        when(partRepository.generatePartCode()).thenReturn(12345L);
        when(employeeService.findById(any(Long.class))).thenReturn(employee);
        when(conversor.converterPart(any(Part.class))).thenReturn(partGetDto);

        PartGetDTO partReturn = partService.registerPart(postDTO, 1L);

        ArgumentCaptor<Part> captor = ArgumentCaptor.forClass(Part.class);
        ArgumentCaptor<Stock> stockCaptor = ArgumentCaptor.forClass(Stock.class);
        InOrder saveOrder = inOrder(partRepository, stockService);
        saveOrder.verify(partRepository).save(captor.capture());
        saveOrder.verify(stockService).saveStock(stockCaptor.capture());

        Part partSaved = captor.getValue();
        Stock stockSaved = stockCaptor.getValue();

        assertThat(partSaved.getName()).isEqualTo(postDTO.name());
        assertThat(partSaved.getMark()).isEqualTo(postDTO.mark());
        assertThat(partSaved.getCategory()).isEqualTo(postDTO.category());
        assertThat(partSaved.getCostPrice()).isEqualByComparingTo(postDTO.costPrice());
        assertThat(partSaved.getSellingPrice()).isEqualByComparingTo(postDTO.sellingPrice());
        assertThat(partSaved.getMinimumStock()).isEqualTo(postDTO.minimumStock());
        assertThat(partSaved.getCreator()).isSameAs(employee);
        assertThat(partSaved.getPartCode()).isNotBlank();
        assertThat(stockSaved.getPart()).isSameAs(partSaved);
        assertThat(stockSaved.getCurrentQuantity()).isEqualTo(postDTO.quantity());

        assertThat(partReturn.id()).isEqualTo(1L);
        assertThat(partReturn.name()).isEqualTo(postDTO.name());
        assertThat(partReturn.mark()).isEqualTo(postDTO.mark());
        assertThat(partReturn.name()).isEqualTo(partSaved.getName());
        assertThat(partReturn.mark()).isEqualTo(partSaved.getMark());
        assertThat(partReturn.category()).isEqualTo(partSaved.getCategory());
        assertThat(partReturn.costPrice()).isEqualTo(partSaved.getCostPrice());
        assertThat(partReturn.sellingPrice()).isEqualTo(partSaved.getSellingPrice());
        assertThat(partReturn.quantity()).isEqualTo(postDTO.quantity());

        verify(partRepository).existsByNameAndMark(postDTO.name(), postDTO.mark());
        verify(employeeService).findById(any(Long.class));
        verify(conversor).converterPart(any(Part.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar uma peça existente")
    void throwsExceptionIfRequestedPartExists(){
        PartPostDTO postDTO = new PartPostDTO(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                BigDecimal.valueOf(17.9 ),
                BigDecimal.valueOf(33.9),
                10,
                25
        );

        when(partRepository.existsByNameAndMark(postDTO.name(), postDTO.mark())).thenReturn(true);

        assertThrows(PartAlreadyExistsException.class, () -> partService.registerPart(postDTO, 1L));

        verify(partRepository).existsByNameAndMark(postDTO.name(), postDTO.mark());

        verify(employeeService, never()).findById(anyLong());
        verify(partRepository, never()).save(any(Part.class));
        verify(conversor, never()).converterPart(any(Part.class));
    }

    @Test
    @DisplayName("Deve listar as movimentações de uma peça")
    void shouldListPartMovements(){
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);
        Part part = new Part(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "12345",
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                10,
                employee
        );
        Stock stock = new Stock(part);
        InventoryMovement movement = new InventoryMovement(
                stock, MovementType.ENTRADA, 0, 5, null, employee
        );
        part.addMovement(movement);
        PartMovementDTO movementDTO = new PartMovementDTO(
                null,
                MovementType.ENTRADA,
                5,
                0,
                5,
                null,
                null,
                employee.getName(),
                movement.getTransactionDate()
        );

        when(partRepository.findById(1L)).thenReturn(Optional.of(part));
        when(conversor.converterPartMovement(movement)).thenReturn(movementDTO);

        Pageable pageable = PageRequest.of(0, 1, Sort.by("transactionDate").descending());
        when(inventoryMovementRepository.findByPartId(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(movement), pageable, 1));

        Page<PartMovementDTO> result = partService.listPartMovements(1L, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst()).isEqualTo(movementDTO);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(1);
        verify(inventoryMovementRepository).findByPartId(1L, pageable);
        verify(conversor).converterPartMovement(movement);
    }

    @Test
    @DisplayName("Deve retornar lista com todas as peças cadastradas.")
    void listParts(){
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);

        Part part = new Part(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "12345",
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                10,
                employee
        );

        List<Part> parts = List.of(part);

        Pageable pageable = PageRequest.of(0, 1);
        Page<Part> pagePart = new PageImpl<>(parts);

        PartGetDTO partGetDto = new PartGetDTO(
                1L,
                "12345",
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                true,
                LocalDate.now(),
                1L,
                1
        );

        when(partRepository.findAll(pageable)).thenReturn(pagePart);
        when(conversor.converterPart(any(Part.class))).thenReturn(partGetDto);

        Page<PartGetDTO> pageReturn = partService.listParts(pageable);

        assertThat(pageReturn).isNotNull();
        assertThat(pageReturn.getTotalElements()).isEqualTo(1);
        assertThat(pageReturn.getContent().getFirst().name()).isEqualTo(partGetDto.name());
        assertThat(pageReturn.getContent().getFirst().partCode()).isEqualTo(partGetDto.partCode());
        assertThat(pageReturn.getNumber()).isEqualTo(0);
        assertThat(pageReturn.getSize()).isEqualTo(1);

        verify(partRepository).findAll(pageable);
        verify(conversor).converterPart(any(Part.class));

    }

    @Test
    @DisplayName("Deve retornar Peça por ID")
    void shouldReturnAnItemById(){
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);

        Part part = new Part(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "12345",
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                10,
                employee
        );

        PartGetDTO partGetDto = new PartGetDTO(
                1L,
                "12345",
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                true,
                LocalDate.now(),
                1L,
                1
        );

        when(partRepository.findById(1L)).thenReturn(Optional.of(part));
        when(conversor.converterPart(part)).thenReturn(partGetDto);

        PartGetDTO partReturn = partService.findByIdController(1L);

        assertThat(partReturn).isNotNull();
        assertThat(partReturn.id()).isEqualTo(1L);
        assertThat(partReturn.name()).isEqualTo(part.getName());
        assertThat(partReturn.mark()).isEqualTo(part.getMark());
        assertThat(partReturn.costPrice()).isEqualByComparingTo(part.getCostPrice());

        verify(partRepository).findById(1L);
        verify(conversor).converterPart(part);
    }

    @Test
    @DisplayName("Exibir todas as peças que contenham o nome digitado")
    void shouldDisplayAllPartsThatContainTheGivenName(){
        String name = "Patim";

        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);

        Part part = new Part(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "12345",
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                10,
                employee
        );

        List<Part> parts = List.of(part);

        Pageable pageable = PageRequest.of(0, 1);
        Page<Part> pagePart = new PageImpl<>(parts);

        PartGetDTO partGetDto = new PartGetDTO(
                1L,
                "12345",
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                true,
                LocalDate.now(),
                1L,
                1
        );

        when(partRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable)))
                .thenReturn(pagePart);
        when(conversor.converterPart(any(Part.class))).thenReturn(partGetDto);

        Page<PartGetDTO> pageReturn = partService.searchPartByName(name, pageable);

        assertThat(pageReturn).isNotNull();
        assertThat(pageReturn.getTotalElements()).isEqualTo(1);
        assertThat(pageReturn.getContent().getFirst().name()).isEqualTo(partGetDto.name());
        assertThat(pageReturn.getContent().getFirst().partCode()).isEqualTo(partGetDto.partCode());

        verify(partRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable));
        verify(conversor).converterPart(any(Part.class));
    }

    @Test
    @DisplayName("Deve atualizar dados de uma peça.")
    void updatePart(){

        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);

        Part part = new Part(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "12345",
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                10,
                employee
        );

        ReflectionTestUtils.setField(part, "id", 1L);

        String oldName = part.getName();
        String oldMark = part.getMark();
        BigDecimal oldCostPrice = part.getCostPrice();
        BigDecimal oldSellingPrice = part.getSellingPrice();
        Integer oldMinimumStock = part.getMinimumStock();

        PartPutDTO partPutDTO = new PartPutDTO(
                1L,
                "Pastilha De Freio",
                null,
                "Diafrag",
                null,
                BigDecimal.valueOf(20.2),
                BigDecimal.valueOf(42.5),
                5
        );


        PartGetDTO partGetDto = new PartGetDTO(
                1L,
                "12345",
                "Pastilha De Freio",
                null,
                "Diafrag",
                PartCategory.FREIO,
                BigDecimal.valueOf(20.2),
                BigDecimal.valueOf(42.5),
                true,
                LocalDate.now(),
                1L,
                1
        );

        when(partRepository.findById(partPutDTO.id())).thenReturn(Optional.of(part));
        when(partRepository.existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), part.getId())).thenReturn(false);
        when(conversor.converterPart(any(Part.class))).thenReturn(partGetDto);

        PartGetDTO partReturn = partService.updatePart(partPutDTO);

        assertThat(partReturn).isNotNull();

        assertThat(partReturn.name()).isNotEqualTo(oldName);
        assertThat(partReturn.mark()).isNotEqualTo(oldMark);
        assertThat(partReturn.costPrice()).isNotEqualTo(oldCostPrice);
        assertThat(partReturn.sellingPrice()).isNotEqualTo(oldSellingPrice);

        assertThat(part.getName()).isEqualTo(partPutDTO.name());
        assertThat(part.getMark()).isEqualTo(partPutDTO.mark());
        assertThat(part.getCostPrice()).isEqualByComparingTo(partPutDTO.costPrice());
        assertThat(part.getSellingPrice()).isEqualByComparingTo(partPutDTO.sellingPrice());

        verify(partRepository).findById(partPutDTO.id());
        verify(partRepository).existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), part.getId());
        verify(conversor).converterPart(any(Part.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se a peça não for encontrada")
    void throwsExceptionIfTheIdDoesNotExist(){
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);

        PartPutDTO partPutDTO = new PartPutDTO(
                1L,
                "Pastilha De Freio",
                null,
                "Diafrag",
                null,
                BigDecimal.valueOf(20.2),
                BigDecimal.valueOf(42.5),
                5
        );

        when(partRepository.findById(partPutDTO.id())).thenReturn(Optional.empty());

        assertThrows(PartNotFoundException.class, () -> partService.updatePart(partPutDTO));

        verify(partRepository).findById(partPutDTO.id());
        verify(partRepository, never()).existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), partPutDTO.id());
        verify(conversor, never()).converterPart(any(Part.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se preço de custo for alterado sem alterar preço de venda")
    void throwsExceptionIfTheSellingPriceIsNull(){
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);

        Part part = new Part(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "12345",
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                10,
                employee
        );

        PartPutDTO partPutDTO = new PartPutDTO(
                1L,
                "Pastilha De Freio",
                null,
                "Diafrag",
                null,
                BigDecimal.valueOf(20.2),
                null,
                5
        );

        when(partRepository.findById(partPutDTO.id())).thenReturn(Optional.of(part));

        assertThrows(IllegalArgumentException.class, () -> partService.updatePart(partPutDTO));

        verify(partRepository).findById(partPutDTO.id());
        verify(partRepository, never()).existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), partPutDTO.id());
        verify(conversor, never()).converterPart(any(Part.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se ja existir peça com mesmo nome e marca")
    void throwsExceptionIfTheNameAndBrandAlreadyExist(){
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);

        Part part = new Part(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "12345",
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                10,
                employee
        );

        PartPutDTO partPutDTO = new PartPutDTO(
                1L,
                "Pastilha De Freio",
                null,
                "Diafrag",
                null,
                BigDecimal.valueOf(20.2),
                BigDecimal.valueOf(44.9),
                5
        );

        when(partRepository.findById(partPutDTO.id())).thenReturn(Optional.of(part));
        when(partRepository.existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), partPutDTO.id())).thenReturn(true);

        assertThrows(PartAlreadyExistsException.class, () -> partService.updatePart(partPutDTO));

        verify(partRepository).findById(partPutDTO.id());
        verify(partRepository).existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), partPutDTO.id());
        verify(conversor, never()).converterPart(any(Part.class));
    }


    @Test
    @DisplayName("Deve lançar exceção se preço de custo for igual a zero.")
    void throwsExceptionIfTheCostPriceIsEqualToZero(){
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);

        Part part = new Part(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "12345",
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                10,
                employee
        );

        PartPutDTO partPutDTO = new PartPutDTO(
                1L,
                "Pastilha De Freio",
                null,
                "Diafrag",
                null,
                BigDecimal.ZERO,
                BigDecimal.valueOf(44.9),
                5
        );

        when(partRepository.findById(partPutDTO.id())).thenReturn(Optional.of(part));
        when(partRepository.existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), partPutDTO.id())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> partService.updatePart(partPutDTO));

        verify(partRepository).findById(partPutDTO.id());
        verify(partRepository).existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), partPutDTO.id());
        verify(conversor, never()).converterPart(any(Part.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se preço de venda for igual a zero.")
    void throwsExceptionIfTheSellingPriceIsEqualToZero(){
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);

        Part part = new Part(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "12345",
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                10,
                employee
        );

        PartPutDTO partPutDTO = new PartPutDTO(
                1L,
                "Pastilha De Freio",
                null,
                "Diafrag",
                null,
                BigDecimal.valueOf(25,4),
                BigDecimal.ZERO,
                5
        );

        when(partRepository.findById(partPutDTO.id())).thenReturn(Optional.of(part));
        when(partRepository.existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), partPutDTO.id())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> partService.updatePart(partPutDTO));

        verify(partRepository).findById(partPutDTO.id());
        verify(partRepository).existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), partPutDTO.id());
        verify(conversor, never()).converterPart(any(Part.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se preço de custo for maior que preço de venda")
    void throwsExceptionIfTheCostPriceIsGreaterThanSellingPrice(){
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);

        Part part = new Part(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "12345",
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                10,
                employee
        );

        PartPutDTO partPutDTO = new PartPutDTO(
                1L,
                "Pastilha De Freio",
                null,
                "Diafrag",
                null,
                BigDecimal.valueOf(50.0),
                BigDecimal.valueOf(44.9),
                5
        );

        when(partRepository.findById(partPutDTO.id())).thenReturn(Optional.of(part));
        when(partRepository.existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), partPutDTO.id())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> partService.updatePart(partPutDTO));

        verify(partRepository).findById(partPutDTO.id());
        verify(partRepository).existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), partPutDTO.id());
        verify(conversor, never()).converterPart(any(Part.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se valor mínimo de estoque não for maior que zero.")
    void throwsExceptionIfTheMinimumStockIsNotPositive(){
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);

        Part part = new Part(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "12345",
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                10,
                employee
        );

        PartPutDTO partPutDTO = new PartPutDTO(
                1L,
                "Pastilha De Freio",
                null,
                "Diafrag",
                null,
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(44.9),
                -5
        );

        when(partRepository.findById(partPutDTO.id())).thenReturn(Optional.of(part));
        when(partRepository.existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), partPutDTO.id())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> partService.updatePart(partPutDTO));

        verify(partRepository).findById(partPutDTO.id());
        verify(partRepository).existsByNameAndMarkAndIdNot(partPutDTO.name(), partPutDTO.mark(), partPutDTO.id());
        verify(conversor, never()).converterPart(any(Part.class));
    }

    @Test
    @DisplayName("Deve buscar peça com estoque baixo.")
    void searchForItemsWithLowStock(){
        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345", JobType.ADMIN);

        Part part = new Part(
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "12345",
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                10,
                employee
        );

        List<Part> parts = List.of(part);

        Pageable pageable = PageRequest.of(0, 1);
        Page<Part> pagePart = new PageImpl<>(parts);

        PartGetDTO partGetDto = new PartGetDTO(
                1L,
                "12345",
                "Patim De Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                BigDecimal.valueOf(17.9),
                BigDecimal.valueOf(33.9),
                true,
                LocalDate.now(),
                1L,
                1
        );

        when(partRepository.searchForItemsWithLowStock(pageable)).thenReturn(pagePart);
        when(conversor.converterPart(any(Part.class))).thenReturn(partGetDto);

        Page<PartGetDTO> pageReturn = partService.searchForItemsWithLowStock(pageable);

        assertThat(pageReturn).isNotNull();
        assertThat(pageReturn.getTotalElements()).isEqualTo(1);
        assertThat(pageReturn.getContent().getFirst().name()).isEqualTo(partGetDto.name());
        assertThat(pageReturn.getContent().getFirst().partCode()).isEqualTo(partGetDto.partCode());

        verify(partRepository).searchForItemsWithLowStock(pageable);
        verify(conversor).converterPart(any(Part.class));
    }
}