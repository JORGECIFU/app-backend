package com.sena.app_backend.util;

import com.sena.app_backend.model.Alquiler;
import com.sena.app_backend.model.EstadoAlquiler;
import com.sena.app_backend.repository.AlquilerRepository;
import com.sena.app_backend.service.AlquilerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler para cerrar automáticamente los alquileres que han vencido.
 * Se ejecuta cada minuto y cierra los alquileres cuyo estado es ACTIVO
 * y cuya fecha de fin ha pasado.
 */
@Component
@RequiredArgsConstructor
public class AlquilerScheduler {

  private final AlquilerRepository alquilerRepo;
  private final AlquilerService alquilerService;

  /**
   * Cada minuto (puedes ajustar el cron a tu necesidad),
   * busca alquileres ACTIVOs cuya fechaFin ya pasó y los cierra.
   */
  @Scheduled(cron = "0 * * * * *")   // cada minuto en el segundo 0
  public void cerrarAlquileresVencidos() {
    LocalDateTime ahora = LocalDateTime.now();
    // 1. Recuperar alquileres activos vencidos
    List<Alquiler> vencidos = alquilerRepo
        .findByEstadoAndFechaFinBefore(EstadoAlquiler.ACTIVO, ahora);

    if (vencidos.isEmpty()) {
      return; // nada que cerrar
    }

    // 2. Para cada uno, delegar la lógica de cierre al servicio
    vencidos.forEach(a -> {
      try {
        alquilerService.cerrarAlquiler(a.getId());
      } catch (Exception ex) {
        // loguear y continuar con el siguiente
        System.err.printf("Error cerrando alquiler %d: %s%n",
            a.getId(), ex.getMessage());
      }
    });

    // 3. (Opcional) log
    System.out.printf("Intentados cerrar %d alquiler(es) vencido(s) a las %s%n",
        vencidos.size(), ahora);
  }
}

