package com.sena.app_backend.controller;

import com.sena.app_backend.dto.response.RendimientoMaquinaResponse;
import com.sena.app_backend.model.RendimientoMaquina;
import com.sena.app_backend.service.RendimientoMaquinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rendimientos")
@RequiredArgsConstructor
public class RendimientoController {

    private final RendimientoMaquinaService rendimientoService;

    @GetMapping("/maquina/{maquinaId}")
    public ResponseEntity<List<RendimientoMaquinaResponse>> obtenerRendimientos(
            @PathVariable Long maquinaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<RendimientoMaquinaResponse> rendimientos = rendimientoService.obtenerHistorialMaquina(maquinaId, fechaInicio, fechaFin);
        return ResponseEntity.ok(rendimientos);
    }
}