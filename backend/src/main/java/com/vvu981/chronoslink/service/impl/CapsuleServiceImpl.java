package com.vvu981.chronoslink.service.impl;

import com.vvu981.chronoslink.model.Capsule;
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
        // 1. Verificamos si el usuario existe (usando tu lógica)
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("El usuario no existe");
        }

        // 2. Pedimos las cápsulas al repositorio
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


}