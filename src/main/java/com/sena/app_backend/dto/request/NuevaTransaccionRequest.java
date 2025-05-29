package com.sena.app_backend.dto.request;

import com.sena.app_backend.model.TipoTransaccionPlataforma;
import lombok.*;
import java.math.BigDecimal;

/** Petición para crear una transacción (depósito o retiro) */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NuevaTransaccionRequest {
  private TipoTransaccionPlataforma tipo;
  private BigDecimal monto;
}
