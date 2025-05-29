package com.sena.app_backend.repository;

import com.sena.app_backend.model.PlataformaFondosCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlataformaFondosCuentaRepository extends JpaRepository<PlataformaFondosCuenta, Long> {
  Optional<PlataformaFondosCuenta> findByUsuarioId(Long usuarioId);
}
