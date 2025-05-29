package com.sena.app_backend.dto.request;

import lombok.*;
import java.math.BigDecimal;

/**
 * Esta clase representa una solicitud para crear o actualizar un plan.
 * Contiene los atributos nombre, gananciaMin, gananciaMax y duracionDias.
 *
 * @author Sena
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanRequest {
  private String nombre;
  private BigDecimal gananciaMin;
  private BigDecimal gananciaMax;
  private BigDecimal duracionDias;
}
