package com.sena.app_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Esta clase representa la entidad Alquiler en la base de datos.
 * Contiene los atributos id, usuario, maquina, plan, fechaInicio, fechaFin, costoTotal y estado.
 *
 * @author Sena
 */
@Entity
@Table(name = "alquiler")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alquiler {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @ManyToOne
  @JoinColumn(name = "maquina_id", nullable = false)
  private Maquina maquina;

  @ManyToOne
  @JoinColumn(name = "plan_id", nullable = false)
  private Plan plan;

  @Column(name = "fecha_inicio", nullable = false)
  private LocalDateTime fechaInicio;

  @Column(name = "fecha_fin", nullable = false)
  private LocalDateTime fechaFin;

  @Column(name = "precio_alquiler", precision = 19, scale = 4, nullable = false)
  private BigDecimal precioAlquiler;

  @Column(name = "costo_total", precision = 19, scale = 4, nullable = false)
  private BigDecimal costoTotal;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoAlquiler estado;  // ACTIVO, CERRADO

  @Column(precision=19, scale=4)
  private BigDecimal montoDevuelto;

  @Column(precision=19, scale=4)
  private BigDecimal gananciaPlataforma;
}
