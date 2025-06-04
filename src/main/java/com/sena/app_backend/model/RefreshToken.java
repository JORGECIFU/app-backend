package com.sena.app_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
/**
 * Esta clase representa la entidad RefreshToken en la base de datos.
 * Se utiliza para almacenar tokens de actualización asociados a un usuario.
 *
 * @author Sena
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String token;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private Usuario user;

  @Column(nullable = false)
  private Instant expiryDate;

}

