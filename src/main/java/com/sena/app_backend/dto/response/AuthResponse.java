package com.sena.app_backend.dto.response;

import lombok.*;

/**
 * Clase que representa la respuesta de autenticación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
  private String token;
}
