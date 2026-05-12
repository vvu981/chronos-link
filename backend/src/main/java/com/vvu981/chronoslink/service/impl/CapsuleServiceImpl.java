package com.vvu981.chronoslink.service.impl;

import com.vvu981.chronoslink.dto.CapsuleFilter;
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

    private final CapsuleRepository capsuleRepository;
    private final UserService userService;
    private final SecurityService securityService;

    public CapsuleServiceImpl(CapsuleRepository capsuleRepository,
                              UserService userService,
                              SecurityService securityService) {
        this.capsuleRepository = capsuleRepository;
        this.userService = userService;
        this.securityService = securityService;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Capsule> findCapsules(UUID ownerId, CapsuleFilter filter) {
        User owner = userService.getActiveUserOrThrow(ownerId);

        // Construcción dinámica completa: Dueño + Activas + Filtros Opcionales
        Specification<Capsule> spec = Specification.where(CapsuleSpecifications.hasOwner(owner))
                .and(CapsuleSpecifications.isActive())
                .and(CapsuleSpecifications.withStatus(filter.status()))
                .and(CapsuleSpecifications.withTitle(filter.title()))
                .and(CapsuleSpecifications.createdAtBetween(filter.from(), filter.to()));

        return capsuleRepository.findAll(spec);
    }

    @Transactional
    @Override
    public Capsule createCapsule(Capsule capsule, UUID ownerId) {
        User owner = userService.getActiveUserOrThrow(ownerId);

        capsule.setOwner(owner);
        capsule.setCreatedAt(LocalDateTime.now());
        // Importante: No asumas que el status viene bien del exterior
        capsule.setStatus(CapsuleStatus.BLOCKED);
        return capsuleRepository.save(capsule);
    }

    @Transactional
    @Override
    public Capsule deleteCapsule(UUID capsuleId, UUID ownerId) {
        Capsule capsule = getActiveCapsuleOrThrow(capsuleId);
        securityService.validateOwnership(capsule, ownerId);

        capsule.setDeletedAt(LocalDateTime.now());
        return capsuleRepository.save(capsule);
    }

    @Transactional
    @Override
    public Capsule editCapsule(Capsule dataIn, UUID ownerId) {
        Capsule existing = getActiveCapsuleOrThrow(dataIn.getId());
        securityService.validateOwnership(existing, ownerId);

        existing.setContent(dataIn.getContent());
        existing.setTitle(dataIn.getTitle());

        // Lógica de transición de estados protegida
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
            throw new RuntimeException("Condiciones de apertura no cumplidas.");
        }

        capsule.setStatus(CapsuleStatus.OPENED);
        return capsuleRepository.save(capsule);
    }

    @Override
    public boolean isReadyToOpen(Capsule capsule) {
        return LocalDateTime.now().isAfter(capsule.getOpenAt())
                && CapsuleStatus.AVAILABLE.equals(capsule.getStatus());
    }

    private boolean capsuleIsActive(Capsule capsule) {
        return capsule.getDeletedAt() == null;
    }

    private Capsule getActiveCapsuleOrThrow(UUID idCapsule) {
        return capsuleRepository.findById(idCapsule)
                .filter(this::capsuleIsActive) // Filtro elegante en el stream del Optional
                .orElseThrow(() -> new RuntimeException("Cápsula no encontrada o inactiva"));
    }

    private boolean canChangeStatus(Capsule capsule) {
        CapsuleStatus status = capsule.getStatus();
        return status.equals(CapsuleStatus.BLOCKED) || status.equals(CapsuleStatus.AVAILABLE);
    }
}