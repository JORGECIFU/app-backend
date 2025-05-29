package com.sena.app_backend.service;

import com.sena.app_backend.dto.request.PlanRequest;
import com.sena.app_backend.dto.response.PlanResponse;
import java.util.List;

/**
 * Esta interfaz define los métodos para la gestión de planes.
 * Incluye operaciones para crear, listar, obtener, actualizar y eliminar planes.
 *
 * @author Sena
 */
public interface PlanService {
  /**
   * Crea un nuevo plan.
   *
   * @param req DTO que contiene la información necesaria para crear el plan.
   * @return DTO de respuesta con los detalles del plan creado.
   */
  PlanResponse crearPlan(PlanRequest req);
  /**
   * Lista todos los planes registrados.
   *
   * @return lista de DTOs de respuesta con los detalles de cada plan.
   */
  List<PlanResponse> listarPlanes();
  /**
   * Obtiene un plan por su ID.
   *
   * @param id ID del plan a recuperar.
   * @return DTO de respuesta con los detalles del plan.
   */
  PlanResponse obtenerPlan(Long id);
  /**
   * Actualiza un plan existente.
   *
   * @param id ID del plan a actualizar.
   * @param req DTO que contiene la información actualizada del plan.
   * @return DTO de respuesta con los detalles del plan actualizado.
   */
  PlanResponse actualizarPlan(Long id, PlanRequest req);
  /**
   * Elimina un plan por su ID.
   *
   * @param id ID del plan a eliminar.
   */
  void eliminarPlan(Long id);
}
