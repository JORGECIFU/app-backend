package com.sena.app_backend.controller;


import com.sena.app_backend.dto.response.UsuarioResponse;
import com.sena.app_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar usuarios administradores.
 * Permite promover a un usuario a administrador.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

  private final UsuarioService usuarioService;

  @PutMapping("/promover/{id}")
  public ResponseEntity<UsuarioResponse> promoverUsuario(@PathVariable Long id) {
    UsuarioResponse actualizado = usuarioService.promoverAAdministrador(id);
    return ResponseEntity.ok(actualizado);
  }
}
