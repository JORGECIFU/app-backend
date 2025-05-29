package com.sena.app_backend.security;

import com.sena.app_backend.repository.AlquilerRepository;
import com.sena.app_backend.repository.MonederoRepository;
import com.sena.app_backend.repository.PlataformaFondosCuentaRepository;
import com.sena.app_backend.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("securityService")
public class SecurityService {
  private final UsuarioRepository usuarioRepo;
  private final AlquilerRepository alquilerRepo;
  private final PlataformaFondosCuentaRepository cuentaRepo;
  private final MonederoRepository monederoRepo;

  public SecurityService(UsuarioRepository usuarioRepo,
                         AlquilerRepository alquilerRepo,
                         PlataformaFondosCuentaRepository cuentaRepo, MonederoRepository monederoRepo) {
    this.usuarioRepo   = usuarioRepo;
    this.alquilerRepo  = alquilerRepo;
    this.cuentaRepo    = cuentaRepo;
    this.monederoRepo = monederoRepo;
  }

  /** Verifica que el id pasado sea el del usuario autenticado */
  public boolean isCurrentUser(Long usuarioId, Authentication auth) {
    return usuarioRepo.findByEmail(auth.getName())
        .map(u -> u.getId().equals(usuarioId))
        .orElse(false);
  }

  /** Verifica que el alquiler pertenezca al usuario autenticado */
  public boolean isAlquilerOwner(Long alquilerId, Authentication auth) {
    return alquilerRepo.findById(alquilerId)
        .map(a -> a.getUsuario().getEmail().equals(auth.getName()))
        .orElse(false);
  }

  /** Verifica que la cuenta de la plataforma pertenezca al usuario */
  public boolean isCuentaOwner(Long usuarioId, Authentication auth) {
    return cuentaRepo.findByUsuarioId(usuarioId)
        .map(c -> c.getUsuario().getEmail().equals(auth.getName()))
        .orElse(false);
  }

  /** Verifica que el monedero pertenezca al usuario autenticado */
  public boolean isMonederoOwner(Long monederoId, Authentication auth) {
    return monederoRepo.findById(monederoId)
        .map(m -> m.getUsuario().getEmail().equals(auth.getName()))
        .orElse(false);
  }
}
