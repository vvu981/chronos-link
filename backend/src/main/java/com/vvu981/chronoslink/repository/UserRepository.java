package com.vvu981.chronoslink.repository;

import com.vvu981.chronoslink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Aquí puedes añadir métodos de búsqueda personalizados más adelante, como:
    // Optional<User> findByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}