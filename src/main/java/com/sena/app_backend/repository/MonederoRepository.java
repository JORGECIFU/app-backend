package com.sena.app_backend.repository;

import com.sena.app_backend.model.Monedero;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para manejar las operaciones de la entidad Monedero.
 * Permite realizar consultas y operaciones CRUD sobre los monederos de los usuarios.
 */
public interface MonederoRepository extends JpaRepository<Monedero, Long> {
  List<Monedero> findByUsuarioId(Long usuarioId);
  Optional<Monedero> findByIdAndUsuarioId(Long id, Long usuarioId);
}