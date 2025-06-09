package com.sena.app_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Esta clase representa la entidad PlatformTransaction en la base de datos.
 * Contiene los atributos id, account, tipo, monto y fechaTransaccion.
 *
 * @author Sena
 */
@Entity
@Table(name = "platforma_transaccion_cuenta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlataformaTransaccionCuenta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "account_id", nullable = false)
  private PlataformaFondosCuenta account;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoTransaccionPlataforma tipo;

  @Column(name = "monto", precision = 19, scale = 4, nullable = false)
  private BigDecimal monto;

  @Column(name = "balance_posterior", precision = 19, scale = 4, nullable = false)
  private BigDecimal balancePosterior;

  @Column(name = "fecha_transaccion", nullable = false)
  private LocalDateTime fechaTransaccion;
}
