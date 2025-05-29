package com.sena.app_backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Esta clase se encarga de la generación y validación de tokens JWT.
 * Utiliza la biblioteca Auth0 para manejar los tokens.
 */
@Component
public class JwtUtil {

  /**
   * Clave secreta utilizada para firmar los tokens JWT.
   * Debe ser mantenida en secreto y no debe ser expuesta públicamente.
   */
  @Value("${jwt.secret}")
  private String secret;                  // tu clave secreta

  /**
   * Tiempo de expiración del token en milisegundos.
   * Por ejemplo, 3600000 ms equivale a 1 hora.
   */
  @Value("${jwt.expirationMs}")
  private long expirationMs;              // p.ej. 3600000

  /**
   * Obtiene el algoritmo de firma utilizado para crear y verificar los tokens JWT.
   * En este caso, se utiliza HMAC con SHA-256 (HS256).
   *
   * @return el algoritmo de firma
   */
  private Algorithm getAlgorithm() {
    return Algorithm.HMAC256(secret);  // HS256 con tu secreto :contentReference[oaicite:6]{index=6}
  }

  /**
   * Genera un token JWT para un usuario específico con su rol.
   *
   * @param username el nombre de usuario para el cual se genera el token
   * @param role     el rol del usuario (p.ej. "ADMIN", "USER")
   * @return el token JWT generado
   */
  public String generateToken(String username, String role) {
    Date now = new Date();
    return JWT.create()
        .withSubject(username)
        .withClaim("rol", role)
        .withIssuedAt(now)
        .withExpiresAt(new Date(now.getTime() + expirationMs))
        .sign(getAlgorithm());          // firma el token :contentReference[oaicite:7]{index=7}
  }

  /**
   * Valida un token JWT verificando su firma y fecha de expiración.
   *
   * @param token el token JWT a validar
   * @return true si el token es válido, false en caso contrario
   */
  public boolean validateToken(String token) {
    try {
      getVerifier().verify(token);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  /**
   * Decodifica un token JWT y devuelve su contenido.
   *
   * @param token el token JWT a decodificar
   * @return un objeto DecodedJWT que contiene los datos del token
   */
  public DecodedJWT getDecodedJWT(String token) {
    return getVerifier().verify(token);
  }

  /**
   * Crea un verificador de tokens JWT utilizando el algoritmo de firma configurado.
   * Este verificador se utiliza para validar la firma y la integridad del token.
   *
   * @return un JWTVerifier configurado
   */
  private JWTVerifier getVerifier() {
    return JWT.require(getAlgorithm()).build();  // crea el verificador :contentReference[oaicite:8]{index=8}
  }
}
