package com.sena.app_backend.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Este filtro se encarga de interceptar las peticiones HTTP y verificar si
 * contienen un token JWT válido en la cabecera Authorization.
 * Si el token es válido, se establece la autenticación en el contexto de seguridad.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  /**
   * Método que se ejecuta para filtrar las peticiones HTTP.
   * Verifica la cabecera Authorization y valida el token JWT.
   *
   * @param request  la solicitud HTTP
   * @param response la respuesta HTTP
   * @param chain    la cadena de filtros
   * @throws IOException          si ocurre un error de entrada/salida
   * @throws jakarta.servlet.ServletException si ocurre un error en el servlet
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain)
      throws IOException, jakarta.servlet.ServletException {

    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      if (jwtUtil.validateToken(token)) {
        DecodedJWT jwt = jwtUtil.getDecodedJWT(token);
        String username = jwt.getSubject();
        String role = jwt.getClaim("rol").asString();
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of(new SimpleGrantedAuthority(role))
            );
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    }
    chain.doFilter(request, response);
  }
}

