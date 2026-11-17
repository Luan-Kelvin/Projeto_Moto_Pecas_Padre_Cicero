package com.motopecas.padrecicero.motopecas.padrecicero.controller.part;

import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartGetDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.domain.dto.part.PartPutDTO;
import com.motopecas.padrecicero.motopecas.padrecicero.services.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/part")
public class PartControllerPut {

    @Autowired
    private PartService partService;

    @PutMapping("/update")
    public ResponseEntity<PartGetDTO> updatePart(@RequestBody PartPutDTO putDTO){
        return ResponseEntity.ok().body(partService.updatePart(putDTO));
    }
}
