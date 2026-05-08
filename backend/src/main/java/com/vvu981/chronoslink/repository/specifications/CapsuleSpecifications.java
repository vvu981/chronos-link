package com.vvu981.chronoslink.repository.specifications;

import com.vvu981.chronoslink.model.*;
import org.springframework.data.jpa.domain.Specification;

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
}
