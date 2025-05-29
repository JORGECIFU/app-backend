package com.sena.app_backend.dto.response;

import lombok.*;
import java.math.BigDecimal;

/** Vista simple del balance de plataforma para un usuario */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuentaPlataformaResponse {
  private Long cuentaId;
  private Long usuarioId;
  private BigDecimal balance;
}

