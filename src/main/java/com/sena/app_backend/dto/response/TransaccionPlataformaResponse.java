package com.sena.app_backend.dto.response;

import com.sena.app_backend.model.TipoTransaccionPlataforma;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Detalle de una transacción en la cuenta de plataforma */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransaccionPlataformaResponse {
  private Long id;
  private TipoTransaccionPlataforma tipo;
  private BigDecimal monto;
  private LocalDateTime fechaTransaccion;
  private BigDecimal balancePosterior;
}

