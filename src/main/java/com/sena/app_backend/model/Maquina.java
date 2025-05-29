package com.sena.app_backend.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Esta clase representa una máquina que puede ser alquilada o utilizada
 * en la aplicación. Contiene información sobre su estado y especificaciones.
 *
 * @author Sena
 */
@Entity
@Table(name = "maquina")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Maquina {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String serial;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoMaquina estado;   // DISPONIBLE, RENTADA, MANTENIMIENTO

  @Column(columnDefinition = "TEXT")
  private String especificaciones;
}
