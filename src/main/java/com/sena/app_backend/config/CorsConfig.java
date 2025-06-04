package com.sena.app_backend.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            // Aplica a todos los endpoints de la aplicación
            .addMapping("/**")

            // Permite solo el origen de tu front (Angular en localhost:4200)
            .allowedOrigins("http://localhost:4200")

            // Métodos HTTP permitidos
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

            // Encabezados que el cliente puede enviar (Content-Type, Authorization, etc.)
            .allowedHeaders("*")

            // Si necesitas enviar y recibir cookies o cabeceras de autorización
            .allowCredentials(true)

            // Cuánto tiempo (en segundos) el navegador puede cachear la respuesta del preflight
            .maxAge(3600);
      }
    };
  }
}
