package com.sena.app_backend.dto.response;

import java.math.BigDecimal;
import lombok.*;

/*
 * Clase que representa la vista previa de un alquiler para el administrador.
 * Contiene información sobre el plan de alquiler, duración, ganancias y precios.
 */
@Getter @AllArgsConstructor @NoArgsConstructor
public class AlquilerPreviewAdminDto {
  private Long planId;
  private String planNombre;
  private BigDecimal duracionDias;
  private BigDecimal gananciaPromedioDiaria;
  private BigDecimal precioBruto;
  private BigDecimal precioAlquiler;
  private BigDecimal gananciaMaxUsuario;
  private BigDecimal ingresoPlataforma;
}