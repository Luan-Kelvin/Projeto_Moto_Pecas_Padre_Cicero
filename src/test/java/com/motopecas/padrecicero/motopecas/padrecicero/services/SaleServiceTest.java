package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.ItemForSalePostDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.ResponseCreateSaleDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.ResponseItemForSaleDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.SaleConclusionDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.*;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.*;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions.EmployeeInactiveException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.itemForSale.ItemForSaleNotFoundException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.partExceptions.InsufficientStockQuantityForSaleException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.salesException.SaleNotFoundException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.salesException.SaleWithNoItemsAddedException;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.ItemForSaleRepository;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.SaleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private Conversor conversor;

    @Mock
    private ItemForSaleRepository itemForSaleRepository;

    @Mock
    private PartService partService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private StockService stockService;

    @InjectMocks
    private SaleService saleService;

    @Test
    @DisplayName("Criar nova venda")
    public void createNewSale() {
        Long idEmployee = 1L;
        Long salesCode = 1010L;

        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345678", JobType.VENDEDOR);
        ReflectionTestUtils.setField(employee, "id", 1L);
        ReflectionTestUtils.setField(employee, "verified", true);

        ResponseCreateSaleDTO responseDTO = new ResponseCreateSaleDTO(
                1L, String.valueOf(salesCode), LocalDate.now(), idEmployee, StatusSale.ABERTA, BigDecimal.ZERO,
                idEmployee, employee.getName()
        );

        when(saleRepository.generateSaleCode()).thenReturn(salesCode);
        when(employeeService.findById(idEmployee)).thenReturn(employee);
        when(conversor.converterResponseCreateSale(any(Sale.class))).thenReturn(responseDTO);

        ResponseCreateSaleDTO saleResponse = saleService.createSale(idEmployee);

        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);

        verify(saleRepository).save(saleCaptor.capture());

        Sale saleSaved = saleCaptor.getValue();

        assertThat(saleSaved).isNotNull();

        assertThat(saleSaved.getSaleCode()).isEqualTo(String.valueOf(salesCode));
        assertThat(saleSaved.getDateOfSale()).isEqualTo(LocalDate.now());
        assertThat(saleSaved.getEmployee().getId()).isEqualTo(idEmployee);

        assertThat(saleSaved.getSaleCode()).isEqualTo(saleResponse.saleCode());
        assertThat(saleSaved.getStatusSale()).isEqualTo(saleResponse.status());
        assertThat(saleSaved.getDateOfSale()).isEqualTo(saleResponse.dateOfSale());
        assertThat(saleSaved.getEmployee().getId()).isEqualTo(saleResponse.id_employee());
        assertThat(saleSaved.getTotalValue()).isEqualTo(saleResponse.totalValue());


        verify(saleRepository).generateSaleCode();
        verify(employeeService).findById(idEmployee);
        verify(conversor).converterResponseCreateSale(any(Sale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se funcionario não estiver ativo")
    void throwExceptionWhenEmployeeIsNotActive() {
        Long idEmployee = 1L;

        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345678", JobType.VENDEDOR);
        ReflectionTestUtils.setField(employee, "id", 1L);
        ReflectionTestUtils.setField(employee, "active", false);

        when(saleRepository.generateSaleCode()).thenReturn(1010L);
        when(employeeService.findById(idEmployee)).thenReturn(employee);

        assertThrows(EmployeeInactiveException.class, () -> saleService.createSale(idEmployee));

        verify(employeeService).findById(idEmployee);
        verify(saleRepository, never()).save(any(Sale.class));
        verify(saleRepository).generateSaleCode();
        verify(conversor, never()).converterResponseCreateSale(any(Sale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se funcionario não estiver verificado")
    void throwExceptionWhenEmployeeIsNotVerified() {
        Long idEmployee = 1L;

        Employee employee = new Employee("Joaquim", "jojo@gmail.com", "12345678", JobType.VENDEDOR);
        ReflectionTestUtils.setField(employee, "id", 1L);
        ReflectionTestUtils.setField(employee, "verified", false);

        when(saleRepository.generateSaleCode()).thenReturn(1010L);
        when(employeeService.findById(idEmployee)).thenReturn(employee);

        assertThrows(EmployeeInactiveException.class, () -> saleService.createSale(idEmployee));

        verify(employeeService).findById(idEmployee);
        verify(saleRepository, never()).save(any(Sale.class));
        verify(saleRepository).generateSaleCode();
        verify(conversor, never()).converterResponseCreateSale(any(Sale.class));
    }

    @Test
    @DisplayName("Criar item de venda")
    void createItemForSale() {
        ItemForSalePostDTO itemForSalePostDTO = new ItemForSalePostDTO(
                1L,
                10,
                "1010"
        );

        Employee employee = new Employee();
        ReflectionTestUtils.setField(employee, "id", 1L);

        Stock stock = new Stock();
        ReflectionTestUtils.setField(stock, "id", 1L);

        Part part = new Part(
                "Patim Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "10201",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(15),
                5,
                employee
        );
        ReflectionTestUtils.setField(part, "id", 1L);
        part.addStock(stock);

        Sale sale = new Sale();
        ReflectionTestUtils.setField(sale, "id", 1L);
        ReflectionTestUtils.setField(sale, "saleCode", "1010");
        ReflectionTestUtils.setField(sale, "statusSale", StatusSale.ABERTA);
        ReflectionTestUtils.setField(sale, "employee", employee);

        ResponseItemForSaleDTO responseItemForSaleDTO = new ResponseItemForSaleDTO(
               10,
                part.getSellingPrice(),
                1L,
                1L,
                BigDecimal.valueOf(150)
        );

        when(partService.findById(1L)).thenReturn(part);
        when(stockService.checkQuantity(part.getStock().getId())).thenReturn(16);
        when(saleRepository.findBySaleCode(itemForSalePostDTO.sale_code())).thenReturn(Optional.of(sale));
        when(conversor.converterItemForSale(any(ItemForSale.class))).thenReturn(responseItemForSaleDTO);

        ResponseItemForSaleDTO responseItemDTO = saleService.addItemForSale(itemForSalePostDTO);

        ArgumentCaptor<ItemForSale> itemCaptor =  ArgumentCaptor.forClass(ItemForSale.class);

        verify(itemForSaleRepository).save(itemCaptor.capture());

        ItemForSale itemSaved = itemCaptor.getValue();

        assertThat(itemSaved.getSale().getId()).isEqualTo(1L);
        assertThat(itemSaved.getPart().getId()).isEqualTo(1L);
        assertThat(itemSaved.getQuantity()).isEqualTo(10);
        assertThat(itemSaved.getUnitPrice()).isEqualTo(BigDecimal.valueOf(15));
        assertThat(itemSaved.getSubtotal()).isEqualTo(BigDecimal.valueOf(150));

        assertThat(responseItemDTO.id_sale()).isEqualTo(sale.getId());
        assertThat(responseItemDTO.id_part()).isEqualTo(part.getId());
        assertThat(responseItemDTO.quantity()).isEqualTo(10);
        assertThat(responseItemDTO.unitPrice()).isEqualTo(BigDecimal.valueOf(15));
        assertThat(responseItemDTO.subTotal()).isEqualTo(BigDecimal.valueOf(150));

        verify(partService).findById(1L);
        verify(stockService).checkQuantity(part.getStock().getId());
        verify(saleRepository).findBySaleCode("1010");
        verify(itemForSaleRepository).save(any(ItemForSale.class));
        verify(conversor).converterItemForSale(any(ItemForSale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se quantidade de estoque for insuficiente.")
    void shouldThrowExceptionIfStockQuantityIsInsufficient() {
        ItemForSalePostDTO itemForSalePostDTO = new ItemForSalePostDTO(
                1L,
                10,
                "1010"
        );

        Employee employee = new Employee();
        ReflectionTestUtils.setField(employee, "id", 1L);

        Stock stock = new Stock();
        ReflectionTestUtils.setField(stock, "id", 1L);

        Part part = new Part(
                "Patim Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "10201",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(15),
                5,
                employee
        );
        ReflectionTestUtils.setField(part, "id", 1L);
        part.addStock(stock);

        Sale sale = new Sale("1010", LocalDate.now(), employee);
        ReflectionTestUtils.setField(sale, "id", 1L);

        when(partService.findById(1L)).thenReturn(part);
        when(stockService.checkQuantity(part.getStock().getId())).thenReturn(5);

        assertThrows(InsufficientStockQuantityForSaleException.class,() -> saleService.addItemForSale(itemForSalePostDTO));

        verify(partService).findById(1L);
        verify(stockService).checkQuantity(part.getStock().getId());
        verify(saleRepository, never()).findBySaleCode("1010");
        verify(itemForSaleRepository, never()).save(any(ItemForSale.class));
        verify(conversor, never()).converterItemForSale(any(ItemForSale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se status da venda não for ABERTA")
    void shouldThrowExceptionIfStatusSaleNotOpen() {
        ItemForSalePostDTO itemForSalePostDTO = new ItemForSalePostDTO(
                1L,
                10,
                "1010"
        );

        Employee employee = new Employee();
        ReflectionTestUtils.setField(employee, "id", 1L);

        Stock stock = new Stock();
        ReflectionTestUtils.setField(stock, "id", 1L);

        Part part = new Part(
                "Patim Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "10201",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(15),
                5,
                employee
        );
        ReflectionTestUtils.setField(part, "id", 1L);
        part.addStock(stock);

        Sale sale = new Sale("1010", LocalDate.now(), employee);
        ReflectionTestUtils.setField(sale, "id", 1L);
        sale.setStatusSale(StatusSale.CANCELADA);

        when(partService.findById(1L)).thenReturn(part);
        when(stockService.checkQuantity(part.getStock().getId())).thenReturn(15);
        when(saleRepository.findBySaleCode("1010")).thenReturn(Optional.of(sale));

        assertThrows(IllegalStateException.class,() -> saleService.addItemForSale(itemForSalePostDTO));

        verify(partService).findById(1L);
        verify(stockService).checkQuantity(part.getStock().getId());
        verify(saleRepository).findBySaleCode("1010");
        verify(itemForSaleRepository, never()).save(any(ItemForSale.class));
        verify(conversor, never()).converterItemForSale(any(ItemForSale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se venda não for encontrada.")
    void shouldThrowExceptionIfSaleNotFound() {
        ItemForSalePostDTO itemForSalePostDTO = new ItemForSalePostDTO(
                1L,
                10,
                "1010"
        );

        Employee employee = new Employee();
        ReflectionTestUtils.setField(employee, "id", 1L);

        Stock stock = new Stock();
        ReflectionTestUtils.setField(stock, "id", 1L);

        Part part = new Part(
                "Patim Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "10201",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(15),
                5,
                employee
        );
        ReflectionTestUtils.setField(part, "id", 1L);
        part.addStock(stock);


        when(partService.findById(1L)).thenReturn(part);
        when(stockService.checkQuantity(part.getStock().getId())).thenReturn(16);
        when(saleRepository.findBySaleCode("1010")).thenReturn(Optional.empty());

        assertThrows(SaleNotFoundException.class,() -> saleService.addItemForSale(itemForSalePostDTO));

        verify(partService).findById(1L);
        verify(stockService).checkQuantity(part.getStock().getId());
        verify(saleRepository).findBySaleCode("1010");
        verify(itemForSaleRepository, never()).save(any(ItemForSale.class));
        verify(conversor, never()).converterItemForSale(any(ItemForSale.class));
    }

    @Test
    @DisplayName("Deve remover o item da venda e deletar no banco.")
    void mustRemoveitemFromTheSale(){
        Long idItem = 1L;

        Employee employee = new Employee();
        ReflectionTestUtils.setField(employee, "id", 1L);

        Part part = new Part(
                "Patim Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "10201",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(15),
                5,
                employee
        );

        ReflectionTestUtils.setField(part, "id", 1L);

        Sale sale = new Sale("1010", LocalDate.now(), employee);
        ReflectionTestUtils.setField(sale, "id", 1L);

        ItemForSale item = new ItemForSale(5, BigDecimal.valueOf(10), part, sale);
        ReflectionTestUtils.setField(item, "id", 1L);

        when(itemForSaleRepository.findById(idItem)).thenReturn(Optional.of(item));
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        saleService.remeoveItemForSale(idItem);

        boolean containing = sale.getItemForSales().contains(item);

        assertThat(containing).isEqualTo(false);
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getSale().getId()).isEqualTo(sale.getId());

        verify(itemForSaleRepository).findById(idItem);
        verify(saleRepository).findById(1L);
        verify(itemForSaleRepository).delete(any(ItemForSale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se o item não for encontrado")
    void mustThrowExceptionIfItemIsNotFound(){
        Long idItem = 1L;

        when(itemForSaleRepository.findById(idItem)).thenReturn(Optional.empty());

        assertThrows(ItemForSaleNotFoundException.class, () -> saleService.remeoveItemForSale(idItem));

        verify(itemForSaleRepository).findById(idItem);
        verify(saleRepository, never()).findById(anyLong());
        verify(itemForSaleRepository, never()).delete(any(ItemForSale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se o venda não for encontrado")
    void mustThrowExceptionIfSaleIsNotFound(){
        Long idItem = 1L;

        Employee employee = new Employee();
        ReflectionTestUtils.setField(employee, "id", 1L);

        Part part = new Part(
                "Patim Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "10201",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(15),
                5,
                employee
        );

        ReflectionTestUtils.setField(part, "id", 1L);

        Sale sale = new Sale("1010", LocalDate.now(), employee);
        ReflectionTestUtils.setField(sale, "id", 1L);

        ItemForSale item = new ItemForSale(5, BigDecimal.valueOf(10), part, sale);
        ReflectionTestUtils.setField(item, "id", 1L);

        when(itemForSaleRepository.findById(idItem)).thenReturn(Optional.of(item));
        when(saleRepository.findById(sale.getId())).thenReturn(Optional.empty());

        assertThrows(SaleNotFoundException.class, () -> saleService.remeoveItemForSale(idItem));

        verify(itemForSaleRepository).findById(idItem);
        verify(saleRepository).findById(anyLong());
        verify(itemForSaleRepository, never()).delete(any(ItemForSale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção se status da venda não estiver ABERTA na hora da remoção.")
    void mustThrowExceptionIfSaleStatusIsNotOPENAtRemovalTime(){
        Long idItem = 1L;

        Employee employee = new Employee();
        ReflectionTestUtils.setField(employee, "id", 1L);

        Part part = new Part(
                "Patim Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "10201",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(15),
                5,
                employee
        );

        ReflectionTestUtils.setField(part, "id", 1L);

        Sale sale = new Sale("1010", LocalDate.now(), employee);
        ReflectionTestUtils.setField(sale, "id", 1L);
        sale.setStatusSale(StatusSale.CANCELADA);

        ItemForSale item = new ItemForSale(5, BigDecimal.valueOf(10), part, sale);
        ReflectionTestUtils.setField(item, "id", 1L);

        when(itemForSaleRepository.findById(idItem)).thenReturn(Optional.of(item));
        when(saleRepository.findById(sale.getId())).thenReturn(Optional.of(sale));

        assertThrows(IllegalStateException.class, () -> saleService.remeoveItemForSale(idItem));

        verify(itemForSaleRepository).findById(idItem);
        verify(saleRepository).findById(anyLong());
        verify(itemForSaleRepository, never()).delete(any(ItemForSale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao finalizar venda cujo status não está ABERTA")
    void shouldThrowExceptionWhenCompletingSaleThatIsNotOpen() {
        SaleConclusionDTO conclusionDTO = new SaleConclusionDTO(1L, PaymentMethod.PIX, null);
        Sale sale = new Sale("12345", LocalDate.now(), new Employee());
        sale.setStatusSale(StatusSale.CANCELADA);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        assertThrows(IllegalStateException.class, () -> saleService.completeSale(conclusionDTO));

        verify(saleRepository).findById(1L);
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao finalizar venda sem itens")
    void shouldThrowExceptionWhenCompletingSaleWithoutItems() {
        SaleConclusionDTO conclusionDTO = new SaleConclusionDTO(1L, PaymentMethod.PIX, null);
        Sale sale = new Sale("12345", LocalDate.now(), new Employee());

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        assertThrows(SaleWithNoItemsAddedException.class, () -> saleService.completeSale(conclusionDTO));

        verify(saleRepository).findById(1L);
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar venda não encontrada")
    void shouldThrowExceptionWhenCancelingSaleThatDoesNotExist() {
        when(saleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SaleNotFoundException.class, () -> saleService.cancelSale(1L));

        verify(saleRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar uma venda já cancelada")
    void shouldThrowExceptionWhenCancelingAlreadyCanceledSale() {
        Employee employee = new Employee();
        Sale sale = new Sale("12345", LocalDate.now(), employee);
        sale.setStatusSale(StatusSale.CANCELADA);

        Part part = new Part(
                "Patim Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "10201",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(15),
                5,
                employee
        );
        Stock stock = new Stock();
        stock.setCurrentQuantity(10);
        part.addStock(stock);

        ItemForSale item = new ItemForSale(2, BigDecimal.valueOf(15), part, sale);
        sale.addItemSale(item);

        Payment payment = new Payment(BigDecimal.valueOf(30), PaymentMethod.PIX, null, sale);
        payment.setPaymentStatus(PaymentStatus.APROVADO);
        sale.addPayment(payment);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        assertThrows(IllegalStateException.class, () -> saleService.cancelSale(1L));

        assertThat(stock.getCurrentQuantity()).isEqualTo(10);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.APROVADO);
        assertThat(sale.getStatusSale()).isEqualTo(StatusSale.CANCELADA);
        verify(saleRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve estornar pagamento, cancelar venda e devolver itens ao estoque")
    void shouldCancelSaleRefundPaymentAndReturnItemsToStock() {
        Employee employee = new Employee();
        Sale sale = new Sale("12345", LocalDate.now(), employee);

        Part firstPart = new Part(
                "Patim Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "10201",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(15),
                5,
                employee
        );
        Stock firstStock = new Stock();
        firstStock.setCurrentQuantity(10);
        firstPart.addStock(firstStock);

        Part secondPart = new Part(
                "Pastilha Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "10202",
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(30),
                5,
                employee
        );
        Stock secondStock = new Stock();
        secondStock.setCurrentQuantity(3);
        secondPart.addStock(secondStock);

        sale.addItemSale(new ItemForSale(2, BigDecimal.valueOf(15), firstPart, sale));
        sale.addItemSale(new ItemForSale(4, BigDecimal.valueOf(30), secondPart, sale));

        Payment payment = new Payment(BigDecimal.valueOf(150), PaymentMethod.PIX, null, sale);
        payment.setPaymentStatus(PaymentStatus.APROVADO);
        sale.addPayment(payment);
        ReflectionTestUtils.setField(sale, "totalValue", BigDecimal.valueOf(150));

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        String response = saleService.cancelSale(1L);

        assertThat(response).isEqualTo(String.format(
                "Venda %s CANCELADA com sucesso! Valor total R$ %.2f",
                sale.getSaleCode(),
                sale.getTotalValue()
        ));
        assertThat(firstStock.getCurrentQuantity()).isEqualTo(12);
        assertThat(secondStock.getCurrentQuantity()).isEqualTo(7);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.ESTORNADO);
        assertThat(sale.getStatusSale()).isEqualTo(StatusSale.CANCELADA);
        verify(saleRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve finalizar a venda criando um novo Payment e mudando o status de venda para 'Finalizada'")
    void completeSale(){
        SaleConclusionDTO conclusionDTO = new SaleConclusionDTO(
                1L,
                PaymentMethod.PIX,
                null
        );

        Employee employee = new Employee();
        ReflectionTestUtils.setField(employee, "id", 1L);

        Sale sale = new Sale("12345", LocalDate.now(), employee);

        Part part = new Part(
                "Patim Freio",
                null,
                "Cobreq",
                PartCategory.FREIO,
                "10201",
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(15),
                5,
                employee
        );

        ItemForSale item = new ItemForSale(5, BigDecimal.valueOf(10), part, sale);
        ReflectionTestUtils.setField(item, "id", 1L);

        sale.addItemSale(item);

        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        String response = saleService.completeSale(conclusionDTO);

        assertThat(sale.getTotalValue()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(response).isEqualTo(String.format(
                "Venda %s finalizada com sucesso! Valor total R$ %.2f",
                sale.getSaleCode(),
                sale.getTotalValue()
        ));

        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);

        verify(saleRepository).save(saleCaptor.capture());

        Sale saleSaved = saleCaptor.getValue();

        Payment payment = saleSaved.getPaymentInfo();

        assertThat(payment).isNotNull();
        assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.PIX);
        assertThat(payment.getValuePayment()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.APROVADO);

        assertThat(saleSaved.getStatusSale()).isEqualTo(StatusSale.CONCLUIDA);

        verify(saleRepository).findById(1L);
    }

}
