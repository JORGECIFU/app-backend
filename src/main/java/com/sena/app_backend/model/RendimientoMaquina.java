package com.sena.app_backend.model;

import com.sena.app_backend.converter.RendimientoListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rendimiento_maquinas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendimientoMaquina {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "maquina_id", nullable = false)
  private Maquina maquina;

  @Column(nullable = false)
  private LocalDateTime fecha;

  @Lob
  @Convert(converter = RendimientoListConverter.class)
  private List<RegistroRendimiento> rendimientos;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RegistroRendimiento {
    private LocalDateTime fechaHora;
    private Double valor;
  }
}
