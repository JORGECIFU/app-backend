package com.sena.app_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa la solicitud para crear un nuevo alquiler.
 * Contiene los identificadores del usuario, la máquina y el plan de alquiler.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NuevoAlquilerRequest {
  private Long maquinaId;
  private Long planId;
}