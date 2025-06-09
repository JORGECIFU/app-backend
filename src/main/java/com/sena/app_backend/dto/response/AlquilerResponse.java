package com.sena.app_backend.dto.response;

import com.sena.app_backend.model.EstadoAlquiler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase que representa la respuesta de un alquiler.
 * Contiene información sobre el alquiler, incluyendo los identificadores del usuario, la máquina y el plan de alquiler,
 * así como las fechas de inicio y fin, el costo total y el estado del alquiler.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlquilerResponse {
  private Long id;
  private Long usuarioId;
  private Long maquinaId;
  private Long planId;
  private LocalDateTime fechaInicio;
  private LocalDateTime fechaFin;
  private BigDecimal precioAlquiler;
  private BigDecimal costoTotal;
  private EstadoAlquiler estado;
  /** Monto devuelto al usuario si hubo cancelación anticipada */
  private BigDecimal montoDevuelto;
  /** Ganancia de la plataforma por este alquiler */
  private BigDecimal gananciaPlataforma;
}