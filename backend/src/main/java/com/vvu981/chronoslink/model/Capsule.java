package com.vvu981.chronoslink.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="capsules")
@Getter
@Setter
@NoArgsConstructor
public class Capsule implements OwnedResource{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT") // Para permitir contenido largo
    private String content;

    @Column(name= "createdAt")
    private LocalDateTime createdAt;

    @Column(name= "deletedAt")
    private LocalDateTime deletedAt;

    @Column(name= "openAt")
    private LocalDateTime openAt;

    @ManyToOne(fetch = FetchType.LAZY) // Evitamos traer el usuario si no es necesario
    @JoinColumn(name = "user_id", nullable = false) // Nombramos la FK de forma clara
    private User owner;

    @Enumerated(EnumType.STRING) // ¡Importante! Guardar el texto del enum, no el número
    @Column(nullable = false)
    private CapsuleStatus status;

    @Override
    public User getOwner() {
        return this.owner; // Devuelve el campo User que ya tienes
    }

}