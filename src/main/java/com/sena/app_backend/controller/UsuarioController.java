package com.sena.app_backend.controller;

import com.sena.app_backend.dto.request.NuevoUsuarioRequest;
import com.sena.app_backend.dto.response.UsuarioResponse;
import com.sena.app_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar usuarios.
 * Permite crear, listar, obtener, actualizar y eliminar usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

  private final UsuarioService service;

  /**
   * Crea un nuevo usuario.
   * Solo el administrador puede crear nuevos usuarios.
   *
   * @param req NuevoUsuarioRequest con la información del nuevo usuario
   * @return UsuarioResponse con la información del usuario creado
   */
  @PostMapping
  @PreAuthorize(
      "!#req.rol.equals(T(com.sena.app_backend.model.Rol).ADMINISTRADOR) "
          + "|| hasAuthority('ADMINISTRADOR')"
  )
  public ResponseEntity<UsuarioResponse> crear(
      @RequestBody NuevoUsuarioRequest req
  ) {
    UsuarioResponse creado = service.crearUsuario(req);
    return ResponseEntity.ok(creado);
  }

  /**
   * Crea un nuevo usuario administrador.
   * Solo el administrador puede crear nuevos administradores.
   *
   * @param req NuevoUsuarioRequest con la información del nuevo administrador
   * @param auth Información de autenticación del usuario que realiza la solicitud
   * @return UsuarioResponse con la información del administrador creado
   */
  @PostMapping("/admin")
  @PreAuthorize("hasAuthority('ADMINISTRADOR')")
  public ResponseEntity<UsuarioResponse> crearAdmin(
      @RequestBody NuevoUsuarioRequest req,
      Authentication auth
  ) {
    UsuarioResponse creado = service.crearAdministrador(req, auth);
    return ResponseEntity.ok(creado);
  }

  /**
   * Lista todos los usuarios.
   * Solo el administrador puede acceder a esta información.
   *
   * @return Lista de UsuarioResponse con la información de los usuarios
   */
  @GetMapping
  public ResponseEntity<List<UsuarioResponse>> listar() {
    return ResponseEntity.ok(service.listarUsuarios());
  }

  /**
   * Obtiene un usuario por su ID.
   * Solo el administrador o el propio usuario pueden acceder a esta información.
   *
   * @param id ID del usuario a obtener
   * @return UsuarioResponse con la información del usuario
   */
  @GetMapping("/{id}")
  @PreAuthorize(
      "hasAuthority('ADMINISTRADOR') " +
          "|| @securityService.isCurrentUser(#id, authentication)"
  )
  public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
    return ResponseEntity.ok(service.obtenerUsuario(id));
  }

  /**
   * Obtiene el usuario autenticado.
   * Solo el propio usuario puede acceder a esta información.
   *
   * @param auth Información de autenticación del usuario
   * @return UsuarioResponse con la información del usuario
   */
  @GetMapping("/me")
  public ResponseEntity<UsuarioResponse> getMe(Authentication auth) {
    String email = auth.getName();        // extrae el username del token
    UsuarioResponse me = service.obtenerPorEmail(email);
    return ResponseEntity.ok(me);
  }

  /**
   * Actualiza un usuario.
   * Solo el administrador o el propio usuario pueden actualizar su información.
   *
   * @param id ID del usuario a actualizar
   * @param req NuevoUsuarioRequest con la información actualizada
   * @return UsuarioResponse con la información del usuario actualizado
   */
  @PutMapping("/{id}")
  @PreAuthorize(
      "hasAuthority('ADMINISTRADOR') " +
          "|| @securityService.isCurrentUser(#id, authentication)"
  )
  public ResponseEntity<UsuarioResponse> actualizar(
      @PathVariable Long id,
      @RequestBody NuevoUsuarioRequest req
  ) {
    return ResponseEntity.ok(service.actualizarUsuario(id, req));
  }

  /**
   * Elimina un usuario.
   * Solo el administrador o el propio usuario pueden eliminar su cuenta.
   *
   * @param id ID del usuario a eliminar
   * @return Respuesta vacía con código 204 No Content
   */
  @DeleteMapping("/{id}")
  @PreAuthorize(
      "hasAuthority('ADMINISTRADOR') " +
          "|| @securityService.isCurrentUser(#id, authentication)"
  )
  public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    service.eliminarUsuario(id);
    return ResponseEntity.noContent().build();
  }
}
