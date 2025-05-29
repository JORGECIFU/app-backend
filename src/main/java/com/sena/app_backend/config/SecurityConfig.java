package com.sena.app_backend.config;

import com.sena.app_backend.security.JwtAuthenticationFilter;
import com.sena.app_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity                    // Habilita @PreAuthorize :contentReference[oaicite:0]{index=0}
public class SecurityConfig {

  private final JwtUtil jwtUtil;

  /**
   * Configuración de seguridad para la aplicación.
   * Define las reglas de autorización y el manejo de sesiones.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Proporciona un AuthenticationManager para la autenticación de usuarios.
   *
   * @param authConfig configuración de autenticación
   * @return AuthenticationManager configurado
   * @throws Exception si ocurre un error al obtener el AuthenticationManager
   */
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authConfig
  ) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  /**
   * Configura las reglas de seguridad HTTP para la aplicación.
   * Define qué rutas son accesibles y quién puede acceder a ellas.
   *
   * @param http la configuración de seguridad HTTP
   * @return el SecurityFilterChain configurado
   * @throws Exception si ocurre un error al configurar la seguridad
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // LOGIN y registro de usuarios abiertos
            .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()

        // Sólo ADMINISTRADOR puede listar todos los usuarios
        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasAuthority("ADMINISTRADOR")

        // Sólo ADMINISTRADOR puede promover o administradores
        .requestMatchers(HttpMethod.PUT, "/api/admin/**").hasAuthority("ADMINISTRADOR")

        // USUARIO o ADMINISTRADOR pueden operaciones sobre un usuario específico
        .requestMatchers("/api/usuarios/{id}").hasAnyAuthority("USUARIO","ADMINISTRADOR")

        // El resto requiere cualquier autenticación válida
        .anyRequest().authenticated()
      )
      .sessionManagement(sess ->
        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    )
        .addFilterBefore(
            new JwtAuthenticationFilter(jwtUtil),
            UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
  }
}
