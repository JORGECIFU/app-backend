// src/main/java/com/sena/app_backend/controller/ProfilePhotoController.java

package com.sena.app_backend.controller;

import com.sena.app_backend.model.Usuario;
import com.sena.app_backend.repository.UsuarioRepository;
import com.sena.app_backend.service.impl.CloudStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/profile-photos")
public class ProfilePhotoController {

  private final CloudStorageService cloudStorageService;
  private final UsuarioRepository usuarioRepository;

  public ProfilePhotoController(CloudStorageService cloudStorageService, UsuarioRepository usuarioRepository) {
    this.cloudStorageService = cloudStorageService;
    this.usuarioRepository = usuarioRepository;
  }

  @PostMapping("/upload")
  public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file,
                                          @RequestParam("fileName") String fileName) {
    try {
      // Obtener el usuario autenticado
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      String email = auth.getName();
      Usuario usuario = usuarioRepository.findByEmail(email)
          .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

      // Si el usuario ya tiene una foto, eliminarla
      if (usuario.getProfilePhotoName() != null) {
        cloudStorageService.deleteProfilePhoto(usuario.getProfilePhotoName());
      }

      // Subir la nueva foto
      String url = cloudStorageService.uploadProfilePhoto(file, fileName);

      // Actualizar el nombre de la foto en el usuario
      usuario.setProfilePhotoName(fileName);
      usuarioRepository.save(usuario);

      return ResponseEntity.ok("Foto de perfil subida exitosamente. URL: " + url);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error al subir la foto: " + e.getMessage());
    }
  }

  @DeleteMapping("/delete/{fileName}")
  public ResponseEntity<String> deletePhoto(@PathVariable String fileName) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    Usuario usuario = usuarioRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // Verificar que la foto pertenezca al usuario
    if (!fileName.equals(usuario.getProfilePhotoName())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body("No tienes permiso para eliminar esta foto");
    }

    if (cloudStorageService.deleteProfilePhoto(fileName)) {
      usuario.setProfilePhotoName(null);
      usuarioRepository.save(usuario);
      return ResponseEntity.ok("Foto de perfil eliminada exitosamente.");
    }
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Error al eliminar la foto.");
  }

  @GetMapping("/download/{fileName}")
  public ResponseEntity<byte[]> downloadPhoto(@PathVariable String fileName) {
    byte[] data = cloudStorageService.downloadProfilePhoto(fileName);
    if (data != null) {
      return ResponseEntity.ok().body(data);
    }
    return ResponseEntity.notFound().build();
  }
}
