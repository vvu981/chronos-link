package com.vvu981.chronoslink.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor; // Indispensable para JPA

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users") // Evitamos la palabra reservada 'user'
@Getter
@Setter
@NoArgsConstructor // Hibernate necesita un constructor vacío
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Especificamos que es un UUID
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Cambiado de Long a String

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "banned_at")
    private LocalDateTime bannedAt;

    @Column(name = "isAdmin")
    private boolean isAdmin = false;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Capsule> capsules;

    @Column(name = "isDeleted")
    private boolean isDeleted = false;

}