package com.sena.app_backend.service.impl;

import com.sena.app_backend.dto.request.NuevoUsuarioRequest;
import com.sena.app_backend.dto.response.UsuarioResponse;
import com.sena.app_backend.model.PlataformaFondosCuenta;
import com.sena.app_backend.model.Rol;
import com.sena.app_backend.model.Usuario;
import com.sena.app_backend.repository.*;
import com.sena.app_backend.service.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Esta clase se encarga de la lógica de negocio relacionada con los usuarios.
 * Implementa la interfaz UsuarioService.
 */
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

  private final UsuarioRepository repo;
  /**
   * Repositorio para manejar las cuentas de fondos de los usuarios.
   * Se utiliza para operaciones relacionadas con las cuentas de fondos.
   */
  private final PlataformaFondosCuentaRepository cuentaRepo;
  private final RefreshTokenRepository refreshTokenRepo;
  private final MonederoRepository monederoRepo;
  private final PlataformaTransaccionCuentaRepository transRepo;
  private final AlquilerRepository alquilerRepo;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  /**
   * Convierte una entidad Usuario a un DTO de respuesta UsuarioResponse.
   *
   * @param u la entidad Usuario
   * @return el DTO de respuesta UsuarioResponse
   */
  private UsuarioResponse mapToDto(Usuario u) {
    return UsuarioResponse.builder()
        .id(u.getId())
        .nombre(u.getNombre())
        .apellido(u.getApellido())
        .email(u.getEmail())
        .rol(u.getRol())
        .build();
  }

  /**
   * Convierte un DTO de solicitud NuevoUsuarioRequest a una entidad Usuario.
   *
   * @param req el DTO de solicitud NuevoUsuarioRequest
   * @return la entidad Usuario
   */
  private Usuario mapToEntity(NuevoUsuarioRequest req) {
    return Usuario.builder()
        .nombre(req.getNombre())
        .apellido(req.getApellido())
        .email(req.getEmail())
        .rol(req.getRol())
        .build();
  }

  /**
   * Crea un nuevo usuario.
   * <p>
   * 1) Crea el usuario con la contraseña encriptada.
   * 2) Crea automáticamente su cuenta de plataforma con balance 0.
   *
   * @param req la solicitud de creación de usuario
   * @return la respuesta del usuario creado
   */
  @Override
  @Transactional
  public UsuarioResponse crearUsuario(NuevoUsuarioRequest req) {
    // 1) Mapeo y encriptado de contraseña
    Usuario entity = mapToEntity(req);
    entity.setPassword(passwordEncoder.encode(req.getPassword()));
    // 2) Fijar siempre rol USUARIO
    entity.setRol(Rol.USUARIO);
    // 3) Persistir usuario
    Usuario saved = repo.save(entity);
    // 4) Crear la cuenta de fondos para usuarios
    PlataformaFondosCuenta cuenta = PlataformaFondosCuenta.builder()
        .usuario(saved)
        .balance(BigDecimal.ZERO)
        .build();
    cuentaRepo.save(cuenta);
    // 5) Devolver DTO
    return mapToDto(saved);
  }

  /**
   * Registra un usuario administrador. Sólo invocable por ADMINISTRADOR.
   * No crea cuenta de fondos.
   */
  @Transactional
  public UsuarioResponse crearAdministrador(NuevoUsuarioRequest req, Authentication auth) {
    // 1) Validar rol caller
    boolean isAdminCaller = auth != null && auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ADMINISTRADOR"));
    if (!isAdminCaller) {
      throw new AccessDeniedException("Solo ADMINISTRADOR puede crear otro ADMINISTRADOR");
    }

    // 2) Mapear y encriptar
    Usuario entity = Usuario.builder()
        .nombre(req.getNombre())
        .apellido(req.getApellido())
        .email(req.getEmail())
        .rol(Rol.ADMINISTRADOR)
        .password(passwordEncoder.encode(req.getPassword()))
        .build();

    // 3) Guardar y devolver
    Usuario saved = repo.save(entity);
    return mapToDto(saved);
  }

  /**
   * Lista todos los usuarios.
   *
   * @return una lista de DTOs de respuesta UsuarioResponse
   */
  @Override
  public List<UsuarioResponse> listarUsuarios() {
    return repo.findAll().stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  /**
   * Obtiene un usuario por su ID.
   *
   * @param id el ID del usuario a obtener
   * @return el DTO de respuesta UsuarioResponse
   */
  @Override
  public UsuarioResponse obtenerUsuario(Long id) {
    Usuario u = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
    return mapToDto(u);
  }

  /**
   * Actualiza un usuario existente.
   * <p>
   * Valida que el usuario exista antes de actualizarlo.
   * Si se cambia el rol a ADMINISTRADOR, elimina su cuenta de fondos.
   *
   * @param id  el ID del usuario a actualizar
   * @param req la solicitud de actualización de usuario
   * @return la respuesta del usuario actualizado
   */
  @Override
  @Transactional
  public UsuarioResponse actualizarUsuario(Long id, NuevoUsuarioRequest req) {
    Usuario u = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
    u.setNombre(req.getNombre());
    u.setApellido(req.getApellido());
    u.setEmail(req.getEmail());
    if (req.getPassword() != null && !req.getPassword().isBlank()) {
      u.setPassword(passwordEncoder.encode(req.getPassword()));
    }
    // Si en la actualización cambiara el rol a ADMINISTRADOR, también eliminaríamos la cuenta:
    if (req.getRol() == Rol.ADMINISTRADOR && u.getRol() != Rol.ADMINISTRADOR) {
      // promocionando a admin
      cuentaRepo.findByUsuarioId(id).ifPresent(cuentaRepo::delete);
      u.setRol(Rol.ADMINISTRADOR);
    }
    Usuario updated = repo.save(u);
    return mapToDto(updated);
  }

  /**
   * Elimina un usuario por su ID.
   * <p>
   * Valida que el usuario exista antes de eliminarlo.
   *
   * @param id el ID del usuario a eliminar
   */
  @Override
  public void eliminarUsuario(Long id) {
    Usuario u = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
    // Si el usuario tiene una cuenta de fondos, la eliminamos
    cuentaRepo.findByUsuarioId(id).ifPresent(cuentaRepo::delete);
    // Si el usuario tiene un monedero, lo eliminamos
    monederoRepo.deleteAll(monederoRepo.findByUsuarioId(id));
    // Si el usuario tiene tokens de refresco, los eliminamos
    refreshTokenRepo.deleteAll(refreshTokenRepo.findByUserId(id));
    // Si el usuario tiene transacciones, las eliminamos
    transRepo.deleteAll(transRepo.findByAccountId(id));
    // Si el usuario tiene alquileres, los eliminamos
    alquilerRepo.deleteAll(alquilerRepo.findByUsuarioId(id));

    // Eliminar el usuario
    repo.deleteById(id);
  }

  /**
   * Promueve un usuario a administrador.
   * <p>
   * Valida que el usuario exista antes de promoverlo.
   * Si ya es administrador, no hace nada.
   *
   * @param id el ID del usuario a promover
   * @return la respuesta del usuario promovido
   */
  @Override
  @Transactional
  public UsuarioResponse promoverAAdministrador(Long id) {
    Usuario u = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

    // 1) Si ya es ADMIN no hacemos nada
    if (u.getRol() != Rol.ADMINISTRADOR) {
      // 2) Cambiar rol
      u.setRol(Rol.ADMINISTRADOR);
      repo.save(u);
      // 3) Eliminar su cuenta de fondos (ya no la necesita)
      cuentaRepo.findByUsuarioId(id).ifPresent(cuentaRepo::delete);
    }

    return mapToDto(u);
  }

  /**
   * Promueve un usuario a cliente.
   * <p>
   * Valida que el usuario exista antes de promoverlo.
   *
   * @param email el ID del usuario a promover
   * @return la respuesta del usuario promovido
   */
  @Override
  public UsuarioResponse obtenerPorEmail(String email) {
    Usuario u = (Usuario) repo.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
    return mapToDto(u);
  }
}
