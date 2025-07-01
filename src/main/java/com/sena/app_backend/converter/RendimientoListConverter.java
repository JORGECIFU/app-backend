package com.sena.app_backend.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sena.app_backend.model.RendimientoMaquina;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;
import java.util.TimeZone;

@Converter
public class RendimientoListConverter implements AttributeConverter<List<RendimientoMaquina.RegistroRendimiento>, String> {

  private final ObjectMapper objectMapper;

  public RendimientoListConverter() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    this.objectMapper.setTimeZone(TimeZone.getTimeZone("America/Bogota"));
  }

  @Override
  public String convertToDatabaseColumn(List<RendimientoMaquina.RegistroRendimiento> rendimientos) {
    try {
      if (rendimientos == null || rendimientos.isEmpty()) {
        return "[]";
      }
      return objectMapper.writeValueAsString(rendimientos);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error converting list to JSON", e);
    }
  }

  @Override
  public List<RendimientoMaquina.RegistroRendimiento> convertToEntityAttribute(String json) {
    try {
      if (json == null || json.trim().isEmpty()) {
        return List.of();
      }
      return objectMapper.readValue(json, new TypeReference<List<RendimientoMaquina.RegistroRendimiento>>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error converting JSON to list", e);
    }
  }
}