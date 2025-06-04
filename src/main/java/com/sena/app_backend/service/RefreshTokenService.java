package com.sena.app_backend.service;

import com.sena.app_backend.model.RefreshToken;

import java.util.Optional;

/**
 * Interfaz que define los métodos para manejar los Refresh Tokens.
 * Permite crear, buscar, verificar expiración y eliminar tokens asociados a un usuario.
 *
 * @author Sena
 */
public interface RefreshTokenService {
  /**
   * Crea un nuevo Refresh Token para un usuario específico.
   *
   * @param userId ID del usuario al que se le asigna el token
   * @return El Refresh Token creado
   */
  RefreshToken createRefreshToken(Long userId);

  /**
   * Busca un Refresh Token por su valor.
   *
   * @param token El valor del token a buscar
   * @return Un Optional que contiene el Refresh Token si se encuentra, o vacío si no
   */
  Optional<RefreshToken> findByToken(String token);

  /**
   * Verifica si un Refresh Token ha expirado.
   *
   * @param token El Refresh Token a verificar
   * @return El Refresh Token si no ha expirado, o lanza una excepción si ha expirado
   */
  RefreshToken verifyExpiration(RefreshToken token);

  /**
   * Elimina un Refresh Token asociado a un usuario específico.
   *
   * @param userId ID del usuario cuyo token se desea eliminar
   * @return El número de tokens eliminados (debería ser 1 si se elimina correctamente)
   */
  int deleteByUserId(Long userId);
}
