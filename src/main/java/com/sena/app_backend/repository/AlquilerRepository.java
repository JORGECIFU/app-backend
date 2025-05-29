package com.sena.app_backend.repository;

import com.sena.app_backend.model.Alquiler;
import com.sena.app_backend.model.EstadoAlquiler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz que define el repositorio para la entidad Alquiler.
 * Extiende JpaRepository para proporcionar operaciones CRUD y consultas personalizadas.
 */
@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {
  List<Alquiler> findByUsuarioId(Long usuarioId);
  List<Alquiler> findByEstadoAndFechaFinBefore(EstadoAlquiler estado, LocalDateTime fecha);
  List<Alquiler> findByUsuarioIdAndEstado(Long usuarioId, EstadoAlquiler estado);
}
