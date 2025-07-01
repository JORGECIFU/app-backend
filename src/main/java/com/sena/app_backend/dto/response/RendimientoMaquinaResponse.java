package com.sena.app_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendimientoMaquinaResponse {

  private Long id;
  private MaquinaDto maquina;
  private LocalDateTime fecha;
  private List<RegistroRendimientoDto> rendimientos;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class MaquinaDto {
    private Long id;
    private String serial;
    private String estado;
    private String recursos;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RegistroRendimientoDto {
    private LocalDateTime fechaHora;
    private Double valor;
  }
}
