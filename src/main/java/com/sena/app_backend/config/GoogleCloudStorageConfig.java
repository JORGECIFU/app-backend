// src/main/java/com/sena/app_backend/config/GoogleCloudStorageConfig.java

package com.sena.app_backend.config;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;

@Configuration
public class GoogleCloudStorageConfig {

  // Asegúrate de que este archivo esté en src/main/resources
  private static final String CREDENTIALS_FILE_PATH = "service-google-key.json"; // Cambia esto

  @Bean
  public Storage storage() throws IOException {
    // Carga las credenciales desde el archivo JSON
    GoogleCredentials credentials = GoogleCredentials.fromStream(
        getClass().getClassLoader().getResourceAsStream(CREDENTIALS_FILE_PATH));

    // Construye el cliente de Storage usando las credenciales
    return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
  }
}