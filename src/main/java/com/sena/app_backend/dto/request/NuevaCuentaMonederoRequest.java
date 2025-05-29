package com.sena.app_backend.dto.request;

import com.sena.app_backend.model.CriptoMoneda;
import lombok.*;

/**
 * Clase que representa la solicitud para crear un nuevo monedero.
 * Permite especificar un alias opcional para el monedero.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NuevaCuentaMonederoRequest {
  /** Alias opcional para el monedero */
  private String alias;
  private CriptoMoneda moneda;
}