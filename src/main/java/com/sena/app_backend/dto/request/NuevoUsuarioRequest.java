package com.sena.app_backend.dto.request;

import com.sena.app_backend.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa la solicitud para crear un nuevo usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NuevoUsuarioRequest {
  private String nombre;
  private String apellido;
  private String email;
  private String password;
  private Rol rol = Rol.USUARIO;
}
