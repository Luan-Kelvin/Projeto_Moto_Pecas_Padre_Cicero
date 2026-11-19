package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.ItemForSalePostDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.ResponseCreateSaleDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.ResponseItemForSaleDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.SaleConclusionDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock.MovementDataDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.*;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.PaymentStatus;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.StatusSale;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.emplyeeExceptions.EmployeeInactiveException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.itemForSale.ItemForSaleNotFoundException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.partExceptions.InsufficientStockQuantityForSaleException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.salesException.SaleNotFoundException;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.salesException.SaleWithNoItemsAddedException;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.ItemForSaleRepository;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ItemForSaleRepository itemForSaleRepository;

    @Autowired
    private PartService partService;

    @Autowired
    private EmployeeService employeeService;


    @Autowired
    private StockService stockService;

    @Autowired
    private Conversor conversor;

    // CRIAR NOVA VENDA (OBS: USE @AUTHENTICATIONPRINCIPAL PARA PEGAR O FUNCIONARIO QUE FAZ A OPERAÇÃO, ADD NO CONTROLLER)
    @Transactional
    public ResponseCreateSaleDTO createSale(Long idEmployee){
        String saleCode = generateSaleCode();

        Employee employee = employeeService.findById(idEmployee);

        if (!Boolean.TRUE.equals(employee.getActive()) || !Boolean.TRUE.equals(employee.getVerified())){
            throw new EmployeeInactiveException("ERRO! Funcionário inativo ou não verificado.");
        }

        Sale sale = new Sale(saleCode, LocalDate.now(), employee);

        saleRepository.save(sale);

        return conversor.converterResponseCreateSale(sale);

    }

    // CRIAR ITEM DE VENDA QUE SERÁ ADICIONADO A VENDA QUE ESTÁ EM ABERTO.
    @Transactional
    public ResponseItemForSaleDTO addItemForSale(ItemForSalePostDTO dto){

        Part part = partService.findById(dto.id_part());

        if (!Boolean.TRUE.equals(part.getActive())) {
            throw new IllegalStateException(
                    "Não é possível vender uma peça desativada."
            );
        }

        Integer quanttyInStock = stockService.checkQuantity(part.getStock().getId());

        if (quanttyInStock < dto.quantity()){
            throw new InsufficientStockQuantityForSaleException("ERRO! Quantidade em estoque insuficiente para venda.");
        }

        Sale sale = saleRepository.findBySaleCode(dto.sale_code())
                .orElseThrow(
                        () -> new SaleNotFoundException(String.format("ERRO! Venda com código = %s, não foi encontrada ou não existe.", dto.sale_code())
                        ));

        if (!sale.getStatusSale().equals(StatusSale.ABERTA)){
            throw new IllegalStateException("ERRO! Não é possível adicionar itens a uma venda finalizada.");
        }

        ItemForSale item = new ItemForSale(dto.quantity(), part.getSellingPrice(), part, sale);
        itemForSaleRepository.save(item);

        sale.addItemSale(item);
        sale.recalculateTotal();

        reducePartInventory(part, dto.quantity(),sale.getEmployee().getId());

        return conversor.converterItemForSale(item);
    }

    // REMOVER ITEM DE VENDA, DELETAR DO BANCO
    @Transactional
    public String remeoveItemForSale(Long id_item){
        ItemForSale item = itemForSaleRepository.findById(id_item)
                .orElseThrow(() -> new ItemForSaleNotFoundException("ERRO! Item de venda não foi encontrado no banco."));

        Sale sale = saleRepository.findById(item.getSale().getId())
                .orElseThrow(() -> new SaleNotFoundException("ERRO! Venda não encontrda."));

        if (!sale.getStatusSale().equals(StatusSale.ABERTA)){
            throw new IllegalStateException("ERRO! Não é possível remover itens de uma venda finalizada.");
        }

        sale.removeItemForSale(item);
        sale.recalculateTotal();
        itemForSaleRepository.delete(item);

        increaseStockPart(item.getPart(), item.getQuantity(), sale.getEmployee().getId());

        return String.format("Item %s da venda %s, deletado com sucesso!", item.getPart().getName(), item.getSale().getSaleCode());
    }

    // FINALIZAR VENDA
    @Transactional
    public String completeSale(SaleConclusionDTO dto){
        Sale sale = saleRepository.findById(dto.id_sale())
                .orElseThrow(() -> new SaleNotFoundException(String.format("Venda com ID = %s, não foi encontrada.", dto.id_sale())));

        if (!sale.getStatusSale().equals(StatusSale.ABERTA)){
            throw new IllegalStateException("ERRO! Venda ja foi finalizada.");
        }

        if (sale.getItemForSales().isEmpty()){
            throw new SaleWithNoItemsAddedException("ERRO! A venda não tem nenhum item adicionado.");
        }

        sale.calculatePurchaseAmount();

        Payment payment = new Payment(sale.getTotalValue(), dto.paymentMethod(), dto.externalReference(), sale);

        sale.addPayment(payment);
        saleRepository.save(sale);

        return String.format(
                "Pagamento solicitado! Venda %s. Valor: R$ %.2f. Status: PENDENTE.",
                sale.getSaleCode(),
                sale.getTotalValue()
        );    }

    // CANCELAR VENDA
    @Transactional
    public String cancelSale(Long idSale){
        Sale sale = saleRepository.findById(idSale)
                .orElseThrow(() -> new SaleNotFoundException("ERRO! Venda não foi enontrada."));

        Payment payment = sale.getPaymentInfo();

        if (sale.getStatusSale().equals(StatusSale.CANCELADA)){
            throw new IllegalStateException("ERRO! Venda já está cancelada.");
        }

        for (ItemForSale i : sale.getItemForSales()){
            increaseStockPart(i.getPart(), i.getQuantity(), sale.getEmployee().getId());
        }

        if (payment != null && payment.getPaymentStatus() == PaymentStatus.APROVADO) {
            payment.refund();
        }

        sale.cancel();

        return String.format("Venda %s CANCELADA com sucesso! Valor total R$ %.2f", sale.getSaleCode(), sale.getTotalValue());
    }

    // LISTAR TODAS AS VENDAS DA LOJA
    public Page<ResponseCreateSaleDTO> listSales(Pageable pageable) {
        return saleRepository.findAll(pageable).map(conversor::converterResponseCreateSale);
    }

    // BUSCAR VENDA POR ID
    public ResponseCreateSaleDTO findById(Long id){
        return conversor.converterResponseCreateSale(saleRepository.findById(id).orElseThrow(
                () -> new SaleNotFoundException("Venda não encontrada."))
        );
    }

    // CONSULTAR VENDAS PRÓRPIAS
    public Page<ResponseCreateSaleDTO> listMySales(Long idEmployee, Pageable pageable) {
        return saleRepository.findByEmployeeId(idEmployee, pageable).map(conversor::converterResponseCreateSale);
    }

    @Transactional
    public String confirmPayment(Long idSale) {
        Sale sale = saleRepository.findById(idSale)
                .orElseThrow(() -> new SaleNotFoundException("Venda não encontrada."));

        if (sale.getStatusSale() != StatusSale.ABERTA) {
            throw new IllegalStateException("Essa venda não está aberta.");
        }

        Payment payment = sale.getPaymentInfo();

        if (payment == null) {
            throw new IllegalStateException("Venda não possui pagamento solicitado.");
        }

        payment.approve();

        sale.complete();

        return "Pagamento confirmado com sucesso!";
    }

    // GERAR CÓDIGO DE VENDA
    private String generateSaleCode() {
        saleRepository.incrementSaleCode();

        Long code = saleRepository.generateSaleCode();

        return code.toString();
    }

    // DIMINUIR ESTOQUE DA PEÇA QUE SERÁ VENDIDA
    private void reducePartInventory(Part part, Integer quantity, Long idEmployee){
        MovementDataDTO movementDataDTO = new MovementDataDTO(
                part.getId(),
                quantity
        );

        stockService.recordDeparture(movementDataDTO, idEmployee);
    }

    // DEVOLVER SALDO AO ESTOQUE DA  PEÇA
    private void increaseStockPart(Part part, Integer quantity, Long idEmployee){
        MovementDataDTO movementDataDTO = new MovementDataDTO(
                part.getId(),
                quantity
        );

        stockService.registerEntry(movementDataDTO, idEmployee);
    }



}
