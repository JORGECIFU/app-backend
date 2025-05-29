package com.sena.app_backend.dto.request;

import com.sena.app_backend.model.TipoTransaccionMonedero;
import lombok.*;

import java.math.BigDecimal;

/**
 * Clase que representa la solicitud para realizar una nueva transacción
 * en el monedero, ya sea una recarga o un retiro.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NuevaTransaccionMonederoRequest {
  private TipoTransaccionMonedero tipo;
  private BigDecimal usdAmount;
  private BigDecimal cryptoAmount;
}
