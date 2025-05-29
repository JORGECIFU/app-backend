package com.sena.app_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Esta clase representa la entidad Plan en la base de datos.
 * Contiene los atributos id, nombre, gananciaMin, gananciaMax y duracionDias.
 *
 * @author Sena
 */
@Entity
@Table(name = "plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String nombre;          // Básico, Gold, Premium

  @Column(name = "ganancia_min", precision = 19, scale = 4, nullable = false)
  private BigDecimal gananciaMin;

  @Column(name = "ganancia_max", precision = 19, scale = 4, nullable = false)
  private BigDecimal gananciaMax;

  @Column(name = "duracion_dias", precision = 19, scale = 4, nullable = false)
  private BigDecimal duracionDias;
}

