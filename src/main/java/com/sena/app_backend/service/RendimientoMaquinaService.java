package com.sena.app_backend.service;

import com.sena.app_backend.dto.response.RendimientoMaquinaResponse;
import com.sena.app_backend.model.RendimientoMaquina;

import java.time.LocalDateTime;
import java.util.List;

public interface RendimientoMaquinaService {

    /**
     * Registra los rendimientos actuales de todas las máquinas activas.
     * Este método es ejecutado automáticamente cada 2 minutos.
     */
    void registrarRendimientos();

    /**
     * Obtiene el historial de rendimientos de una máquina específica
     * en un rango de fechas.
     *
     * @param maquinaId el ID de la máquina
     * @param fechaInicio fecha de inicio del rango
     * @param fechaFin fecha de fin del rango
     * @return lista de rendimientos en el rango especificado
     */
    List<RendimientoMaquinaResponse> obtenerHistorialMaquina(Long maquinaId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Obtiene los rendimientos de una máquina para un día específico.
     *
     * @param maquinaId el ID de la máquina
     * @param fecha la fecha específica
     * @return el rendimiento del día o null si no existe
     */
    RendimientoMaquinaResponse obtenerRendimientoDiario(Long maquinaId, LocalDateTime fecha);

    /**
     * Genera un valor de rendimiento simulado basado en el nivel de recursos.
     *
     * @param maquinaId el ID de la máquina
     * @return el valor de rendimiento generado
     */
    Double generarRendimientoParaMaquina(Long maquinaId);
}