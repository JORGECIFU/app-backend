package com.sena.app_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Excepción para indicar que no hay fondos suficientes en la cuenta.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException() {
    super("Fondos insuficientes");
  }
}


