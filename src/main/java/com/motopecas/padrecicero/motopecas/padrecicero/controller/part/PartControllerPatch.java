package com.motopecas.padrecicero.motopecas.padrecicero.controller.part;

import com.motopecas.padrecicero.motopecas.padrecicero.services.PartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/part")
@SecurityRequirement(name = "bearer-key")
public class PartControllerPatch {

    @Autowired
    private PartService partService;

    // DESATIVAR PEÇA
    @PatchMapping("/deactivate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deactivatePart(@PathVariable("id") Long id) {
        partService.deactivatePart(id);
        return ResponseEntity.ok("Peça desativada com sucesso.");
    }

    // ATIVAR PEÇA
    @PatchMapping("/activate/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<String> activatePart(@PathVariable("id") Long id) {
        partService.activatePart(id);
        return ResponseEntity.ok("Peça ativada com sucesso.");
    }
}
