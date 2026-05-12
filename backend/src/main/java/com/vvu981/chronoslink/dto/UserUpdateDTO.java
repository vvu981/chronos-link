package com.vvu981.chronoslink.dto;

// UserUpdateDTO (Record)
public record UserUpdateDTO(
        String email,
        String username,
        String password // Opcional
) {}