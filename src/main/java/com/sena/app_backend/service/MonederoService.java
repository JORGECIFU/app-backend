package com.sena.app_backend.service;

import com.sena.app_backend.dto.request.NuevaCuentaMonederoRequest;
import com.sena.app_backend.dto.request.NuevaTransaccionMonederoRequest;
import com.sena.app_backend.dto.response.MonederoResponse;
import com.sena.app_backend.dto.response.TransaccionMonederoResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface MonederoService {


  /**
   * Crea un nuevo monedero para el usuario autenticado.
   *
   * @param auth Información de autenticación del usuario
   * @param req DTO que contiene los detalles del nuevo monedero
   * @return MonederoResponse con los detalles del monedero creado
   */
  MonederoResponse crearMonedero(Authentication auth, NuevaCuentaMonederoRequest req);

  /**
   * Lista todos los monederos del usuario autenticado.
   *
   * @param auth Información de autenticación del usuario
   * @return Lista de MonederoResponse con los detalles de cada monedero
   */
  List<MonederoResponse> listarMonederos(Authentication auth);

  /**
   * Mueve fondos entre monederos o a una cuenta externa.
   *
   * @param auth Información de autenticación del usuario
   * @param monederoId ID del monedero desde el cual se moverán los fondos
   * @param req DTO que contiene los detalles de la transacción
   */
  TransaccionMonederoResponse moverFondos(Authentication auth, Long monederoId, NuevaTransaccionMonederoRequest req);

  /**
   * Obtiene el historial de transacciones de un monedero específico.
   * @param auth Información de autenticación del usuario
   * @param monederoId ID del monedero del cual se desea obtener el historial
   * @return Lista de TransaccionMonederoResponse con el historial de transacciones
   */
  List<TransaccionMonederoResponse> historial(Authentication auth, Long monederoId);
}
