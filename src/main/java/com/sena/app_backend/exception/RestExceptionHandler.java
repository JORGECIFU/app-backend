package com.sena.app_backend.exception;

import org.springframework.http.HttpStatus;
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

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String,String> handleOther(RuntimeException ex) {
    return Map.of("error", ex.getMessage());
  }

}