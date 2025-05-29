package com.sena.app_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Esta clase representa la entidad Monedero en la base de datos.
 * Contiene los atributos id, usuario, saldoActual y transacciones.
 *
 * @author Sena
 */
@Entity
@Table(name = "monedero")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Monedero {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "alias", nullable = false, length = 50)
  private String alias;

  @ManyToOne
  @JoinColumn(name = "usuario_id", nullable = false)
  private Usuario usuario;

  @Enumerated(EnumType.STRING)
  @Column(name = "moneda", length = 20, nullable = false)
  private CriptoMoneda moneda;

  @Column(name = "saldo_actual", precision = 19, scale = 4, nullable = false)
  private BigDecimal saldoActual;

  @OneToMany(mappedBy = "monedero", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Transaccion> transacciones;
}
