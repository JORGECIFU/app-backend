package com.sena.app_backend.controller;

import com.sena.app_backend.dto.request.NuevaTransaccionRequest;
import com.sena.app_backend.dto.response.CuentaPlataformaResponse;
import com.sena.app_backend.dto.response.TransaccionPlataformaResponse;
import com.sena.app_backend.service.PlataformaCuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las cuentas de la plataforma.
 * Proporciona endpoints para obtener información de la cuenta,
 * historial de transacciones y crear nuevas transacciones.
 */
@RestController
@RequestMapping("/api/plataforma/cuenta")
@RequiredArgsConstructor
public class PlataformaCuentaController {

  private final PlataformaCuentaService service;

  /**
   * Obtiene la información de la cuenta de un usuario.
   * Solo accesible por administradores o el propietario de la cuenta.
   *
   * @param usuarioId ID del usuario cuya cuenta se desea obtener.
   * @return respuesta HTTP 200 con los detalles de la cuenta.
   */
  @GetMapping("/{usuarioId}")
  @PreAuthorize(
      "hasAuthority('ADMINISTRADOR') " +
          "|| @securityService.isCurrentUser(#usuarioId, authentication)"
  )
  public ResponseEntity<CuentaPlataformaResponse> obtenerCuenta(
      @PathVariable Long usuarioId) {
    return ResponseEntity.ok(service.obtenerCuenta(usuarioId));
  }

  /**
   * Obtiene el historial de transacciones de un usuario.
   * Solo accesible por administradores o el propietario de la cuenta.
   * @param usuarioId ID del usuario cuyo historial se desea obtener.
   * @return respuesta HTTP 200 con la lista de transacciones.
   */
  @GetMapping("/{usuarioId}/transacciones")
  @PreAuthorize(
      "hasAuthority('ADMINISTRADOR') " +
          "|| @securityService.isCurrentUser(#usuarioId, authentication)"
  )
  public ResponseEntity<List<TransaccionPlataformaResponse>> historial(
      @PathVariable Long usuarioId) {
    return ResponseEntity.ok(service.listarHistorial(usuarioId));
  }

  /**
   * Crea una nueva transacción en la cuenta de un usuario.
   * Solo accesible por administradores o el propietario de la cuenta.
   *
   * @param usuarioId ID del usuario cuya cuenta se desea modificar.
   * @param req DTO que contiene los detalles de la nueva transacción.
   * @return respuesta HTTP 200 con los detalles de la transacción creada.
   */
  @PostMapping("/{usuarioId}/transacciones")
  @PreAuthorize(
      "hasAuthority('ADMINISTRADOR') " +
          "|| @securityService.isCurrentUser(#usuarioId, authentication)"
  )
  public ResponseEntity<TransaccionPlataformaResponse> nuevaTransaccion(
      @PathVariable Long usuarioId,
      @RequestBody NuevaTransaccionRequest req) {
    return ResponseEntity.ok(service.crearTransaccion(usuarioId, req));
  }
}

