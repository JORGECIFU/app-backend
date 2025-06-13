package com.sena.app_backend.repository;

import com.sena.app_backend.model.RefreshToken;
import com.sena.app_backend.model.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para manejar operaciones CRUD de RefreshToken.
 * Extiende JpaRepository para proporcionar métodos predefinidos.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  /**
   * Busca un RefreshToken por su token.
   *
   * @param token El token del RefreshToken a buscar.
   * @return Un Optional que contiene el RefreshToken si se encuentra, o vacío si no.
   */
  Optional<RefreshToken> findByToken(String token);

  /**
   * Elimina el RefreshToken asociado al usuario especificado.
   *
   * @param user El usuario cuyo RefreshToken se eliminará.
   * @return El número de registros eliminados.
   */
  @Modifying(clearAutomatically = true)
  @Transactional
  @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
  int deleteByUser(Usuario user);

  /**
   * Busca un RefreshToken por el ID del usuario.
   *
   * @param id El ID del usuario cuyo RefreshToken se busca.
   * @return Un Optional que contiene el RefreshToken si se encuentra, o vacío si no.
   */
  List<RefreshToken> findByUserId(Long id);
}