package com.sena.app_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Esta clase representa la entidad PlatformAccount en la base de datos.
 * Contiene los atributos id, usuario, balance y transactions.
 *
 * @author Sena
 */
@Entity
@Table(name = "platforma_fondos_cuenta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlataformaFondosCuenta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "usuario_id", nullable = false, unique = true)
  private Usuario usuario;

  @Column(name = "balance", precision = 19, scale = 4, nullable = false)
  private BigDecimal balance;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PlataformaTransaccionCuenta> transactions;
}
