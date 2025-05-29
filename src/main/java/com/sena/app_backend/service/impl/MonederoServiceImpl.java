package com.sena.app_backend.service.impl;

import com.sena.app_backend.client.CoinbaseClient;
import com.sena.app_backend.dto.request.*;
import com.sena.app_backend.dto.response.*;
import com.sena.app_backend.exception.InsufficientFundsException;
import com.sena.app_backend.model.*;
import com.sena.app_backend.repository.*;
import com.sena.app_backend.service.MonederoService;
import com.sena.app_backend.service.PlataformaCuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonederoServiceImpl implements MonederoService {

  private final MonederoRepository monederoRepo;
  private final TransaccionRepository txRepo;
  private final UsuarioRepository usuarioRepo;
  private final PlataformaCuentaService plataformaService;
  private final CoinbaseClient coinbaseClient;

  private Usuario getUsuario(Authentication auth) {
    String email = auth.getName();
    return usuarioRepo.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
  }

  @Override
  @Transactional
  public MonederoResponse crearMonedero(Authentication auth, NuevaCuentaMonederoRequest req) {
    Usuario u = getUsuario(auth);
    Monedero m = Monedero.builder()
        .usuario(u)
        .alias(req.getAlias())
        .moneda(req.getMoneda())
        .saldoActual(BigDecimal.ZERO)
        .build();
    m = monederoRepo.save(m);
    return MonederoResponse.builder()
        .monederoId(m.getId())
        .usuarioId(u.getId())
        .alias(m.getAlias())
        .moneda(m.getMoneda())
        .saldoActual(m.getSaldoActual())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<MonederoResponse> listarMonederos(Authentication auth) {
    Usuario u = getUsuario(auth);
    return monederoRepo.findByUsuarioId(u.getId()).stream()
        .map(m -> MonederoResponse.builder()
            .monederoId(m.getId())
            .usuarioId(u.getId())
            .alias(m.getAlias())
            .moneda(m.getMoneda())
            .saldoActual(m.getSaldoActual())
            .build())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public TransaccionMonederoResponse moverFondos(
      Authentication auth,
      Long monederoId,
      NuevaTransaccionMonederoRequest req
  ) {
    // 1) Obtiene el usuario a partir del token
    Usuario u = getUsuario(auth);

    // 2) Carga el monedero y valida que pertenezca al usuario
    Monedero m = monederoRepo.findByIdAndUsuarioId(monederoId, u.getId())
        .orElseThrow(() -> new RuntimeException("Monedero no encontrado"));

    // 3) Spot price USD/<CRYPTO> (ej. USD/BTC o USD/ETH)
    String moneda = m.getMoneda().name();  // asume enum BITCOIN, ETHEREUM
    BigDecimal spotPrice = coinbaseClient.getSpotPrice(moneda);

    // 4) Cálculo de cantidades:
    //    - Para RECARGA_DESDE_PLATAFORMA: convierto USD → cripto
    //    - Para PASO_A_PLATAFORMA: convierto cripto → USD
    BigDecimal usdAmount    = req.getUsdAmount();
    BigDecimal cryptoAmount;
    BigDecimal usdToPlatform;

    if (req.getTipo() == TipoTransaccionMonedero.RECARGA_DESDE_PLATAFORMA) {
      // USD → CRYPTO
      cryptoAmount    = usdAmount.divide(spotPrice, 8, RoundingMode.HALF_UP);
      usdToPlatform   = usdAmount;  // monto en USD a debitar de la plataforma

      // 5) Debita USD de la cuenta de plataforma
      plataformaService.crearTransaccion(
          u.getId(),
          new NuevaTransaccionRequest(
              TipoTransaccionPlataforma.RETIRO_WALLET,
              usdToPlatform
          )
      );
      // 6) Abona cripto al monedero
      m.setSaldoActual(m.getSaldoActual().add(cryptoAmount));

    } else { // PASO_A_PLATAFORMA
      // CRYPTO → USD
      cryptoAmount    = req.getCryptoAmount(); // aquí si quieres que el front pase la cripto
      usdToPlatform   = cryptoAmount.multiply(spotPrice).setScale(4, RoundingMode.HALF_UP);

      if (m.getSaldoActual().compareTo(cryptoAmount) < 0) {
        throw new InsufficientFundsException();
      }

      // 5) Abona USD a la cuenta de plataforma
      plataformaService.crearTransaccion(
          u.getId(),
          new NuevaTransaccionRequest(
              TipoTransaccionPlataforma.RECARGA_PLATAFORMA,
              usdToPlatform
          )
      );
      // 6) Debita cripto del monedero
      m.setSaldoActual(m.getSaldoActual().subtract(cryptoAmount));
    }

    // 7) Persistir cambio de saldo
    monederoRepo.save(m);

    // 8) Registrar transacción de monedero
    Transaccion tx = Transaccion.builder()
        .monedero(m)
        .tipo(req.getTipo())
        .monto(cryptoAmount)          // monto en cripto
        .fechaTransaccion(LocalDateTime.now())
        .build();
    tx = txRepo.save(tx);

    // 9) Devolver DTO con saldo actualizado
    return TransaccionMonederoResponse.builder()
        .id(tx.getId())
        .tipo(tx.getTipo())
        .monto(tx.getMonto())
        .fechaTransaccion(tx.getFechaTransaccion())
        .saldoPosterior(m.getSaldoActual())
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<TransaccionMonederoResponse> historial(Authentication auth, Long monederoId) {
    Usuario u = getUsuario(auth);
    Monedero m = monederoRepo.findByIdAndUsuarioId(monederoId, u.getId())
        .orElseThrow(() -> new RuntimeException("Monedero no encontrado"));
    return txRepo.findByMonederoIdOrderByFechaTransaccionDesc(m.getId()).stream()
        .map(tx -> TransaccionMonederoResponse.builder()
            .id(tx.getId())
            .tipo(tx.getTipo())
            .monto(tx.getMonto())
            .fechaTransaccion(tx.getFechaTransaccion())
            .saldoPosterior(tx.getMonedero().getSaldoActual())
            .build())
        .collect(Collectors.toList());
  }
}