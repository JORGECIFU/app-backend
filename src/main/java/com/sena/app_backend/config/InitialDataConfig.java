package com.sena.app_backend.config;

import com.sena.app_backend.model.*;
import com.sena.app_backend.repository.MaquinaRepository;
import com.sena.app_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;

@Configuration
@Profile("production")
@RequiredArgsConstructor
public class InitialDataConfig {

  private final UsuarioRepository usuarioRepository;
  private final MaquinaRepository maquinaRepository;
  private final PasswordEncoder passwordEncoder;

  private void crearMaquinasIniciales() {
    // Si ya existen máquinas, no crear más
    if (maquinaRepository.count() > 0) {
      return;
    }

    // Crear 10 máquinas BAJOS
    for (int i = 1; i <= 10; i++) {
      Maquina m = Maquina.builder()
          .serial("BAJO-" + i)
          .estado(EstadoMaquina.DISPONIBLE)
          .recursos(NivelRecursos.BAJOS)
          .especificaciones("Máquina de recursos bajos #" + i)
          .build();
      maquinaRepository.save(m);
    }

    // Crear 5 máquinas MEDIOS
    for (int i = 1; i <= 5; i++) {
      Maquina m = Maquina.builder()
          .serial("MEDIO-" + i)
          .estado(EstadoMaquina.DISPONIBLE)
          .recursos(NivelRecursos.MEDIOS)
          .especificaciones("Máquina de recursos medios #" + i)
          .build();
      maquinaRepository.save(m);
    }

    // Crear 5 máquinas ALTOS
    for (int i = 1; i <= 5; i++) {
      Maquina m = Maquina.builder()
          .serial("ALTO-" + i)
          .estado(EstadoMaquina.DISPONIBLE)
          .recursos(NivelRecursos.ALTOS)
          .especificaciones("Máquina de recursos altos #" + i)
          .build();
      maquinaRepository.save(m);
    }

    // Crear 5 máquinas SUPERIORES
    for (int i = 1; i <= 5; i++) {
      Maquina m = Maquina.builder()
          .serial("SUP-" + i)
          .estado(EstadoMaquina.DISPONIBLE)
          .recursos(NivelRecursos.SUPERIORES)
          .especificaciones("Máquina de recursos superiores #" + i)
          .build();
      maquinaRepository.save(m);
    }
  }

  private void crearAdministradorInicial() {
    if (usuarioRepository.count() > 0) {
      return; // Ya existe un usuario administrador
    }

    Usuario admin = Usuario.builder()
        .nombre("admin")
        .password(passwordEncoder.encode("adminpass"))
        .nombre("Administrador")
        .apellido("Sistema")
        .email("admin@mineriacripto.com")
        .rol(Rol.ADMINISTRADOR)
        .build();

    usuarioRepository.save(admin);
  }

  @Bean
  CommandLineRunner initDatabase() {
    return args -> {
      crearMaquinasIniciales();
      crearAdministradorInicial();
  };
}
}