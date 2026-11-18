package com.motopecas.padrecicero.motopecas.padrecicero.controller.sale;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.ItemForSalePostDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.ResponseCreateSaleDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.ResponseItemForSaleDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.SaleConclusionDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.services.SaleService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sale")
public class SaleControllerPost {

    @Autowired
    private SaleService saleService;

    // CRIAR NOVA VENDA (DEIXAR EM ABERTO)
    @PostMapping("/new-sale")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAIXA')")
    public ResponseEntity<ResponseCreateSaleDTO> createNewSale(@AuthenticationPrincipal Employee employee){
        return ResponseEntity.ok().body(saleService.createSale(employee.getId()));
    }

    // ADICIONAR ITEMS EM UMA VENDA
    @PostMapping("/add-item")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAIXA')")
    public ResponseEntity<ResponseItemForSaleDTO> addItem(@RequestBody ItemForSalePostDTO item){
        return ResponseEntity.ok().body(saleService.addItemForSale(item));
    }

    // FINALIZAR VENDA
    @PostMapping("/complete-sale")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    public ResponseEntity<String> completeSale(@RequestBody @Valid SaleConclusionDTO dto){
        return ResponseEntity.ok().body(saleService.completeSale(dto));
    }

    // CANCELAR VENDA
    @PostMapping("/cancel-sale/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<String> cancelSale(@PathVariable Long id){
        return ResponseEntity.ok().body(saleService.cancelSale(id));
    }

    // CONFIRMAR PAGAMENTO
    @PostMapping("/{id}/confirm-payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAIXA')")
    public ResponseEntity<String> confirmPayment(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(saleService.confirmPayment(id));
    }

}
