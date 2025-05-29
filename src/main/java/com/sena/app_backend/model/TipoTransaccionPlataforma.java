package com.sena.app_backend.model;

/**
 * Esta clase representa los tipos de transacciones que se pueden realizar
 * en la aplicación internamente, como recargas, pagos de alquiler y retiros.
 */
public enum TipoTransaccionPlataforma {
  RECARGA_PLATAFORMA,   // Ingreso de fondos desde pasarela de pago
  PAGO_ALQUILER,        // Descuento para iniciar o renovar alquilera
  GANANCIA_ALQUILER,    // Transferencia de ganancias al monedero del usuario
  RETIRO_WALLET         // Transferencia de plataforma al monedero digital
}
