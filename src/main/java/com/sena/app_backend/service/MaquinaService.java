package com.sena.app_backend.service;

import com.sena.app_backend.dto.request.MaquinaRequest;
import com.sena.app_backend.dto.response.MaquinaResponse;

import java.util.List;

/**
 * Interfaz que define los métodos para la gestión de máquinas.
 */
public interface MaquinaService {
  /**
   * Crea una nueva máquina.
   *
   * @param req DTO que contiene la información necesaria para crear la máquina.
   * @return DTO de respuesta con los detalles de la máquina creada.
   */
  MaquinaResponse crearMaquina(MaquinaRequest req);
  /**
   * Lista todas las máquinas registradas.
   *
   * @return lista de DTOs de respuesta con los detalles de cada máquina.
   */
  List<MaquinaResponse> listarMaquinas();
  /**
   * Obtiene una máquina por su ID.
   *
   * @param id ID de la máquina a recuperar.
   * @return DTO de respuesta con los detalles de la máquina.
   */
  MaquinaResponse obtenerMaquina(Long id);
  /**
   * Actualiza una máquina existente.
   *
   * @param id ID de la máquina a actualizar.
   * @param req DTO que contiene la información actualizada de la máquina.
   * @return DTO de respuesta con los detalles de la máquina actualizada.
   */
  MaquinaResponse actualizarMaquina(Long id, MaquinaRequest req);
  /**
   * Elimina una máquina por su ID.
   *
   * @param id ID de la máquina a eliminar.
   */
  void eliminarMaquina(Long id);
}
