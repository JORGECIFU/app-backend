package com.sena.app_backend.controller;

import com.sena.app_backend.dto.request.AuthRequest;
import com.sena.app_backend.dto.response.AuthResponse;
import com.sena.app_backend.dto.request.RefreshTokenRequest;  // DTO para solicitar refresh
import com.sena.app_backend.dto.response.RefreshTokenResponse; // DTO para responder nuevo JWT
import com.sena.app_backend.exception.InvalidRefreshTokenException;
import com.sena.app_backend.model.RefreshToken;
import com.sena.app_backend.model.Usuario;
import com.sena.app_backend.repository.UsuarioRepository;
import com.sena.app_backend.security.JwtUtil;
import com.sena.app_backend.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtUtil jwtUtil;
  private final UsuarioRepository userRepo;
  private final RefreshTokenService refreshTokenService;

  /**
   * 1) Login: Autentica, genera JWT y Refresh Token, y los devuelve en la respuesta.
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
    // 1. Autenticar credenciales
    Authentication authentication = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
    );

    // 2. Cargar usuario desde la base de datos
    Usuario user = userRepo.findByEmail(req.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + req.getUsername()));

    // 3. Generar token JWT con username y rol
    String token = jwtUtil.generateToken(user.getEmail(), user.getRol().name());

    // 4. Crear o reemplazar el Refresh Token del usuario
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

    // 5. Devolver AuthResponse con JWT y Refresh Token
    AuthResponse resp = AuthResponse.builder()
        .token(token)
        .refreshToken(refreshToken.getToken())
        .build();

    return ResponseEntity.ok(resp);
  }

  /**
   * 2) Refresh-Token: dado un refresh token válido, devuelve un nuevo access token (JWT).
   */
  @PostMapping("/refresh")
  public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    // 1) Buscar el RefreshToken
    RefreshToken rt = refreshTokenService.findByToken(requestRefreshToken)
        .orElseThrow(() -> new InvalidRefreshTokenException("Refresh Token no válido"));

    // 2) Verificar expiración
    refreshTokenService.verifyExpiration(rt); // si expira, lanza TokenRefreshException

    // 3) Generar nuevo JWT
    Usuario user = rt.getUser();
    String token = jwtUtil.generateToken(user.getEmail(), user.getRol().name());

    // 4) Crear nuevo RefreshToken
    RefreshToken newRt = refreshTokenService.createRefreshToken(user.getId());

    // 5) Devolver respuesta
    return AuthResponse.builder()
        .token(token)
        .refreshToken(newRt.getToken())
        .build();
  }
}
