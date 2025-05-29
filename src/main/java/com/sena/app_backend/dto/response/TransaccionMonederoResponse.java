package com.sena.app_backend.dto.response;

import com.sena.app_backend.model.TipoTransaccionMonedero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase que representa la respuesta de una transacción en el monedero.
 * Contiene información sobre el ID de la transacción, el tipo de transacción,
 * el monto, la fecha de la transacción y el saldo posterior.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionMonederoResponse {
  private Long id;
  private TipoTransaccionMonedero tipo;
  private BigDecimal monto;
  private LocalDateTime fechaTransaccion;
  private BigDecimal saldoPosterior;
}
