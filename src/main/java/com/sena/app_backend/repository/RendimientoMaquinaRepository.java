package com.sena.app_backend.repository;

import com.sena.app_backend.model.RendimientoMaquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RendimientoMaquinaRepository extends JpaRepository<RendimientoMaquina, Long> {

    @Query("SELECT r FROM RendimientoMaquina r WHERE r.maquina.id = :maquinaId AND DATE(r.fecha) = DATE(:fecha)")
    Optional<RendimientoMaquina> findByMaquinaIdAndFecha(@Param("maquinaId") Long maquinaId, @Param("fecha") LocalDateTime fecha);

    @Query("SELECT r FROM RendimientoMaquina r WHERE r.maquina.id = :maquinaId AND r.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<RendimientoMaquina> findByMaquinaIdAndFechaBetween(@Param("maquinaId") Long maquinaId, @Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}