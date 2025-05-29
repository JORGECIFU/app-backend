package com.sena.app_backend.repository;

import com.sena.app_backend.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Esta interfaz es un repositorio para la entidad Plan.
 * Extiende JpaRepository para proporcionar métodos CRUD y consultas personalizadas.
 *
 * @author Sena
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
  // Optional<Plan> findByNombre(String nombre);
}
