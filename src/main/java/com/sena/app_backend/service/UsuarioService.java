package com.sena.app_backend.service;

import com.sena.app_backend.dto.request.NuevoUsuarioRequest;
import com.sena.app_backend.dto.response.UsuarioResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Esta interfaz define los métodos para la gestión de usuarios en la aplicación.
 * Incluye operaciones para crear, obtener, actualizar y eliminar usuarios,
 * así como para promover usuarios a administradores y listar todos los usuarios.
 */
public interface UsuarioService {
  // Gestion Usuario
  /**
   * Crea un nuevo usuario.
   *
   * @param req DTO que contiene la información necesaria para crear el usuario.
   * @return DTO de respuesta con los detalles del usuario creado.
   */
  UsuarioResponse crearUsuario(NuevoUsuarioRequest req);
  /**
   * Crea un nuevo usuario administrador.
   *
   * @param req DTO que contiene la información necesaria para crear el administrador.
   * @param auth Información de autenticación del usuario que realiza la solicitud.
   * @return DTO de respuesta con los detalles del administrador creado.
   */
  UsuarioResponse crearAdministrador(NuevoUsuarioRequest req, Authentication auth);
  /**
   * Lista todos los usuarios registrados.
   *
   * @return lista de DTOs de respuesta con los detalles de cada usuario.
   */
  UsuarioResponse obtenerUsuario(Long id);
  /**
   * Actualiza un usuario existente.
   *
   * @param id ID del usuario a actualizar.
   * @param req DTO que contiene la información actualizada del usuario.
   * @return DTO de respuesta con los detalles del usuario actualizado.
   */
  UsuarioResponse actualizarUsuario(Long id, NuevoUsuarioRequest req);
  /**
   * Elimina un usuario por su ID.
   *
   * @param id ID del usuario a eliminar.
   */
  void eliminarUsuario(Long id);
  /**
   * Obtiene un usuario por su email.
   *
   * @param email el email del usuario a obtener
   * @return el DTO de respuesta UsuarioResponse
   */
  UsuarioResponse obtenerPorEmail(String email);
  // Gestion Administrador
  /**
   * Promueve un usuario a administrador.
   *
   * @param id ID del usuario a promover.
   * @return DTO de respuesta con los detalles del usuario promovido.
   */
  UsuarioResponse promoverAAdministrador(Long id);
  /**
   * Lista todos los usuarios registrados en la aplicación.
   *
   * @return lista de DTOs de respuesta con los detalles de cada usuario.
   */
  List<UsuarioResponse> listarUsuarios();
}