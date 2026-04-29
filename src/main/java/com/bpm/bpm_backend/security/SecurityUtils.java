package com.bpm.bpm_backend.security;

import com.bpm.bpm_backend.model.User;
import com.bpm.bpm_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Componente utilitario para extraer datos del usuario autenticado
 * directamente desde el {@link SecurityContextHolder}, sin necesidad
 * de pasar el requesterId en el cuerpo de la petición.
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    /** Devuelve el email (username) del usuario actualmente autenticado. */
    public String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails ud) {
            return ud.getUsername();
        }
        return principal.toString();
    }

    /** Devuelve la entidad {@link User} completa del usuario autenticado. */
    public User getCurrentUser() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in DB: " + email));
    }

    /** Devuelve el ID de MongoDB del usuario autenticado. */
    public String getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
