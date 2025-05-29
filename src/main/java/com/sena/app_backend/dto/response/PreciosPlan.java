package com.sena.app_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Clase que representa los precios de un plan de alquiler.
 * Contiene información sobre la ganancia promedio diaria, precios brutos y finales,
 * así como las ganancias del usuario y la plataforma.
 */
@Data
@Builder
public class PreciosPlan {
  /** Ganancia promedio diaria ( (min + max) / 2 ). */
  private BigDecimal gananciaPromedioDiaria;

  /** Precio bruto antes de comisión (gananciaProm × días). */
  private BigDecimal precioBruto;

  /** Precio final al usuario tras aplicar comisión. */
  private BigDecimal precioAlquiler;

  /** Ganancia máxima del usuario (ingresoBrutoUsuario − precioFinal). */
  private BigDecimal gananciaMaxUsuario;

  /** Ingreso que se queda la plataforma (igual a precioFinal). */
  private BigDecimal ingresoPlataforma;
}
