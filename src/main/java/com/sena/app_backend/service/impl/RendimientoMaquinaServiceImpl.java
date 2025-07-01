package com.sena.app_backend.service.impl;

import com.sena.app_backend.dto.response.RendimientoMaquinaResponse;
import com.sena.app_backend.model.EstadoMaquina;
import com.sena.app_backend.model.Maquina;
import com.sena.app_backend.model.NivelRecursos;
import com.sena.app_backend.model.RendimientoMaquina;
import com.sena.app_backend.repository.MaquinaRepository;
import com.sena.app_backend.repository.RendimientoMaquinaRepository;
import com.sena.app_backend.service.RendimientoMaquinaService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RendimientoMaquinaServiceImpl implements RendimientoMaquinaService {

  private final RendimientoMaquinaRepository rendimientoRepo;
  private final MaquinaRepository maquinaRepo;
  private final Random random = new Random();

  // Definir zona horaria de Colombia
  private static final ZoneId COLOMBIA_ZONE = ZoneId.of("America/Bogota");



  private RendimientoMaquinaResponse convertToDto(RendimientoMaquina rendimiento) {
    return RendimientoMaquinaResponse.builder()
        .id(rendimiento.getId())
        .maquina(RendimientoMaquinaResponse.MaquinaDto.builder()
            .id(rendimiento.getMaquina().getId())
            .serial(rendimiento.getMaquina().getSerial())
            .estado(rendimiento.getMaquina().getEstado().toString())
            .recursos(rendimiento.getMaquina().getRecursos().toString())
            .build())
        .fecha(rendimiento.getFecha())
        .rendimientos(rendimiento.getRendimientos().stream()
            .map(reg -> new RendimientoMaquinaResponse.RegistroRendimientoDto(
                reg.getFechaHora(),
                reg.getValor()))
            .toList())
        .build();
  }

  @Override
  @Scheduled(fixedRate = 120000) // Cada 2 minutos (120,000 ms)
  public void registrarRendimientos() {
    List<Maquina> maquinasActivas = maquinaRepo.findByEstadoIn(List.of(EstadoMaquina.RENTADA));

    // Usar hora de Colombia
    LocalDateTime ahora = ZonedDateTime.now(COLOMBIA_ZONE).toLocalDateTime();
    LocalDateTime fechaDelDia = ahora.toLocalDate().atStartOfDay();

    for (Maquina maquina : maquinasActivas) {
      Double valorRendimiento = generarRendimientoPorNivel(maquina.getRecursos());

      // Buscar si ya existe un registro para hoy
      Optional<RendimientoMaquina> rendimientoExistente =
          rendimientoRepo.findByMaquinaIdAndFecha(maquina.getId(), fechaDelDia);

      if (rendimientoExistente.isPresent()) {
        // Agregar al registro existente
        RendimientoMaquina rendimiento = rendimientoExistente.get();
        rendimiento.getRendimientos().add(
            new RendimientoMaquina.RegistroRendimiento(ahora, valorRendimiento)
        );
        rendimientoRepo.save(rendimiento);
      } else {
        // Crear nuevo registro
        List<RendimientoMaquina.RegistroRendimiento> registros = new ArrayList<>();
        registros.add(new RendimientoMaquina.RegistroRendimiento(ahora, valorRendimiento));

        RendimientoMaquina nuevoRendimiento = RendimientoMaquina.builder()
            .maquina(maquina)
            .fecha(fechaDelDia)
            .rendimientos(registros)
            .build();

        rendimientoRepo.save(nuevoRendimiento);
      }
    }
  }

  @Override
  public List<RendimientoMaquinaResponse> obtenerHistorialMaquina(Long maquinaId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
    List<RendimientoMaquina> rendimientos = rendimientoRepo.findByMaquinaIdAndFechaBetween(maquinaId, fechaInicio, fechaFin);

    return rendimientos.stream()
        .map(this::convertToDto)
        .toList();
  }

  @Override
  public RendimientoMaquinaResponse obtenerRendimientoDiario(Long maquinaId, LocalDateTime fecha) {
    LocalDateTime fechaDelDia = fecha.toLocalDate().atStartOfDay();
    return rendimientoRepo.findByMaquinaIdAndFecha(maquinaId, fechaDelDia)
        .map(this::convertToDto)
        .orElse(null);
  }

  @Override
  public Double generarRendimientoParaMaquina(Long maquinaId) {
    Optional<Maquina> maquina = maquinaRepo.findById(maquinaId);
    if (maquina.isPresent()) {
      return generarRendimientoPorNivel(maquina.get().getRecursos());
    }
    return 0.0;
  }

  private Double generarRendimientoPorNivel(NivelRecursos nivel) {
    // Generar rendimientos simulados basados en el nivel de recursos
    return switch (nivel) {
      case BAJOS -> 0.001 + (random.nextDouble() * 0.004); // 0.001 - 0.005
      case MEDIOS -> 0.005 + (random.nextDouble() * 0.010); // 0.005 - 0.015
      case ALTOS -> 0.015 + (random.nextDouble() * 0.020); // 0.015 - 0.035
      case SUPERIORES -> 0.035 + (random.nextDouble() * 0.030); // 0.035 - 0.065
    };
  }
}