package com.sena.app_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(InsufficientFundsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String,String> handleInsufficientFunds(InsufficientFundsException ex) {
    return Map.of("error", ex.getMessage());
  }

  @ExceptionHandler(InvalidRefreshTokenException.class)
  public ResponseEntity<Map<String,String>> handleInvalidRefresh(InvalidRefreshTokenException ex) {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(TokenRefreshException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Map<String,String> handleTokenRefresh(TokenRefreshException ex) {
    // devuelve { "error": "Refresh token [xxx] ha expirado..." }
    return Map.of("error", ex.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String,String> handleOther(RuntimeException ex) {
    return Map.of("error", ex.getMessage());
  }

}