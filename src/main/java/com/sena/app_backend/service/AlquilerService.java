package com.sena.app_backend.service;

import com.sena.app_backend.dto.request.NuevoAlquilerRequest;
import com.sena.app_backend.dto.response.AlquilerAdminResponse;
import com.sena.app_backend.dto.response.AlquilerPreviewAdminDto;
import com.sena.app_backend.dto.response.AlquilerPreviewUserDto;
import com.sena.app_backend.dto.response.AlquilerResponse;
import com.sena.app_backend.model.Alquiler;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz que define los métodos para la gestión de alquileres.
 * Proporciona operaciones para crear, listar, obtener y cerrar alquileres.
 */
public interface AlquilerService {
  /**
   * Crea un nuevo alquiler.
   *
   * @param req DTO que contiene la información necesaria para crear el alquiler.
   * @return DTO de respuesta con los detalles del alquiler creado.
   */
  AlquilerResponse crearAlquiler(NuevoAlquilerRequest req, String userEmail);
  /**
   * Lista todos los alquileres registrados.
   *
   * @return lista de DTOs de respuesta con los detalles de cada alquiler.
   */
  List<AlquilerResponse> listarAlquileres();
  /**
   * Obtiene un alquiler por su ID.
   *
   * @param id ID del alquiler a recuperar.
   * @return DTO de respuesta con los detalles del alquiler.
   */
  AlquilerResponse obtenerAlquiler(Long id);
  /**
   * Cierra un alquiler, actualizando su estado a CERRADO.
   *
   * @param id ID del alquiler a cerrar.
   */
  void cerrarAlquiler(Long id);


  AlquilerPreviewAdminDto previewAdmin(Long planId);
  /**
   * Obtiene un DTO de vista previa del alquiler para el usuario.
   *
   * @param planId ID del plan asociado al alquiler.
   * @return DTO de vista previa del alquiler para el usuario.
   */
  AlquilerPreviewUserDto previewUser(Long planId);
  /**
   * Lista los alquileres activos de un usuario específico.
   *
   * @param usuarioId ID del usuario cuyos alquileres se desean listar.
   * @return lista de DTOs de respuesta con los detalles de los alquileres activos.
   */
  List<AlquilerResponse> listarActivosPorUsuario(Long usuarioId);
  /**
   * Lista los alquileres activos de un administrador específico.
   *
   * @param adminId ID del administrador cuyos alquileres se desean listar.
   * @return lista de DTOs de respuesta con los detalles de los alquileres activos.
   */
  List<AlquilerPreviewAdminDto> previewAllAdmin();
  /**
   * Lista los alquileres activos de un usuario específico.
   *
   * @return lista de DTOs de respuesta con los detalles de los alquileres activos.
   */
  List<AlquilerPreviewUserDto> previewAllUser();


  BigDecimal calcularMontoDevuelto(Alquiler a);

  /**
   * Retorna la fracción (entre 0 y 1) de días ya consumidos
   * en función de la fecha de inicio, fecha de cierre actual (si es anticipado)
   * y la longitud total en días del plan.
   */
  BigDecimal calcularFraccionUsada(Alquiler a);

  /** Nuevo: Obtener un alquiler DETALLADO para administrador */
  AlquilerAdminResponse obtenerAlquilerParaAdmin(Long id);

  /** Nuevo: Listar todos los alquileres en formato ADMIN (con gananciaPlataforma) */
  List<AlquilerAdminResponse> listarAlquileresParaAdmin();

  /**
   * Lista los alquileres cerrados de un usuario específico.
   *
   * @param usuarioId ID del usuario cuyos alquileres cerrados se desean listar.
   * @return lista de DTOs de respuesta con los detalles de los alquileres cerrados.
   */
  List<AlquilerResponse> listarCerradosPorUsuario(Long usuarioId);
}
