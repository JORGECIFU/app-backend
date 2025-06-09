package com.sena.app_backend.controller;

import com.sena.app_backend.dto.request.NuevoAlquilerRequest;
import com.sena.app_backend.dto.response.AlquilerAdminResponse;
import com.sena.app_backend.dto.response.AlquilerResponse;
import com.sena.app_backend.service.AlquilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar los alquileres de máquinas.
 * Define los endpoints para crear, listar, obtener y cerrar alquileres.
 */
@RestController
@RequestMapping("/api/alquileres")
@RequiredArgsConstructor
public class AlquilerController {

  /**
   * Servicio que encapsula la lógica de negocio de Alquiler.
   */
  private final AlquilerService service;

  /**
   * Crea un nuevo alquiler.
   *
   * @param req DTO que contiene la información necesaria
   *            (usuario, máquina, plan, fechas).
   * @return respuesta HTTP 200 con el alquiler creado.
   * @throws RuntimeException si los datos de entrada no son válidos
   *                          o no se puede crear el alquiler.
   */
  @PostMapping
  @PreAuthorize("hasAuthority('USUARIO')")
  public ResponseEntity<AlquilerResponse> crear(
      @RequestBody NuevoAlquilerRequest req, Authentication auth) {
    AlquilerResponse alquilerCreado = service.crearAlquiler(req, auth.getName());
    return ResponseEntity.ok(alquilerCreado);
  }

  /**
   * Recupera la lista de todos los alquileres registrados.
   * Solo accesible por administradores.
   *
   * @return respuesta HTTP 200 con la lista de alquileres.
   */
  @GetMapping
  @PreAuthorize("hasAuthority('ADMINISTRADOR')")
  public ResponseEntity<List<AlquilerAdminResponse>> listarParaAdmin() {
    List<AlquilerAdminResponse> todos = service.listarAlquileresParaAdmin();
    return ResponseEntity.ok(todos);
  }

  /**
   * Obtiene un alquiler por su ID.
   * - Administradores pueden ver cualquier alquiler.
   * - Usuarios solo pueden ver su propio alquiler.
   *
   * @param id identificador del alquiler.
   * @return respuesta HTTP 200 con el alquiler solicitado.
   * @throws RuntimeException si no existe el alquiler o no tiene permisos.
   */
  @GetMapping("/{id}")
  @PreAuthorize(
      "hasAuthority('ADMINISTRADOR') " +
          "or @securityService.isAlquilerOwner(#id, authentication)"
  )
  public ResponseEntity<AlquilerResponse> obtener(@PathVariable Long id) {
    AlquilerResponse encontrado = service.obtenerAlquiler(id);
    return ResponseEntity.ok(encontrado);
  }

  /**
   * Marca un alquiler como CERRADO de forma manual.
   * Solo accesible por administradores.
   *
   * @param id identificador del alquiler a cerrar.
   * @return respuesta HTTP 204 No Content al cerrarse correctamente.
   * @throws RuntimeException si no existe el alquiler.
   */
  @PutMapping("/cerrar/{id}")
  @PreAuthorize("hasAuthority('ADMINISTRADOR')")
  public ResponseEntity<Void> cerrar(@PathVariable Long id) {
    service.cerrarAlquiler(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Devuelve una vista previa del coste/ganancias de un plan dado.
   * Si quien consulta tiene autoridad ADMINISTRADOR, incluye también ingresoPlataforma.
   * Si no, se devuelve sólo la info para el usuario.
   *
   * @param planId id del plan a previsualizar
   * @return DTO con campos según rol
   */
  @GetMapping("/preview")
  public ResponseEntity<?> preview(@RequestParam Long planId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin = auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ADMINISTRADOR"));

    if (isAdmin) {
      return ResponseEntity.ok(service.previewAdmin(planId));
    } else {
      return ResponseEntity.ok(service.previewUser(planId));
    }
  }

  /**
   * Lista todos los alquileres ACTIVOS de un usuario.
   * - ADMINISTRADOR puede consultar cualquiera.
   * - USUARIO sólo los suyos.
   */
  @GetMapping("/usuario/{usuarioId}/activas")
  @PreAuthorize(
      "hasAuthority('ADMINISTRADOR') " +
          "or @securityService.isCurrentUser(#usuarioId, authentication)"
  )
  public ResponseEntity<List<AlquilerResponse>> listarActivosUsuario(
      @PathVariable Long usuarioId
  ) {
    return ResponseEntity.ok(service.listarActivosPorUsuario(usuarioId));
  }


  /**
   * Lista todos los alquileres CERRADOS de un usuario.
   * - ADMINISTRADOR puede consultar cualquiera.
   * - USUARIO sólo los suyos.
   */
  @GetMapping("/usuario/{usuarioId}/cerradas")
  @PreAuthorize(
      "hasAuthority('ADMINISTRADOR') " +
          "or @securityService.isCurrentUser(#usuarioId, authentication)"
  )
  public ResponseEntity<List<AlquilerResponse>> listarCerradosUsuario(
      @PathVariable Long usuarioId
  ) {
    return ResponseEntity.ok(service.listarCerradosPorUsuario(usuarioId));
  }

  /**
   * Vista previa de todos los alquileres activos.
   * - ADMINISTRADOR ve todos los detalles.
   * - USUARIO no ve cuanto gana la plataforma.
   */

  @RequestMapping("/preview/all")
  public ResponseEntity<?> previewAll() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    boolean isAdmin = auth != null && auth.isAuthenticated()
        && auth.getAuthorities().stream()
           .anyMatch(a -> a.getAuthority().equals("ADMINISTRADOR"));

    if (isAdmin) {
      return ResponseEntity.ok(service.previewAllAdmin());
    } else {
      return ResponseEntity.ok(service.previewAllUser());
    }
  }
}
