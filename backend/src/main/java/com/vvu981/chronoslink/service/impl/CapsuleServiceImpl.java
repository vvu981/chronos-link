package com.vvu981.chronoslink.service.impl;

import com.vvu981.chronoslink.model.Capsule;
import com.vvu981.chronoslink.model.CapsuleStatus;
import com.vvu981.chronoslink.model.User;
import com.vvu981.chronoslink.repository.CapsuleRepository;
import com.vvu981.chronoslink.service.CapsuleService;
import com.vvu981.chronoslink.service.SecurityService;
import com.vvu981.chronoslink.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CapsuleServiceImpl implements CapsuleService {

    private final CapsuleRepository capsuleRepository;
    private final UserService userService; // Interfaz, no Impl
    private final SecurityService securityService; // Interfaz, no Impl

    public CapsuleServiceImpl(CapsuleRepository capsuleRepository,
                              UserService userService,
                              SecurityService securityService) {
        this.capsuleRepository = capsuleRepository;
        this.userService = userService;
        this.securityService = securityService;
    }

    @Override
    public List<Capsule> getActiveCapsulesByOwnerOrThrow(UUID ownerId) {
        User owner = userService.getActiveUserOrThrow(ownerId);

        // Más limpio con Streams (Java Moderno)
        return owner.getCapsules().stream()
                .filter(this::capsuleIsActive)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Capsule> getAllCapsulesByUser(UUID userId) {
        User user = userService.getActiveUserOrThrow(userId);
        return capsuleRepository.findByOwner(user);
    }

    @Transactional
    @Override
    public Capsule createCapsule(Capsule capsule, UUID ownerId) {
        User owner = userService.getActiveUserOrThrow(ownerId);

        capsule.setOwner(owner);
        capsule.setCreatedAt(LocalDateTime.now());
        capsule.setStatus(CapsuleStatus.BLOCKED);
        return capsuleRepository.save(capsule);
    }

    @Transactional
    @Override
    public Capsule deleteCapsule(UUID capsuleId, UUID ownerId) {
        Capsule capsule = getActiveCapsuleOrThrow(capsuleId);

        // El SecurityService ya se encarga de verificar al usuario
        securityService.validateOwnership(capsule, ownerId);

        capsule.setDeletedAt(LocalDateTime.now());
        return capsuleRepository.save(capsule);
    }

    @Transactional
    @Override
    public Capsule editCapsule(Capsule dataIn, UUID ownerId) {
        Capsule existing = getActiveCapsuleOrThrow(dataIn.getId());

        // Bloqueamos edición si no es el dueño
        securityService.validateOwnership(existing, ownerId);

        existing.setContent(dataIn.getContent());
        existing.setTitle(dataIn.getTitle());

        if (canChangeStatus(existing)) {
            existing.setStatus(dataIn.getStatus());
        }

        return capsuleRepository.save(existing);
    }

    @Transactional
    @Override
    public Capsule openCapsule(UUID id, UUID ownerId) {
        Capsule capsule = getActiveCapsuleOrThrow(id);

        securityService.validateOwnership(capsule, ownerId);

        if (!isReadyToOpen(capsule)) {
            throw new RuntimeException("Esta cápsula aún no puede abrirse.");
        }

        capsule.setStatus(CapsuleStatus.OPENED);
        return capsuleRepository.save(capsule);
    }

    @Override
    public boolean isReadyToOpen(Capsule capsule) {
        return LocalDateTime.now().isAfter(capsule.getOpenAt())
                && CapsuleStatus.AVAILABLE.equals(capsule.getStatus());
    }

    // --- MÉTODOS PRIVADOS DE APOYO ---

    private boolean capsuleIsActive(Capsule capsule) {
        return capsule.getDeletedAt() == null;
    }

    private Capsule getActiveCapsuleOrThrow(UUID idCapsule) {
        Capsule capsule = capsuleRepository.findById(idCapsule)
                .orElseThrow(() -> new RuntimeException("Cápsula no encontrada"));

        if (!capsuleIsActive(capsule)) throw new RuntimeException("Cápsula no activa");
        return capsule;
    }

    private boolean canChangeStatus(Capsule capsule) {
        // No repetimos la consulta a la DB, usamos el objeto que ya tenemos
        CapsuleStatus status = capsule.getStatus();
        return status.equals(CapsuleStatus.BLOCKED) || status.equals(CapsuleStatus.AVAILABLE);
    }
}