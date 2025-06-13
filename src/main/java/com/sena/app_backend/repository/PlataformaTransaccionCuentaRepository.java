package com.sena.app_backend.repository;

import com.sena.app_backend.model.PlataformaTransaccionCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlataformaTransaccionCuentaRepository extends JpaRepository<PlataformaTransaccionCuenta, Long> {
  List<PlataformaTransaccionCuenta> findByAccountIdOrderByFechaTransaccionDesc(Long accountId);

  Iterable<? extends PlataformaTransaccionCuenta> findByAccountId(Long accountId);
}
