package com.sena.app_backend.controller;

import com.sena.app_backend.dto.request.*;
import com.sena.app_backend.dto.response.*;
import com.sena.app_backend.service.MonederoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monedero")
@RequiredArgsConstructor
public class MonederoController {

  private final MonederoService service;

  /**
   * Crea un nuevo monedero para el usuario autenticado.
   *
   * @param req DTO que contiene los detalles del nuevo monedero
   * @param auth Información de autenticación del usuario
   * @return Respuesta HTTP con el monedero creado
   */
  @PostMapping
  @PreAuthorize("hasAuthority('USUARIO')")
  public ResponseEntity<MonederoResponse> crear(
      @RequestBody NuevaCuentaMonederoRequest req,
      Authentication auth
  ) {
    return ResponseEntity.ok(service.crearMonedero(auth, req));
  }

  /**
   * Lista todos los monederos del usuario autenticado.
   *
   * @param auth Información de autenticación del usuario
   * @return Respuesta HTTP con la lista de monederos
   */
  @GetMapping
  @PreAuthorize("hasAuthority('USUARIO')")
  public ResponseEntity<List<MonederoResponse>> listar(Authentication auth) {
    return ResponseEntity.ok(service.listarMonederos(auth));
  }

  /**
   * Mueve fondos entre monederos o a una cuenta externa.
   *
   * @param monederoId ID del monedero desde el cual se moverán los fondos
   * @param req DTO que contiene los detalles de la transacción
   * @param auth Información de autenticación del usuario
   * @return Respuesta HTTP con la transacción realizada
   */
  @PostMapping("/{monederoId}/transacciones")
  @PreAuthorize(
      "hasAuthority('USUARIO') and " +
          "@securityService.isMonederoOwner(#monederoId, authentication)"
  )
  public ResponseEntity<TransaccionMonederoResponse> mover(
      @PathVariable Long monederoId,
      @RequestBody NuevaTransaccionMonederoRequest req,
      Authentication auth
  ) {
    return ResponseEntity.ok(service.moverFondos(auth, monederoId, req));
  }

  /**
   * Obtiene el historial de transacciones de un monedero.
   * @param monederoId ID del monedero cuyo historial se desea consultar
   * @param auth Información de autenticación del usuario
   * @return Respuesta HTTP con la lista de transacciones del monedero
   */
  @GetMapping("/{monederoId}/transacciones")
  @PreAuthorize(
      "hasAuthority('USUARIO') and " +
          "@securityService.isMonederoOwner(#monederoId, authentication)"
  )
  public ResponseEntity<List<TransaccionMonederoResponse>> historial(
      @PathVariable Long monederoId,
      Authentication auth
  ) {
    return ResponseEntity.ok(service.historial(auth, monederoId));
  }
}
