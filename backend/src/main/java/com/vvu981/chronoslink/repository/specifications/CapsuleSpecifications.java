package com.vvu981.chronoslink.repository.specifications;

import com.vvu981.chronoslink.model.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class CapsuleSpecifications {

    public static Specification<Capsule> hasOwner(User owner) {
        return (root, query, cb) -> cb.equal(root.get("owner"), owner);
    }

    public static Specification<Capsule> isActive() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Capsule> withStatus(CapsuleStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<Capsule> withTitle(String title) {
        return (root, query, cb) -> (title == null || title.isBlank())
                ? cb.conjunction() : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Capsule> createdAtBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            // Escenario 1: No hay filtros de fecha -> No filtrar nada (conjunción)
            if (from == null && to == null) {
                return cb.conjunction();
            }

            // Escenario 2: Rango completo -> BETWEEN
            if (from != null && to != null) {
                return cb.between(root.get("createdAt"), from, to);
            }

            // Escenario 3: Solo fecha de inicio -> >= from
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            }

            // Escenario 4: Solo fecha de fin -> <= to
            return cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }
}
