package com.vvu981.chronoslink.service.impl;

import com.vvu981.chronoslink.model.Capsule;
import com.vvu981.chronoslink.model.CapsuleStatus;
import com.vvu981.chronoslink.model.User;
import com.vvu981.chronoslink.repository.CapsuleRepository;
import com.vvu981.chronoslink.repository.specifications.CapsuleSpecifications;
import com.vvu981.chronoslink.service.CapsuleService;
import com.vvu981.chronoslink.service.SecurityService;
import com.vvu981.chronoslink.service.UserService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CapsuleServiceImpl implements CapsuleService {

    // En el paquete dto o service
    public record CapsuleFilter(
            CapsuleStatus status,
            String title,
            LocalDateTime from,
            LocalDateTime to
    ) {}

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
    public List<Capsule> findCapsules(UUID ownerId, CapsuleFilter filter) {
        User owner = userService.getActiveUserOrThrow(ownerId);

        Specification<Capsule> spec = Specification.where(CapsuleSpecifications.hasOwner(owner))
                .and(CapsuleSpecifications.isActive())
                .and(CapsuleSpecifications.withStatus(filter.status()))
                .and(CapsuleSpecifications.withTitle(filter.title()));

        return capsuleRepository.findAll(spec);
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