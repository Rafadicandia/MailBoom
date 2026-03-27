package com.mailboom.api.infrastructure.whatsapp.controller;

import com.mailboom.api.application.whatsapp.port.in.DeleteClientConfigUseCase;
import com.mailboom.api.application.whatsapp.port.in.GetClientConfigUseCase;
import com.mailboom.api.application.whatsapp.port.in.SaveClientConfigUseCase;
import com.mailboom.api.application.whatsapp.usecase.command.SaveClientConfigCommand;
import com.mailboom.api.domain.model.whatsapp.ClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/whatsapp")
@RequiredArgsConstructor
public class ClientConfigController {

    private final SaveClientConfigUseCase saveUseCase;
    private final GetClientConfigUseCase getUseCase;
    private final DeleteClientConfigUseCase deleteUseCase;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientConfig> saveConfig(@RequestBody SaveClientConfigCommand command) {
        return ResponseEntity.ok(saveUseCase.save(command));
    }

    @GetMapping("/{clientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientConfig> getConfig(@PathVariable UUID clientId) {
        return ResponseEntity.ok(getUseCase.get(clientId));
    }

    @DeleteMapping("/{clientId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteConfig(@PathVariable UUID clientId) {
        deleteUseCase.delete(clientId);
        return ResponseEntity.noContent().build();
    }
}
