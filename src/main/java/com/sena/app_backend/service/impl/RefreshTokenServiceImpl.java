package com.sena.app_backend.service.impl;

import com.sena.app_backend.exception.TokenRefreshException;
import com.sena.app_backend.model.RefreshToken;
import com.sena.app_backend.model.Usuario;
import com.sena.app_backend.repository.RefreshTokenRepository;
import com.sena.app_backend.repository.UsuarioRepository;
import com.sena.app_backend.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;  // Asegúrate de importar la de Spring
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
  @Value("${jwt.refreshExpirationMs}")
  private Long refreshTokenDurationMs;

  private final RefreshTokenRepository refreshTokenRepository;
  private final UsuarioRepository userRepository;

  /**
   * Crea (o reemplaza) el RefreshToken para un usuario:
   *  1. Elimina el token previo (si existía) en la misma transacción
   *  2. Inserta el nuevo token
   */
  @Override
  @Transactional
  public RefreshToken createRefreshToken(Long userId) {
    // 1) Obtener el usuario
    Usuario user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

    // 2) Eliminar el token previo (si existe) en la misma transacción
    refreshTokenRepository.deleteByUser(user);

    // 3) Crear y guardar el nuevo RefreshToken
    RefreshToken refreshToken = RefreshToken.builder()
        .user(user)
        .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
        .token(UUID.randomUUID().toString())
        .build();

    return refreshTokenRepository.save(refreshToken);
  }

  @Override
  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  @Override
  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().isBefore(Instant.now())) {
      // Si expiró, eliminarlo y lanzar excepción
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(token.getToken(), "Expired refresh token. Please login again");
    }
    return token;
  }

  @Override
  @Transactional
  public int deleteByUserId(Long userId) {
    Usuario user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
    return refreshTokenRepository.deleteByUser(user);
  }
}
