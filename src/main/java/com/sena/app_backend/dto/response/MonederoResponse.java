package com.sena.app_backend.dto.response;

import com.sena.app_backend.model.CriptoMoneda;
import lombok.*;

import java.math.BigDecimal;

/**
 * Clase que representa la respuesta de un monedero.
 * Contiene información sobre el ID del monedero, el ID del usuario,
 * un alias opcional y el saldo actual del monedero.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonederoResponse {
  private Long monederoId;
  private Long usuarioId;
  private String alias;
  private CriptoMoneda moneda;
  private BigDecimal saldoActual;
}