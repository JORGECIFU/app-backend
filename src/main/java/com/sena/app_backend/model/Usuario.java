package com.sena.app_backend.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Esta clase representa la entidad Usuario en la base de datos.
 * Contiene los atributos id, nombre, email, password y rol.
 *
 * @author Sena
 */
@Entity
@Table(name = "usuario")
@Data                      // genera getters, setters, toString, equals y hashCode
@NoArgsConstructor         // genera constructor sin argumentos
@AllArgsConstructor        // genera constructor con todos los campos
@Builder                   // añade un builder para instancias más legibles
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private String apellido;
  @Column(unique = true, nullable = false)
  private String email;
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Rol rol = Rol.USUARIO;  // valor por defecto
}
