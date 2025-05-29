package com.sena.app_backend.security;

import java.util.Optional;
import java.util.function.BiPredicate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class OwnershipService {

  /**
   * Verifica si un recurso pertenece al usuario autenticado.
   *
   * @param <I>   Tipo del ID de recurso (Long, UUID, etc.)
   * @param id    Identificador del recurso
   * @param auth  Authentication (tiene el username)
   * @param loader Función que, dado un id, carga opcionalmente el objeto propietario (tiene getOwnerUsername())
   * @param predicate  Predicado que compara el ownerUsername con auth.getName()
   */
  public <I, R> boolean isOwner(
      I id,
      Authentication auth,
      java.util.function.Function<I, Optional<R>> loader,
      BiPredicate<R, String> predicate
  ) {
    String username = auth.getName();
    return loader.apply(id)
        .map(ownerEntity -> predicate.test(ownerEntity, username))
        .orElse(false);
  }
}
