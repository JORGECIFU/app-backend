package com.sena.app_backend.dto.request;

import lombok.*;

/**
 * Clase que representa la solicitud de autenticación.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
  private String username;
  private String password;
}
