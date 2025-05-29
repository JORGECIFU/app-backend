package com.sena.app_backend.dto.response;

import com.sena.app_backend.model.EstadoMaquina;
import lombok.*;

/**
 * Clase que representa la respuesta de una máquina.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaquinaResponse {
  private Long id;
  private String serial;
  private EstadoMaquina estado;
  private String especificaciones;
}
