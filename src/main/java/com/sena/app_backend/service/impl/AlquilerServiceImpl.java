package com.sena.app_backend.service.impl;

import com.sena.app_backend.dto.request.NuevaTransaccionRequest;
import com.sena.app_backend.dto.request.NuevoAlquilerRequest;
import com.sena.app_backend.dto.response.AlquilerPreviewAdminDto;
import com.sena.app_backend.dto.response.AlquilerPreviewUserDto;
import com.sena.app_backend.dto.response.AlquilerResponse;
import com.sena.app_backend.dto.response.PreciosPlan;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
   * Mapea la entidad Alquiler a DTO de respuesta.
   */
  private AlquilerResponse mapToDto(Alquiler a) {
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
        .build();
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
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userEmail));
    Maquina maquina = maquinaRepo.findById(req.getMaquinaId())
        .orElseThrow(() -> new RuntimeException("Máquina no encontrada"));
    Plan plan = planRepo.findById(req.getPlanId())
        .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

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
    // 1) Recuperamos el alquiler
    Alquiler a = alquilerRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Alquiler no encontrado"));

    // 2) Solo actuamos si sigue ACTIVO
    if (a.getEstado() == EstadoAlquiler.ACTIVO) {
      // 2.1) Calculamos el resultado económico del plan
      PreciosPlan pr = CalculadorPreciosPlan.calculate(a.getPlan());
      // Rendimiento del plan para el usuario
      BigDecimal gananciaUsuario = pr.getGananciaMaxUsuario();

      // 2.2) Creamos una transacción de PAGO_ALQUILER en la cuenta de la plataforma
      //     Esto abonará al usuario su beneficio neto al finalizar el alquiler
      plataformaService.crearTransaccion(
          a.getUsuario().getId(),
          new NuevaTransaccionRequest(
              TipoTransaccionPlataforma.GANANCIA_ALQUILER,
              gananciaUsuario
          )
      );

      // 2.3) Marcamos el alquiler como cerrado
      a.setEstado(EstadoAlquiler.CERRADO);
      alquilerRepo.save(a);
    }
  }

  /**
   * Cierra un alquiler, actualizando su estado a CERRADO.
   * <p>
   * 1. Busca el alquiler por ID.
   * 2. Cambia su estado a CERRADO.
   * 3. Guarda los cambios en la base de datos.
   *
   * @param planId ID del alquiler a cerrar
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
}
