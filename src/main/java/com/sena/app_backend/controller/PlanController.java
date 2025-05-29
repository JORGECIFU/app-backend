package com.sena.app_backend.controller;

import com.sena.app_backend.dto.request.PlanRequest;
import com.sena.app_backend.dto.response.PlanResponse;
import com.sena.app_backend.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Esta clase es un controlador REST para gestionar los planes.
 * Proporciona endpoints para crear, listar, obtener, actualizar y eliminar planes.
 * Los métodos están protegidos por anotaciones de seguridad que restringen el acceso según el rol del usuario.
 *
 * @author Sena
 */
@RestController
@RequestMapping("/api/planes")
@RequiredArgsConstructor
public class PlanController {

  private final PlanService service;

  /** Cualquier usuario autenticado puede ver la lista */
  @GetMapping
  public ResponseEntity<List<PlanResponse>> listar() {
    return ResponseEntity.ok(service.listarPlanes());
  }

  /** Cualquier usuario autenticado puede ver un plan */
  @GetMapping("/{id}")
  public ResponseEntity<PlanResponse> obtener(@PathVariable Long id) {
    return ResponseEntity.ok(service.obtenerPlan(id));
  }

  /** Solo administradores pueden crear planes */
  @PostMapping
  @PreAuthorize("hasAuthority('ADMINISTRADOR')")
  public ResponseEntity<PlanResponse> crear(@RequestBody PlanRequest req) {
    return ResponseEntity.ok(service.crearPlan(req));
  }

  /** Solo administradores pueden actualizar planes */
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMINISTRADOR')")
  public ResponseEntity<PlanResponse> actualizar(
      @PathVariable Long id,
      @RequestBody PlanRequest req
  ) {
    return ResponseEntity.ok(service.actualizarPlan(id, req));
  }

  /** Solo administradores pueden eliminar planes */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ADMINISTRADOR')")
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    service.eliminarPlan(id);
    return ResponseEntity.noContent().build();
  }
}
