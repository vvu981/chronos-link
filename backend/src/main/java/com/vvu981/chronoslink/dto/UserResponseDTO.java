package com.vvu981.chronoslink.dto;

import com.vvu981.chronoslink.model.User;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Este objeto es el que se enviará al Frontend.
 * NO incluye la contraseña ni datos sensibles de administración interna.
 */

public record UserResponseDTO(
        UUID id,
        String email,
        String username,
        LocalDateTime createdAt
) {
    // Constructor compacto para convertir una Entidad User a este DTO
    public UserResponseDTO(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                LocalDateTime.now()
        );
    }
}