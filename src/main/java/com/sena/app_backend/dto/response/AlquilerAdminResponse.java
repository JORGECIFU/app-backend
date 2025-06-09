package com.sena.app_backend.dto.response;

import com.sena.app_backend.model.EstadoAlquiler;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO con información extendida que ve el administrador.
 * Se incluye el monto que la plataforma ganó (o retuvo) al cerrar o cancelar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlquilerAdminResponse {
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
  /** Ganancia neta que la plataforma obtuvo al cerrar este alquiler */
  private BigDecimal gananciaPlataforma;
}
