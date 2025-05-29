package com.sena.app_backend.service.impl;

import com.sena.app_backend.model.Usuario;
import com.sena.app_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Esta clase implementa la interfaz UserDetailsService de Spring Security
 * para cargar los detalles del usuario desde la base de datos.
 */
@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

  private final UsuarioRepository usuarioRepo;

  /**
   * Carga un usuario por su email.
   *
   * @param email el email del usuario a cargar
   * @return los detalles del usuario
   * @throws UsernameNotFoundException si no se encuentra el usuario
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Usuario user = (Usuario) usuarioRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

    // Convierte el enum Rol a GrantedAuthority
    SimpleGrantedAuthority authority =
        new SimpleGrantedAuthority(user.getRol().name());

    return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        List.of(authority)
    );
  }
}
