package com.sena.app_backend.controller;

import com.sena.app_backend.dto.request.AuthRequest;
import com.sena.app_backend.dto.response.AuthResponse;
import com.sena.app_backend.model.Usuario;
import com.sena.app_backend.repository.UsuarioRepository;
import com.sena.app_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la autenticación de usuarios.
 * Permite iniciar sesión y obtener un token JWT.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtUtil jwtUtil;
  private final UsuarioRepository userRepo;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
    // 1. Autenticar credenciales
    Authentication authentication = authManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
    );
    // 2. Cargar usuario desde la base de datos
    Usuario user = (Usuario) userRepo.findByEmail(req.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + req.getUsername()));
    // 3. Generar token JWT con username y rol
    String token = jwtUtil.generateToken(user.getEmail(), user.getRol().name());
    // 4. Devolver AuthResponse
    AuthResponse resp = new AuthResponse(token);
    return ResponseEntity.ok(resp);
  }
}

