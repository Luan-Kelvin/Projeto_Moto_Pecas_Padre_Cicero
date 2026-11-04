package com.motopecas.padrecicero.motopecas.padrecicero.services;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.InventoryMovement;
import com.motopecas.padrecicero.motopecas.padrecicero.repository.InventoryMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvetoryMovementService {

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    // SALVAR MOVIMENTAÇÃO NO BANCO (USADO POR CLASSE)
    public void saveMovement(InventoryMovement movement){
        if (movement != null){
            inventoryMovementRepository.save(movement);
        }
    }
}
