package com.motopecas.padrecicero.motopecas.padrecicero.controller.part;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartMovementDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.services.PartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/part")
@SecurityRequirement(name = "bearer-key")
public class PartControllerGet {

    @Autowired
    private PartService partService;

    // LISTAR TODAS AS PEÇAS CADASTRADAS
    @GetMapping("/list")
    public ResponseEntity<Page<PartGetDTO>> listParts(Pageable pageable){
        return ResponseEntity.ok().body(partService.listParts(pageable));
    }

    // BUSCAR PEÇA PELO ID
    @GetMapping("id/{id}")
    public ResponseEntity<PartGetDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(partService.findByIdController(id));
    }

    // BUSCAR PEÇAS COM ESTOQUE BAIXO
    @GetMapping("/stock/low")
    public ResponseEntity<Page<PartGetDTO>> itemsWithLowStock(Pageable pageable){
        return ResponseEntity.ok().body(partService.searchForItemsWithLowStock(pageable));
    }

    // CONSULTAR QUANTIDADE DISPONÍVEL EM ESTOQUE
    @GetMapping("/stock/quantity/{id}")
    public ResponseEntity<Integer> getAvailableQuantity(@PathVariable Long id){
        return ResponseEntity.ok().body(partService.checkQuantityPart(id));
    }

    // CONSULTR MOVIMENTAÇÕES DE ESTOQUE
    @GetMapping("/stock/movements/{id}")
    public ResponseEntity<Page<PartMovementDTO>> listPartMovements(
            @PathVariable Long id,
            Pageable pageable
    ){
        return ResponseEntity.ok().body(partService.listPartMovements(id, pageable));
    }

    // PESQUISAR PEÇA
    @GetMapping("/search")
    public ResponseEntity<Page<PartGetDTO>> searchPart(@RequestParam String search, Pageable pageable){
        return ResponseEntity.ok().body(partService.searchPartByName(search, pageable));
    }

}
