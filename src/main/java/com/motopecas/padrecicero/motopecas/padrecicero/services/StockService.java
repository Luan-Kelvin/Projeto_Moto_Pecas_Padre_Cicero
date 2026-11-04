package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock.InputData;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock.InputResponseDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.InventoryMovement;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Part;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Stock;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.InputStatus;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.enuns.MovementType;
import com.motopecas.padrecicero.motopecas.padrecicero.exception.stockExceptions.StockNotFoundException;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return stockRepository.findByCurrentQuantity(id);
    }

    // REGISTRAR ENTRADA DE PEÇAS (OBS: USE @AUTHENTICATIONPRINCIPAL PARA PEGAR O FUNCIONARIO QUE FAZ A OPERAÇÃO, ADD NO CONTROLLER)
    @Transactional
    public InputResponseDTO registerEntry(InputData inputData, Long idEmployee){
        Part part  = partService.findById(inputData.idPart());

        Employee employee = employeeService.findById(idEmployee);

        Stock stock = stockRepository.findById(part.getStock().getId())
                .orElseThrow(() ->  new StockNotFoundException(String.format("ERRO! Estoque de %s, não foi enontrado.", part.getName())));

        stock.addNewQuantityParts(inputData.quantity());

        InventoryMovement movement = new InventoryMovement(
                stock, MovementType.ENTRADA, inputData.quantity(), null, employee
        );

        movementService.saveMovement(movement);

        return new InputResponseDTO(
                InputStatus.SUCCESS,
                inputData.quantity(),
                stock.getId(),
                part.getName(),
                stock.getCurrentQuantity(),
                movement.getId()
        );
    }
}
