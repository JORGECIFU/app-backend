package com.sena.app_backend.dto.response;

import lombok.*;
import java.math.BigDecimal;

/**
 * Esta clase representa una respuesta para un plan.
 * Contiene los atributos id, nombre, gananciaMin, gananciaMax y duracionDias.
 *
 * @author Sena
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResponse {
  private Long id;
  private String nombre;
  private BigDecimal gananciaMin;
  private BigDecimal gananciaMax;
  private BigDecimal duracionDias;
}

