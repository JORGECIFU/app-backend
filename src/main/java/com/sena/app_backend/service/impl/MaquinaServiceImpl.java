package com.sena.app_backend.service.impl;

import com.sena.app_backend.dto.request.MaquinaRequest;
import com.sena.app_backend.dto.response.MaquinaResponse;
import com.sena.app_backend.model.Maquina;
import com.sena.app_backend.repository.MaquinaRepository;
import com.sena.app_backend.service.MaquinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de máquinas.
 * Esta clase maneja la lógica de negocio relacionada con las máquinas.
 */
@Service
@RequiredArgsConstructor
public class MaquinaServiceImpl implements MaquinaService {

  private final MaquinaRepository repo;

  /**
   * Mapea la entidad Maquina a DTO de respuesta.
   *
   * @param m la entidad Maquina
   * @return el DTO de respuesta MaquinaResponse
   */
  private MaquinaResponse mapToDto(Maquina m) {
    return MaquinaResponse.builder()
        .id(m.getId())
        .serial(m.getSerial())
        .estado(m.getEstado())
        .especificaciones(m.getEspecificaciones())
        .build();
  }

  /**
   * Mapea el DTO de solicitud MaquinaRequest a la entidad Maquina.
   *
   * @param req el DTO de solicitud MaquinaRequest
   * @return la entidad Maquina
   */
  private Maquina mapToEntity(MaquinaRequest req) {
    return Maquina.builder()
        .serial(req.getSerial())
        .estado(req.getEstado())
        .especificaciones(req.getEspecificaciones())
        .build();
  }

  /**
   * Crea una nueva máquina.
   * <p>
   * Valida que no exista otra máquina con el mismo serial antes de crearla.
   *
   * @param req la solicitud de creación de máquina
   * @return la respuesta de la máquina creada
   */
  @Override
  public MaquinaResponse crearMaquina(MaquinaRequest req) {
    // Podrías validar que no exista ya otro serial
    if (repo.findBySerial(req.getSerial()).isPresent()) {
      throw new RuntimeException("Ya existe una máquina con serial: " + req.getSerial());
    }
    Maquina m = mapToEntity(req);
    Maquina saved = repo.save(m);
    return mapToDto(saved);
  }

  /**
   * Lista todas las máquinas.
   *
   * @return una lista de respuestas de máquinas
   */
  @Override
  public List<MaquinaResponse> listarMaquinas() {
    return repo.findAll()
        .stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  /**
   * Obtiene una máquina por su ID.
   *
   * @param id el ID de la máquina
   * @return la respuesta de la máquina
   */
  @Override
  public MaquinaResponse obtenerMaquina(Long id) {
    Maquina m = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Máquina no encontrada: " + id));
    return mapToDto(m);
  }

  /**
   * Actualiza una máquina existente.
   * <p>
   * Valida que la máquina exista antes de actualizarla.
   *
   * @param id el ID de la máquina a actualizar
   * @param req la solicitud de actualización de máquina
   * @return la respuesta de la máquina actualizada
   */
  @Override
  public MaquinaResponse actualizarMaquina(Long id, MaquinaRequest req) {
    Maquina m = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Máquina no encontrada: " + id));
    m.setSerial(req.getSerial());
    m.setEstado(req.getEstado());
    m.setEspecificaciones(req.getEspecificaciones());
    Maquina updated = repo.save(m);
    return mapToDto(updated);
  }

  /**
   * Elimina una máquina por su ID.
   * <p>
   * Valida que la máquina exista antes de eliminarla.
   *
   * @param id el ID de la máquina a eliminar
   */
  @Override
  public void eliminarMaquina(Long id) {
    if (!repo.existsById(id)) {
      throw new RuntimeException("Máquina no encontrada: " + id);
    }
    repo.deleteById(id);
  }
}
