package com.sena.app_backend.service.impl;

import com.sena.app_backend.dto.request.NuevaTransaccionRequest;
import com.sena.app_backend.dto.request.NuevoAlquilerRequest;
import com.sena.app_backend.dto.response.*;
import com.sena.app_backend.exception.InsufficientFundsException;
import com.sena.app_backend.model.*;
import com.sena.app_backend.repository.AlquilerRepository;
import com.sena.app_backend.repository.MaquinaRepository;
import com.sena.app_backend.repository.PlanRepository;
import com.sena.app_backend.repository.UsuarioRepository;
import com.sena.app_backend.service.AlquilerService;
import com.sena.app_backend.service.PlataformaCuentaService;
import com.sena.app_backend.util.CalculadorPreciosPlan;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;

/**
 * Servicio para gestión de alquileres.
 * <p>
 * Implementa la creación, listado, obtención y cierre de alquileres,
 * incluyendo el cálculo detallado del costoTotal aplicando comisión de plataforma.
 */
@Service
@RequiredArgsConstructor
public class AlquilerServiceImpl implements AlquilerService {

  private final AlquilerRepository alquilerRepo;
  private final UsuarioRepository usuarioRepo;
  private final MaquinaRepository maquinaRepo;
  private final PlanRepository planRepo;
  private final PlataformaCuentaService plataformaService;


  /**
   * Mapea la entidad Alquiler a DTO de respuesta para el administrador,
   * calculando también cuánto ganó la plataforma.
   */
  private AlquilerAdminResponse mapToAdminDto(Alquiler a) {

    return AlquilerAdminResponse.builder()
        .id(a.getId())
        .usuarioId(a.getUsuario().getId())
        .maquinaId(a.getMaquina().getId())
        .planId(a.getPlan().getId())
        .fechaInicio(a.getFechaInicio())
        .fechaFin(a.getFechaFin())
        .precioAlquiler(a.getPrecioAlquiler())
        .costoTotal(a.getCostoTotal())
        .estado(a.getEstado())
        .montoDevuelto(Optional.ofNullable(a.getMontoDevuelto()).orElse(ZERO))
        .gananciaPlataforma(Optional.ofNullable(a.getGananciaPlataforma()).orElse(ZERO))
        .build();
  }


  /**
   * Calcula el monto a devolver al usuario en caso de cierre anticipado del alquiler.
   * <p>
   * Si el alquiler ya ha finalizado, devuelve cero.
   * Si aún está activo, calcula la parte no consumida del precioAlquiler.
   *
   * @param a Alquiler del cual se calculará el monto devuelto
   * @return Monto a devolver al usuario
   */
  public BigDecimal calcularMontoDevuelto(Alquiler a) {
    LocalDateTime ahora = LocalDateTime.now();

    // Si ya pasó la fechaFin, no hay devolución
    if (!ahora.isBefore(a.getFechaFin())) {
      return ZERO;
    }

    // Calculamos la fracción de tiempo usado
    long totalSegundos = java.time.Duration.between(a.getFechaInicio(), a.getFechaFin()).getSeconds();
    long usadosSegundos = java.time.Duration.between(a.getFechaInicio(), ahora).getSeconds();

    if (usadosSegundos <= 0) {
      // Si aún no empieza, devolver todo el precioAlquiler
      return a.getPrecioAlquiler();
    }

    // Calculamos la fracción usada
    BigDecimal fraccionUsada = new BigDecimal(usadosSegundos)
        .divide(new BigDecimal(totalSegundos), 8, RoundingMode.HALF_UP);

    // La parte no consumida del precioAlquiler
    BigDecimal montoNoUsado = a.getPrecioAlquiler()
        .multiply(BigDecimal.ONE.subtract(fraccionUsada))
        .setScale(4, RoundingMode.HALF_UP);

    // La ganancia de la plataforma es proporcional al tiempo usado
    BigDecimal gananciaPlataforma = a.getCostoTotal()
        .subtract(a.getPrecioAlquiler())
        .multiply(fraccionUsada)
        .setScale(4, RoundingMode.HALF_UP);

    return montoNoUsado;
  }

