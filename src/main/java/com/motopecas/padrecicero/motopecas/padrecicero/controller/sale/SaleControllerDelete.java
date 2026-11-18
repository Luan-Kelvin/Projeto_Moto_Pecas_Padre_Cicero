package com.motopecas.padrecicero.motopecas.padrecicero.controller.sale;

import com.motopecas.padrecicero.motopecas.padrecicero.services.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sale")
public class SaleControllerDelete {

    @Autowired
    private SaleService saleService;

    // REMOVER ITEM DE VENDA EM ABERTO
    @DeleteMapping("/remove-item/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAIXA')")
    public ResponseEntity<String> removeItem(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(saleService.remeoveItemForSale(id));
    }
}
