package com.sena.app_backend.service.impl;

import com.sena.app_backend.dto.request.PlanRequest;
import com.sena.app_backend.dto.response.PlanResponse;
import com.sena.app_backend.model.Plan;
import com.sena.app_backend.repository.PlanRepository;
import com.sena.app_backend.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Esta clase implementa la interfaz PlanService y proporciona la lógica de negocio para la gestión de planes.
 * Utiliza un repositorio para interactuar con la base de datos y realizar operaciones CRUD.
 *
 * @author Sena
 */
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

  private final PlanRepository repo;

  /**
   * Convierte una entidad Plan a un DTO de respuesta PlanResponse.
   *
   * @param p la entidad Plan
   * @return el DTO de respuesta PlanResponse
   */
  private PlanResponse toDto(Plan p) {
    return PlanResponse.builder()
        .id(p.getId())
        .nombre(p.getNombre())
        .gananciaMin(p.getGananciaMin())
        .gananciaMax(p.getGananciaMax())
        .duracionDias(p.getDuracionDias())
        .build();
  }

  /**
   * Convierte un DTO de solicitud PlanRequest a una entidad Plan.
   *
   * @param r el DTO de solicitud PlanRequest
   * @return la entidad Plan
   */
  private Plan toEntity(PlanRequest r) {
    return Plan.builder()
        .nombre(r.getNombre())
        .gananciaMin(r.getGananciaMin())
        .gananciaMax(r.getGananciaMax())
        .duracionDias(r.getDuracionDias())
        .build();
  }

  /**
   * Crea un nuevo plan.
   * <p>
   * Valida que no exista otro plan con el mismo nombre antes de crear uno nuevo.
   *
   * @param req la solicitud de creación de plan
   * @return la respuesta del plan creado
   */
  @Override
  public PlanResponse crearPlan(PlanRequest req) {
    Plan saved = repo.save(toEntity(req));
    return toDto(saved);
  }

  /**
   * Lista todos los planes disponibles.
   *
   * @return una lista de respuestas de planes
   */
  @Override
  public List<PlanResponse> listarPlanes() {
    return repo.findAll().stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Obtiene un plan por su ID.
   *
   * @param id el ID del plan a obtener
   * @return la respuesta del plan
   */
  @Override
  public PlanResponse obtenerPlan(Long id) {
    Plan p = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Plan no encontrado: " + id));
    return toDto(p);
  }

  /**
   * Actualiza un plan existente.
   *
   * @param id  el ID del plan a actualizar
   * @param req la solicitud de actualización de plan
   * @return la respuesta del plan actualizado
   */
  @Override
  public PlanResponse actualizarPlan(Long id, PlanRequest req) {
    Plan p = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Plan no encontrado: " + id));
    p.setNombre(req.getNombre());
    p.setGananciaMin(req.getGananciaMin());
    p.setGananciaMax(req.getGananciaMax());
    p.setDuracionDias(req.getDuracionDias());
    Plan updated = repo.save(p);
    return toDto(updated);
  }

  /**
   * Elimina un plan por su ID.
   *
   * @param id el ID del plan a eliminar
   */
  @Override
  public void eliminarPlan(Long id) {
    if (!repo.existsById(id)) {
      throw new RuntimeException("Plan no encontrado: " + id);
    }
    repo.deleteById(id);
  }
}
