package com.sena.app_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Esta clase representa la entidad Transaccion en la base de datos.
 * Contiene los atributos id, monedero, tipo, monto y fechaTransaccion.
 *
 * @author Sena
 */
@Entity
@Table(name = "transaccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaccion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "monedero_id", nullable = false)
  private Monedero monedero;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoTransaccionMonedero tipo;  // RECARGA, RETIRO

  @Column(precision = 19, scale = 4, nullable = false)
  private BigDecimal monto;

  @Column(name = "fecha_transaccion", nullable = false)
  private LocalDateTime fechaTransaccion;
}
