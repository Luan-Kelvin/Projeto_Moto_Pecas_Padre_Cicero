package com.motopecas.padrecicero.motopecas.padrecicero.controller.sale;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.sale.ResponseCreateSaleDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.services.SaleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sale")
@SecurityRequirement(name = "bearer-key")
public class SaleControllerGet {

    @Autowired
    private SaleService saleService;

    // CONSULTAR VENDAS DA LOJA
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<ResponseCreateSaleDTO>> listSales(Pageable pageable){
        return ResponseEntity.ok().body(saleService.listSales(pageable));
    }

    // CONSULTAR VENDA POR ID
    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAIXA')")
    public ResponseEntity<ResponseCreateSaleDTO> findById(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(saleService.findById(id));
    }

    // CONSULTAR PRÓPRIAS VENDAS
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAIXA')")
    public ResponseEntity<Page<ResponseCreateSaleDTO>> listMySales(
            @AuthenticationPrincipal Employee employee,
            Pageable pageable
    ){
        return ResponseEntity.ok().body(saleService.listMySales(employee.getId(), pageable));
    }

}
