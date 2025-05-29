package com.sena.app_backend.repository;

import com.sena.app_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Esta interfaz extiende JpaRepository para proporcionar métodos CRUD
 * para la entidad Usuario.
 *
 * @author Sena
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByEmail(String email);
}
