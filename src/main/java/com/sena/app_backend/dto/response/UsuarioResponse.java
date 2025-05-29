package com.sena.app_backend.dto.response;

import com.sena.app_backend.model.Rol;
import lombok.*;

/**
 * Clase que representa la respuesta de un usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {
  private Long id;
  private String nombre;
  private String apellido;
  private String email;
  private Rol rol;
}
