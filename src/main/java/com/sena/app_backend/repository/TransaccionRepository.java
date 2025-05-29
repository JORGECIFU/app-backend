package com.sena.app_backend.repository;

import com.sena.app_backend.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio para manejar las operaciones de la entidad Transaccion.
 * Permite realizar consultas y operaciones CRUD sobre las transacciones de los monederos.
 */
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
  List<Transaccion> findByMonederoIdOrderByFechaTransaccionDesc(Long monederoId);
}
