package com.sena.app_backend.repository;

import com.sena.app_backend.model.EstadoMaquina;
import com.sena.app_backend.model.Maquina;
import com.sena.app_backend.model.NivelRecursos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Maquina.
 * Proporciona métodos para realizar operaciones CRUD en la base de datos.
 */
@Repository
public interface MaquinaRepository extends JpaRepository<Maquina, Long> {
  Optional<Maquina> findBySerial(String serial);
  Optional<Maquina> findFirstByEstadoAndRecursos(
      EstadoMaquina estado,
      NivelRecursos recursos
  );
}
