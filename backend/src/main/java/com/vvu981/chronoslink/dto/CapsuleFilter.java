package com.vvu981.chronoslink.dto;

import com.vvu981.chronoslink.model.CapsuleStatus;
import java.time.LocalDateTime;

/**
 * Objeto de transporte para agrupar los criterios de búsqueda.
 * Esto evita que el Service tenga firmas de métodos gigantes.
 */
public record CapsuleFilter(
        CapsuleStatus status,
        String title,
        LocalDateTime from,
        LocalDateTime to
) {}