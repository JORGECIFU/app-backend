package com.sena.app_backend.service;

import com.sena.app_backend.dto.request.NuevaTransaccionRequest;
import com.sena.app_backend.dto.response.CuentaPlataformaResponse;
import com.sena.app_backend.dto.response.TransaccionPlataformaResponse;
import java.util.List;

public interface PlataformaCuentaService {

  /**
   * Obtiene la cuenta de plataforma asociada a un usuario.
   *
   * @param usuarioId ID del usuario
   * @return CuentaPlataformaResponse con los detalles de la cuenta
   */
  CuentaPlataformaResponse obtenerCuenta(Long usuarioId);

  /**
   * Lista el historial de transacciones de un usuario.
   *
   *  @param usuarioId ID del usuario
   *  @return Lista de TransaccionPlataformaResponse con el historial de transacciones
   */
  List<TransaccionPlataformaResponse> listarHistorial(Long usuarioId);

  /**
   * Crea una nueva transacción en la cuenta de un usuario.
   *
   * @param usuarioId ID del usuario cuya cuenta se desea modificar
   * @param req DTO que contiene los detalles de la nueva transacción
   * @return TransaccionPlataformaResponse con los detalles de la transacción creada
   */
  TransaccionPlataformaResponse crearTransaccion(Long usuarioId, NuevaTransaccionRequest req);
}
