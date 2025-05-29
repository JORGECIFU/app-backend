package com.sena.app_backend.service.impl;

import com.sena.app_backend.dto.request.NuevaTransaccionRequest;
import com.sena.app_backend.dto.response.CuentaPlataformaResponse;
import com.sena.app_backend.dto.response.TransaccionPlataformaResponse;
import com.sena.app_backend.exception.InsufficientFundsException;
import com.sena.app_backend.model.*;
import com.sena.app_backend.repository.*;
import com.sena.app_backend.service.PlataformaCuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de gestión de cuentas de plataforma.
 * Esta clase maneja la lógica de negocio relacionada con las cuentas de los usuarios en la plataforma,
 */
@Service
@RequiredArgsConstructor
public class PlataformaCuentaServiceImpl implements PlataformaCuentaService {

  private final PlataformaFondosCuentaRepository cuentaRepo;
  private final PlataformaTransaccionCuentaRepository transRepo;
  private final UsuarioRepository usuarioRepo;

  /**
   * Convierte una entidad PlataformaFondosCuenta a un DTO de respuesta CuentaPlataformaResponse.
   *
   * @param usuarioId la entidad PlataformaFondosCuenta
   * @return el DTO de respuesta CuentaPlataformaResponse
   */
  @Override
  @Transactional(readOnly = true)
  public CuentaPlataformaResponse obtenerCuenta(Long usuarioId) {
    PlataformaFondosCuenta cuenta = cuentaRepo.findByUsuarioId(usuarioId)
        .orElseThrow(() -> new RuntimeException("Cuenta no encontrada para usuario " + usuarioId));
    return CuentaPlataformaResponse.builder()
        .cuentaId(cuenta.getId())
        .usuarioId(usuarioId)
        .balance(cuenta.getBalance())
        .build();
  }

  /**
   *
   * Obtiene el historial de transacciones de un usuario.
   * <p>
   *   1. Busca la cuenta asociada al usuario por su ID.
   *   2. Si no se encuentra la cuenta, lanza una excepción.
   *   3. Recupera todas las transacciones asociadas a la cuenta,
   *   ordenadas por fecha de transacción de forma descendente.
   *   4. Mapea cada transacción a un DTO de respuesta TransaccionPlataformaResponse,
   *   incluyendo el balance posterior.
   *   5. Retorna la lista de transacciones.
   *   @param usuarioId el ID del usuario
   *   @return una lista de transacciones del usuario
   */
  @Override
  @Transactional(readOnly = true)
  public List<TransaccionPlataformaResponse> listarHistorial(Long usuarioId) {
    PlataformaFondosCuenta cuenta = cuentaRepo.findByUsuarioId(usuarioId)
        .orElseThrow(() -> new RuntimeException("Cuenta no encontrada para usuario " + usuarioId));
    return transRepo.findByAccountIdOrderByFechaTransaccionDesc(cuenta.getId())
        .stream()
        .map(tx -> TransaccionPlataformaResponse.builder()
            .id(tx.getId())
            .tipo(tx.getTipo())
            .monto(tx.getMonto())
            .fechaTransaccion(tx.getFechaTransaccion())
            .balancePosterior(cuenta.getBalance())
            .build())
        .collect(Collectors.toList());
  }

  /**
   * Crea una nueva transacción en la cuenta del usuario.
   * <p>
   *   1. Busca el usuario por ID.
   *   2. Si no se encuentra, lanza una excepción.
   *   3. Busca la cuenta asociada al usuario, o crea una nueva si no existe.
   *   4. Calcula el nuevo balance
   *   según el tipo de transacción:
   *   - RECARGA_PLATAFORMA: suma el monto al balance.
   *   - PAGO_ALQUILER: resta el monto del balance, validando que haya suficiente saldo.
   *   - RETIRO_WALLET: resta el monto del balance, validando que haya suficiente saldo.
   *   5. Actualiza el balance de la cuenta.
   *   6. Crea una nueva transacción con los detalles proporcionados.
   *   7. Guarda la transacción en la base de datos.
   *   8. Retorna un DTO de respuesta TransaccionPlataformaResponse con los detalles de la transacción creada.
   *   @param usuarioId el ID del usuario que realiza la transacción
   *   @param req la solicitud de nueva transacción
   *   @return un DTO de respuesta TransaccionPlataformaResponse con los detalles de la transacción creada
   */
  @Override
  @Transactional
  public TransaccionPlataformaResponse crearTransaccion(Long usuarioId, NuevaTransaccionRequest req) {
    Usuario user = usuarioRepo.findById(usuarioId)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    PlataformaFondosCuenta cuenta = cuentaRepo.findByUsuarioId(usuarioId)
        .orElseGet(() -> {
          // si no existe, inicializar cuenta con balance 0
          PlataformaFondosCuenta nueva = PlataformaFondosCuenta.builder()
              .usuario(user)
              .balance(BigDecimal.ZERO)
              .build();
          return cuentaRepo.save(nueva);
        });

    BigDecimal nuevoBalance = switch (req.getTipo()) {
      case RECARGA_PLATAFORMA ->
        // Ingreso de fondos
          cuenta.getBalance().add(req.getMonto());
      case PAGO_ALQUILER -> {
        // Si quieres tratar el pago de alquiler
        if (cuenta.getBalance().compareTo(req.getMonto()) < 0) {
          // lanzamos aquí la excepción
          throw new InsufficientFundsException();
        }
        yield cuenta.getBalance().subtract(req.getMonto());
      }
      case RETIRO_WALLET -> {
        // Retiro de la plataforma al wallet
        if (cuenta.getBalance().compareTo(req.getMonto()) < 0) {
          throw new RuntimeException("Saldo insuficiente para retiro");
        }
        yield cuenta.getBalance().subtract(req.getMonto());
      }
      default -> throw new IllegalArgumentException("Tipo de transacción no soportado: " + req.getTipo());
    };

    // actualizar balance
    cuenta.setBalance(nuevoBalance);
    cuentaRepo.save(cuenta);

    // crear y guardar transacción
    PlataformaTransaccionCuenta tx = PlataformaTransaccionCuenta.builder()
        .account(cuenta)
        .tipo(req.getTipo())
        .monto(req.getMonto())
        .fechaTransaccion(LocalDateTime.now())
        .build();
    PlataformaTransaccionCuenta saved = transRepo.save(tx);

    return TransaccionPlataformaResponse.builder()
        .id(saved.getId())
        .tipo(saved.getTipo())
        .monto(saved.getMonto())
        .fechaTransaccion(saved.getFechaTransaccion())
        .balancePosterior(nuevoBalance)
        .build();
  }
}