  /**
   * Mapea la entidad Alquiler a DTO de respuesta.
   */
  private AlquilerResponse mapToDto(Alquiler a) {
    // 1) Calcular cuánto corresponde devolver (solo si se cerró anticipadamente)
    BigDecimal montoDevuelto = ZERO;
    if (EstadoAlquiler.CERRADO.equals(a.getEstado())) {
      montoDevuelto = calcularMontoDevuelto(a);
    }

    return AlquilerResponse.builder()
        .id(a.getId())
        .usuarioId(a.getUsuario().getId())
        .maquinaId(a.getMaquina().getId())
        .planId(a.getPlan().getId())
        .fechaInicio(a.getFechaInicio())
        .fechaFin(a.getFechaFin())
        .precioAlquiler(a.getPrecioAlquiler())
        .costoTotal(a.getCostoTotal())
        .estado(a.getEstado())
        .montoDevuelto(a.getMontoDevuelto())
        .gananciaPlataforma(a.getGananciaPlataforma())
        .build();
  }

  /**
   * Obtiene un alquiler detallado para el administrador.
   * <p>
   * 1. Busca el alquiler por ID.
   * 2. Mapea la entidad a DTO de respuesta para el administrador.
   *
   * @param id ID del alquiler a recuperar
   * @return DTO de respuesta con detalles del alquiler
   */
  @Override
  public AlquilerAdminResponse obtenerAlquilerParaAdmin(Long id) {
    Alquiler a = alquilerRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Alquiler no encontrado"));
    return mapToAdminDto(a);
  }

  /**
   * Lista todos los alquileres para el administrador.
   * <p>
   * 1. Recupera todos los alquileres de la base de datos.
   * 2. Mapea cada alquiler a su DTO correspondiente para el administrador.
   *
   * @return lista de DTOs de respuesta con detalles de cada alquiler
   */
  @Override
  public List<AlquilerAdminResponse> listarAlquileresParaAdmin() {
    return alquilerRepo.findAll().stream()
        .map(this::mapToAdminDto)
        .collect(Collectors.toList());
  }

  /**
   * Crea un nuevo alquiler.
   * <p>
   * 1. Valida que el usuario, máquina y plan existan.
   * 2. Calcula fechas de inicio y fin basadas en la duración del plan.
   * 3. Calcula el costoTotal aplicando comisión de plataforma al ingreso bruto estimado.
   * 4. Persiste el alquiler solo si el cálculo es exitoso.
   *
   * @param req petición con usuarioId, maquinaId y planId
   * @return DTO con detalle del alquiler creado
   */
  @Override
  public AlquilerResponse crearAlquiler(NuevoAlquilerRequest req, String userEmail) {
    // 1. Recupera entidades relacionadas
    Usuario usuario = usuarioRepo.findByEmail(userEmail)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    Plan plan = planRepo.findById(req.getPlanId())
        .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

    // Determinar nivel de recursos según el plan
    NivelRecursos nivelRequerido = switch (plan.getNombre().toUpperCase()) {
      case "BASIC" -> NivelRecursos.BAJOS;
      case "GOLD" -> NivelRecursos.MEDIOS;
      case "PREMIUM" -> NivelRecursos.ALTOS;
      case "VIP" -> NivelRecursos.SUPERIORES;
      default -> throw new RuntimeException("Nivel de plan no reconocido");
    };

    // Buscar primera máquina disponible con los recursos requeridos
    Maquina maquina = maquinaRepo.findFirstByEstadoAndRecursos(
            EstadoMaquina.DISPONIBLE, nivelRequerido)
        .orElseThrow(() -> new RuntimeException(
            "No hay máquinas disponibles para el nivel requerido"));

    // Marcar máquina como rentada
    maquina.setEstado(EstadoMaquina.RENTADA);
    maquinaRepo.save(maquina);

    // 2. Fechas de inicio y fin del periodo
    LocalDateTime inicio = LocalDateTime.now();
    BigDecimal duracion = plan.getDuracionDias();
    LocalDateTime fin = inicio
        .plusDays(duracion.toBigInteger().longValue())
        .plusSeconds(
            duracion
                .subtract(new BigDecimal(duracion.toBigInteger()))
                .multiply(BigDecimal.valueOf(86400)) // segundos por día
                .longValue()
        );

    // 3. Delegamos el cálculo de precios
    PreciosPlan pricing = CalculadorPreciosPlan.calculate(plan);
    BigDecimal precioBruto = pricing.getPrecioBruto();
    BigDecimal precioAlquiler = pricing.getPrecioAlquiler();


    // 4) Intentar débito automático de fondos
    try {
      plataformaService.crearTransaccion(
          usuario.getId(),
          new NuevaTransaccionRequest(
              TipoTransaccionPlataforma.PAGO_ALQUILER,
              precioAlquiler
          )
      );
    } catch (RuntimeException ex) {
      // Si viene de falta de fondos, lanzamos la excepción
      throw new InsufficientFundsException();
    }
    ;

    // 5. Construye y guarda la entidad Alquiler
    Alquiler a = Alquiler.builder()
        .usuario(usuario)
        .maquina(maquina)
        .plan(plan)
        .fechaInicio(inicio)
        .fechaFin(fin)
        .precioAlquiler(precioAlquiler)
        .costoTotal(precioBruto)
        .estado(EstadoAlquiler.ACTIVO)
        .build();

    Alquiler saved = alquilerRepo.save(a);
    return mapToDto(saved);
  }

