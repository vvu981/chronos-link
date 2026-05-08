package com.vvu981.chronoslink.service.impl;

import com.vvu981.chronoslink.model.Capsule;
import com.vvu981.chronoslink.model.CapsuleStatus;
import com.vvu981.chronoslink.model.User;
import com.vvu981.chronoslink.repository.CapsuleRepository;
import com.vvu981.chronoslink.repository.UserRepository;
import com.vvu981.chronoslink.service.CapsuleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CapsuleServiceImpl implements CapsuleService {

    private final CapsuleRepository capsuleRepository;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    public CapsuleServiceImpl(CapsuleRepository capsuleRepository, UserRepository userRepository, UserServiceImpl userService) {
        this.capsuleRepository = capsuleRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public List<Capsule> getActiveCapsulesByOwnerOrThrow(UUID ownerId) {
        User owner = userService.getActiveUserOrThrow(ownerId);
        List<Capsule> allCapsules = owner.getCapsules();
        List<Capsule> activeCapsules = new ArrayList<>();

        for (Capsule c : allCapsules)
            if (capsuleIsActive(c))
                activeCapsules.add(c);
        return activeCapsules;
    }

    @Override
    public List<Capsule> getAllCapsulesByUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("El usuario no existe");
        }
        return capsuleRepository.findByOwner(user);
    }

    @Override
    public Capsule createCapsule(Capsule capsule, UUID ownerId) {
        User owner = userService.getActiveUserOrThrow(ownerId);

        capsule.setOwner(owner);
        capsule.setCreatedAt(LocalDateTime.now());
        return capsuleRepository.save(capsule);
    }


    @Override
    public Capsule deleteCapsule(Capsule capsule, UUID ownerId) {
        User owner = userRepository.findById(ownerId).
                orElseThrow(() -> new RuntimeException("Error: No se encuentra el propietario de la capsula"));

        if (!userService.userIsActive(owner)) {
            throw new RuntimeException("Error: usuario no activo");
        }

        capsule.setDeletedAt(LocalDateTime.now());

        return capsuleRepository.save(capsule);


    }

    private boolean capsuleIsActive(Capsule capsule) {
        return capsule.getDeletedAt() == null;
    }

    private Capsule getActiveCapsuleOrThrow(UUID idCapsule) {
        Capsule capsule = capsuleRepository.findById(idCapsule)
                .orElseThrow(() -> new RuntimeException("Error al obtener capsula: capsula no encontrada con ese id"));

        if (!capsuleIsActive(capsule)) throw new RuntimeException("Error al obtener capsula: Capsula no activa");
        return capsule;
    }

    @Override
    public boolean isReadyToOpen(UUID id) {
        Capsule capsule = getActiveCapsuleOrThrow(id);
        return LocalDateTime.now().isAfter(capsule.getOpenAt()) && capsule.getStatus().equals(CapsuleStatus.AVAILABLE);
    }

    @Override
    public Capsule openCapsule(UUID id) {
        Capsule capsule = getActiveCapsuleOrThrow(id);

        // Si intentan abrirla antes de tiempo, lanzamos error
        if (!isReadyToOpen(id)) {
            throw new RuntimeException("Esta cápsula aún no puede abrirse. Fecha de apertura: " + capsule.getOpenAt());
        }
        capsule.setStatus(CapsuleStatus.OPENED);
        capsuleRepository.save(capsule);
        return capsule;
    }


}