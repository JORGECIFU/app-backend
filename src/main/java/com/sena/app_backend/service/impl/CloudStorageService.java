// src/main/java/com/sena/app_backend/service/impl/CloudStorageService.java

package com.sena.app_backend.service.impl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
public class CloudStorageService {

  @Value("${gcp.bucket.name}") // Define esto en application.properties o application.yml
  private String bucketName;

  private final Storage storage;

  // Inyecta el bean de Storage que configuramos en GoogleCloudStorageConfig
  public CloudStorageService(Storage storage) {
    this.storage = storage;
  }

  private static final String PROFILE_PHOTOS_FOLDER = "fotos-perfil/";

  /**
   * Sube un archivo a la carpeta 'fotos-perfil' del bucket.
   * @param file El archivo MultipartFile recibido del frontend.
   * @param fileName El nombre deseado para el archivo dentro de la carpeta.
   * @return La URL pública del objeto (si se hace público) o una URL firmada.
   * @throws IOException Si ocurre un error al leer el archivo.
   */
  public String uploadProfilePhoto(MultipartFile file, String fileName) throws IOException {
    // El nombre del blob incluirá el prefijo de la carpeta
    String blobName = PROFILE_PHOTOS_FOLDER + fileName;
    BlobId blobId = BlobId.of(bucketName, blobName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

    // Sube el archivo
    storage.create(blobInfo, file.getBytes());

    // Opcional: Si quieres que la URL sea directamente accesible sin autenticación
    // blob.makePublic(); // ¡Cuidado! Esto hace el objeto público para cualquiera.
    // return blob.getMediaLink(); // URL pública

    // Recomendado: Generar una URL firmada para acceso seguro y temporal
    // Esto permite que el frontend acceda al archivo por un tiempo limitado
    URL signedUrl = storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
    return signedUrl.toString();
  }

  /**
   * Descarga un archivo de la carpeta 'fotos-perfil'.
   * @param fileName El nombre del archivo dentro de la carpeta 'fotos-perfil'.
   * @return Los bytes del archivo.
   */
  public byte[] downloadProfilePhoto(String fileName) {
    String blobName = PROFILE_PHOTOS_FOLDER + fileName;
    BlobId blobId = BlobId.of(bucketName, blobName);
    Blob blob = storage.get(blobId);
    if (blob == null) {
      return null; // O lanzar una excepción
    }
    return blob.getContent();
  }

  /**
   * Elimina un archivo de la carpeta 'fotos-perfil'.
   * @param fileName El nombre del archivo a eliminar.
   * @return true si se eliminó, false en caso contrario.
   */
  public boolean deleteProfilePhoto(String fileName) {
    String blobName = PROFILE_PHOTOS_FOLDER + fileName;
    BlobId blobId = BlobId.of(bucketName, blobName);
    return storage.delete(blobId);
  }
}