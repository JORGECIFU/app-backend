package com.sena.app_backend.util;

import com.sena.app_backend.dto.response.PreciosPlan;
import com.sena.app_backend.model.Plan;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculadorPreciosPlan {
  /** % de comisión de plataforma (10%). */
  private static final BigDecimal COMISION = new BigDecimal("0.10");


  /**
   * Calcula los precios y ganancias asociados a un plan.
   *
   * @param plan el plan del cual se calcularán los precios
   * @return un objeto PreciosPlan con los cálculos realizados
   */
  public static PreciosPlan calculate(Plan plan) {
    BigDecimal min = plan.getGananciaMin();
    BigDecimal max = plan.getGananciaMax();
    BigDecimal dias = plan.getDuracionDias();

    // 1) Ganancia promedio diaria = (min + max) / 2
    BigDecimal gananciaProm = min.add(max)
        .divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);

    // 2) Precio bruto = gananciaProm × días
    BigDecimal precioBruto = gananciaProm.multiply(dias);

    // 3) Precio final al usuario = precioBruto × (1 – COMISION)
    BigDecimal precioAlquiler = precioBruto
        .multiply(BigDecimal.ONE.subtract(COMISION))
        .setScale(4, RoundingMode.HALF_UP);

    // 4) Ingreso bruto usuario si alcanza max cada día
    BigDecimal ingresoBrutoUsuario = max.multiply(dias);

    // 5) Ganancia máxima usuario = ingresoBrutoUsuario – precioFinal
    BigDecimal gananciaMaxUsuario = ingresoBrutoUsuario
        .subtract(precioAlquiler)
        .setScale(4, RoundingMode.HALF_UP);

    // 6) Ingreso plataforma = precioFinal
    BigDecimal ingresoPlataforma = precioBruto;

    return PreciosPlan.builder()
        .gananciaPromedioDiaria(gananciaProm)
        .precioBruto(precioBruto)
        .precioAlquiler(precioAlquiler)
        .gananciaMaxUsuario(gananciaMaxUsuario)
        .ingresoPlataforma(ingresoPlataforma)
        .build();
  }
}
