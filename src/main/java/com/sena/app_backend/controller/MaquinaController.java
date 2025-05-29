package com.sena.app_backend.controller;

import com.sena.app_backend.dto.request.MaquinaRequest;
import com.sena.app_backend.dto.response.MaquinaResponse;
import com.sena.app_backend.service.MaquinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar máquinas.
 * Permite crear, listar, obtener, actualizar y eliminar máquinas.
 */
@RestController
@RequestMapping("/api/maquinas")
@RequiredArgsConstructor
public class MaquinaController {

  private final MaquinaService service;

  /** Sólo ADMIN puede crear máquinas nuevas */
  @PostMapping
  @PreAuthorize("hasAuthority('ADMINISTRADOR')")
  public ResponseEntity<MaquinaResponse> crear(@RequestBody MaquinaRequest req) {
    return ResponseEntity.ok(service.crearMaquina(req));
  }

  /** Cualquiera autenticado puede listar todas las máquinas */
  @GetMapping
  public ResponseEntity<List<MaquinaResponse>> listar() {
    return ResponseEntity.ok(service.listarMaquinas());
  }

  /** Sólo ADMIN puede ver máquina por ID */
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMINISTRADOR')")
  public ResponseEntity<MaquinaResponse> obtener(@PathVariable Long id) {
    return ResponseEntity.ok(service.obtenerMaquina(id));
  }

  /** Sólo ADMIN puede actualizar máquina */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMINISTRADOR')")
  public ResponseEntity<MaquinaResponse> actualizar(
      @PathVariable Long id,
      @RequestBody MaquinaRequest req
  ) {
    return ResponseEntity.ok(service.actualizarMaquina(id, req));
  }

  /** Sólo ADMIN puede eliminar máquina */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMINISTRADOR')")
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    service.eliminarMaquina(id);
    return ResponseEntity.noContent().build();
  }
}
