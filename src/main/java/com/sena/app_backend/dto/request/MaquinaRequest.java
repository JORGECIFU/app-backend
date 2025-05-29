package com.sena.app_backend.dto.request;

import com.sena.app_backend.model.EstadoMaquina;
import lombok.*;

/**
 * Clase que representa la solicitud para crear o actualizar una máquina.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaquinaRequest {
  private String serial;
  private EstadoMaquina estado;
  private String especificaciones;
}

