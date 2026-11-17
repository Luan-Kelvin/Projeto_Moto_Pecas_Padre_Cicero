package com.motopecas.padrecicero.motopecas.padrecicero.controller.part;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartPostDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock.InventoryAdjustamentDataDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.stock.MovementAdjustmnetResponseDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.entitys.Employee;
import com.motopecas.padrecicero.motopecas.padrecicero.services.PartService;
import com.motopecas.padrecicero.motopecas.padrecicero.services.StockService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/part")
public class PartControllerPost {

    @Autowired
    private PartService partService;

    @Autowired
    private StockService stockService;


    // CADASTRAR NOVA PEÇA
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<PartGetDTO> registerPart(
            @RequestBody @Valid PartPostDTO postDTO,
            @AuthenticationPrincipal Employee employee){
        PartGetDTO partGetDTO = partService.registerPart(postDTO, employee.getId());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/part/id/{id}")
                .buildAndExpand(partGetDTO.id())
                .toUri();

        return ResponseEntity.created(location).body(partGetDTO);
    }

    // REALIZAR AJUSTE NO ESTOQUE DA PEÇA
    @PostMapping("/adjustament")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MovementAdjustmnetResponseDTO> adjustmnetStock(
            @RequestBody InventoryAdjustamentDataDTO adjustamentDataDTO,
            @AuthenticationPrincipal Employee employee
    ){
        return ResponseEntity.ok().body(stockService.performAdjustament(adjustamentDataDTO, employee.getId()));
    }
}