  /**
   * Listar todos los alquileres.
   *
   * @return lista de DTOs de respuesta con detalles de cada alquiler
   */
  @Override
  public List<AlquilerResponse> listarAlquileres() {
    return alquilerRepo.findAll()
        .stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  @Override
  public AlquilerResponse obtenerAlquiler(Long id) {
    Alquiler a = alquilerRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Alquiler no encontrado"));
    return mapToDto(a);
  }

  /**
   * Cierra un alquiler, actualizando su estado a CERRADO.
   * <p>
   * 1. Busca el alquiler por ID.
   * 2. Cambia su estado a CERRADO.
   * 3. Guarda los cambios en la base de datos.
   *
   * @param id ID del alquiler a cerrar
   */
  @Override
  @Transactional
  public void cerrarAlquiler(Long id) {
    // 1. Recuperar el alquiler por ID o lanzar excepción si no existe
    Alquiler a = alquilerRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Alquiler no encontrado"));

    // 2. Solo procesar si el alquiler está ACTIVO
    if (a.getEstado() != EstadoAlquiler.ACTIVO) {
      return;
    }

    // 3. Obtener el instante actual y determinar si se completó el periodo contratado
    LocalDateTime ahora = LocalDateTime.now();
    boolean completo = !ahora.isBefore(a.getFechaFin());

    if (completo) {
      // --- Caso A: Cierre normal (el mes llegó a su fin) ---

      // 4. Total bruto que la plataforma debe recaudar tras completar el periodo
      BigDecimal totalBruto = a.getCostoTotal().setScale(4, RoundingMode.HALF_UP);

      // 5. Como no hay devolución al usuario, montoDevuelto = 0
      a.setMontoDevuelto(BigDecimal.ZERO);

      // 6. La ganancia de la plataforma es todo el costo bruto
      a.setGananciaPlataforma(totalBruto);

      // 7. Registrar la transacción de ganancia completa en la cuenta del usuario
      plataformaService.crearTransaccion(
          a.getUsuario().getId(),
          new NuevaTransaccionRequest(
              TipoTransaccionPlataforma.GANANCIA_ALQUILER,
              totalBruto
          )
      );

    } else {
      // --- Caso B: Cierre anticipado (por mantenimiento u otro motivo) ---

      // 8. Calcular fracción de tiempo efectivamente usado
      BigDecimal frUsada = calcularFraccionUsada(a);
      //    frUsada = segundos transcurridos / segundos totales del periodo

      // 9. Extra total que la plataforma debe facturar a lo largo del mes
      BigDecimal extraTot = a.getCostoTotal().subtract(a.getPrecioAlquiler());
      //    extraTot = precioBruto – anticipo (precioAlquiler)

      // 10. Extra ya “consumido” por la plataforma según fracción usada
      BigDecimal extraUsd = extraTot.multiply(frUsada)
          .setScale(4, RoundingMode.HALF_UP);

      // 11. Parte del anticipo que corresponde al tiempo usado
      BigDecimal algUsd = a.getPrecioAlquiler().multiply(frUsada)
          .setScale(4, RoundingMode.HALF_UP);

      // 12. Monto a devolver al usuario = anticipo – parte usada
      BigDecimal montoDev = a.getPrecioAlquiler().subtract(algUsd)
          .setScale(4, RoundingMode.HALF_UP);

      // 13. Asignar en la entidad los valores calculados
      a.setMontoDevuelto(montoDev);
      a.setGananciaPlataforma(extraUsd);

      // 14. Registrar la transacción de devolución al usuario
      if (montoDev.compareTo(BigDecimal.ZERO) > 0) {
        plataformaService.crearTransaccion(
            a.getUsuario().getId(),
            new NuevaTransaccionRequest(
                TipoTransaccionPlataforma.CANCELACION_ALQUILER,
                montoDev
            )
        );
      }

      // 15. Registrar la transacción de ganancia proporcional de la plataforma
      if (extraUsd.compareTo(BigDecimal.ZERO) > 0) {
        plataformaService.crearTransaccion(
            a.getUsuario().getId(),
            new NuevaTransaccionRequest(
                TipoTransaccionPlataforma.GANANCIA_ALQUILER,
                extraUsd
            )
        );
      }

      // 16. Poner la máquina en mantenimiento
      a.getMaquina().setEstado(EstadoMaquina.MANTENIMIENTO);
      maquinaRepo.save(a.getMaquina());

      // 17. Acortar el periodo de alquiler para reflejar la fecha real de cierre
      a.setFechaFin(ahora);
    }

    // 18. Marcar el alquiler como CERRADO
    a.setEstado(EstadoAlquiler.CERRADO);

    // 19. Guardar todos los cambios en la base de datos
    alquilerRepo.save(a);
  }


  /**
   * Previsualiza un plan de alquiler desde la perspectiva del administrador.
   * <p>
   * 1. Busca el plan por ID.
   * 2. Calcula precios y ganancias usando el método de cálculo.
   *
   * @param planId ID del plan a previsualizar
   * @return DTO con detalles del plan para el administrador
   */
  @Override
  public AlquilerPreviewAdminDto previewAdmin(Long planId) {
    Plan plan = planRepo.findById(planId)
        .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

    PreciosPlan pr = CalculadorPreciosPlan.calculate(plan);

    return new AlquilerPreviewAdminDto(
        plan.getId(),
        plan.getNombre(),
        plan.getDuracionDias(),
        pr.getGananciaPromedioDiaria(),
        pr.getPrecioBruto(),
        pr.getPrecioAlquiler(),
        pr.getGananciaMaxUsuario(),
        pr.getIngresoPlataforma()
    );
  }

  /**
   * Previsualiza un plan de alquiler desde la perspectiva del usuario.
   * <p>
   * 1. Busca el plan por ID.
   * 2. Calcula precios y ganancias usando el método de cálculo.
   *
   * @param planId ID del plan a previsualizar
   * @return DTO con detalles del plan para el usuario
   */
  @Override
  public AlquilerPreviewUserDto previewUser(Long planId) {
    AlquilerPreviewAdminDto adminDto = previewAdmin(planId);
    return new AlquilerPreviewUserDto(
        adminDto.getPlanId(),
        adminDto.getPlanNombre(),
        adminDto.getDuracionDias(),
        adminDto.getGananciaPromedioDiaria(),
        adminDto.getPrecioBruto(),
        adminDto.getPrecioAlquiler(),
        adminDto.getGananciaMaxUsuario()
    );
  }

  /**
   * Lista los alquileres activos de un usuario específico.
   * <p>
   * 1. Busca los alquileres por ID de usuario y estado ACTIVO.
   * 2. Mapea cada alquiler a su DTO correspondiente.
   *
   * @param usuarioId ID del usuario
   * @return lista de DTOs de respuesta con detalles de cada alquiler activo
   */
  @Override
  public List<AlquilerResponse> listarActivosPorUsuario(Long usuarioId) {
    return alquilerRepo
        .findByUsuarioIdAndEstado(usuarioId, EstadoAlquiler.ACTIVO)
        .stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  /**
   * Previsualiza todos los planes de alquiler desde la perspectiva del administrador.
   * <p>
   * 1. Busca todos los planes disponibles.
   * 2. Mapea cada plan a su DTO correspondiente, calculando precios y ganancias.
   *
   * @return lista de DTOs de respuesta con detalles de cada plan para el administrador
   */
  @Override
  public List<AlquilerPreviewAdminDto> previewAllAdmin() {
    return planRepo.findAll().stream()
        .map(plan -> {
          PreciosPlan pr = CalculadorPreciosPlan.calculate(plan);
          return new AlquilerPreviewAdminDto(
              plan.getId(), plan.getNombre(), plan.getDuracionDias(),
              pr.getGananciaPromedioDiaria(), pr.getPrecioBruto(),
              pr.getPrecioAlquiler(), pr.getGananciaMaxUsuario(),
              pr.getIngresoPlataforma()
          );
        })
        .collect(Collectors.toList());
  }

  /**
   * Previsualiza todos los planes de alquiler desde la perspectiva del usuario.
   * <p>
   * 1. Busca todos los planes disponibles.
   * 2. Mapea cada plan a su DTO correspondiente, calculando precios y ganancias.
   *
   * @return lista de DTOs de respuesta con detalles de cada plan para el usuario
   */
  @Override
  public List<AlquilerPreviewUserDto> previewAllUser() {
    return previewAllAdmin().stream()
        .map(adminDto -> new AlquilerPreviewUserDto(
            adminDto.getPlanId(),
            adminDto.getPlanNombre(),
            adminDto.getDuracionDias(),
            adminDto.getGananciaPromedioDiaria(),
            adminDto.getPrecioBruto(),
            adminDto.getPrecioAlquiler(),
            adminDto.getGananciaMaxUsuario()
        ))
        .collect(Collectors.toList());
  }

  /**
   * Retorna la fracción (entre 0 y 1) de días ya consumidos
   * en función de la fecha de inicio, fecha de cierre actual (si es anticipado)
   * y la longitud total en días del plan.
   */
  public BigDecimal calcularFraccionUsada(Alquiler a) {
    LocalDateTime ahora = LocalDateTime.now();
    // Si ya pasó de la fechaFin, lo tratamos como “completo”
    if (!ahora.isBefore(a.getFechaFin())) {
      return BigDecimal.ONE;
    }
    // Total de segundos del período contratado
    long totalSegundos = java.time.Duration.between(a.getFechaInicio(), a.getFechaFin()).getSeconds();
    // Segundos consumidos hasta “ahora”
    long usadosSegundos = java.time.Duration.between(a.getFechaInicio(), ahora).getSeconds();
    if (usadosSegundos <= 0) {
      return ZERO;
    }
    // fracción usada = usadosSegundos / totalSegundos (BigDecimal con precisión)
    return new BigDecimal(usadosSegundos)
        .divide(new BigDecimal(totalSegundos), 8, RoundingMode.HALF_UP);
  }

  /**
   * Lista los alquileres cerrados de un usuario específico.
   * <p>
   * 1. Busca los alquileres por ID de usuario y estado CERRADO.
   * 2. Mapea cada alquiler a su DTO correspondiente.
   *
   * @param usuarioId ID del usuario
   * @return lista de DTOs de respuesta con detalles de cada alquiler cerrado
   */
  @Override
  public List<AlquilerResponse> listarCerradosPorUsuario(Long usuarioId) {
    return alquilerRepo
        .findByUsuarioIdAndEstado(usuarioId, EstadoAlquiler.CERRADO)
        .stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }
}
