package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock.InventoryAdjustamentDataDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock.MovementAdjustmnetResponseDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock.MovementDataDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.InventoryMovement;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Stock;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.InputStatus;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.MovementType;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PartService partService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private InvetoryMovementService movementService;



    // CONSULTAR QUANTIDADE EM ESTOQUE
    public Integer checkQuantity(Long id){
        return stockRepository.findCurrentQuantityById(id);
    }

    // REGISTRAR ENTRADA DE PEÇAS (OBS: USE @AUTHENTICATIONPRINCIPAL PARA PEGAR O FUNCIONARIO QUE FAZ A OPERAÇÃO, ADD NO CONTROLLER)
    @Transactional
    public void registerEntry(MovementDataDTO inputData, Long idEmployee){
        validatePositiveQuantity(inputData.quantity());

        Part part  = partService.findById(inputData.idPart());

        Employee employee = employeeService.findById(idEmployee);

        Stock stock = part.getStock();
        Integer previousQuantity = stock.getCurrentQuantity();

        stock.addNewQuantityParts(inputData.quantity());
        Integer newQuantity = stock.getCurrentQuantity();

        InventoryMovement movement = new InventoryMovement(
                stock, MovementType.ENTRADA, previousQuantity, newQuantity, null, employee
        );

        movementService.saveMovement(movement);
    }

    // REGISTRAR SAÍDA DE PEÇAS (OBS: USE @AUTHENTICATIONPRINCIPAL PARA PEGAR O FUNCIONARIO QUE FAZ A OPERAÇÃO, ADD NO CONTROLLER)
    @Transactional
    public void recordDeparture(MovementDataDTO dto, Long idEmployee){
        validatePositiveQuantity(dto.quantity());

        Part part = partService.findById(dto.idPart());

        Employee employee = employeeService.findById(idEmployee);

        Stock stock = part.getStock();
        Integer previousQuantity = stock.getCurrentQuantity();

        stock.removeQuantityOfItem(dto.quantity());
        Integer newQuantity = stock.getCurrentQuantity();

        InventoryMovement movement = new InventoryMovement(
                stock, MovementType.SAIDA, previousQuantity, newQuantity, null, employee
        );

        movementService.saveMovement(movement);
    }


    // REALIZAR AJUSTE NO ESTOQUE (OBS: USE @AUTHENTICATIONPRINCIPAL PARA PEGAR O FUNCIONARIO QUE FAZ A OPERAÇÃO, ADD NO CONTROLLER)
    @Transactional
    public MovementAdjustmnetResponseDTO performAdjustament(InventoryAdjustamentDataDTO dto, Long idEmployee){
        Part part = partService.findById(dto.idParty());

        Employee employee = employeeService.findById(idEmployee);

        Stock stock = part.getStock();
        Integer previousQuantity = stock.getCurrentQuantity();

        stock.adjustQuantity(dto.currentQuantity());

        InventoryMovement movement = new InventoryMovement(
                stock, MovementType.AJUSTE, previousQuantity, dto.currentQuantity(), dto.reason(), employee
        );

        movementService.saveMovement(movement);

        return new MovementAdjustmnetResponseDTO(
                InputStatus.SUCCESS,
                MovementType.AJUSTE,
                movement.getQuantity(),
                dto.reason(),
                stock.getId(),
                part.getName(),
                stock.getCurrentQuantity(),
                previousQuantity,
                dto.currentQuantity(),
                movement.getId()
        );

    }

    // VALIDAR DISPONIBILIDADE
    public Boolean validateAvailability(Long idPart){
        Part part = partService.findById(idPart);

        if (part.getStock().getCurrentQuantity() > 0){
            return true;
        }else {
            return false;
        }
    }

    // SALVAR STOCK NO BANCO
    public void saveStock(Stock stock){
        stockRepository.save(stock);
    }


    // VALIDAR QUANTIDADE POSITIVA
    private void validatePositiveQuantity(Integer quantity){
        if (quantity == null || quantity <= 0){
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }
}
