package com.vvu981.chronoslink.dto;

import com.vvu981.chronoslink.model.User;

import java.time.LocalDateTime;
import java.util.UUID;


public record UserCreateDTO(
        UUID id,
        String email,
        String username,
        String password
) {
    // Constructor compacto para convertir una Entidad User a este DTO
    public UserCreateDTO(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword()
        );
    }
}
