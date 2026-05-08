package com.vvu981.chronoslink.service;

import com.vvu981.chronoslink.model.Capsule;
import com.vvu981.chronoslink.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CapsuleService {


    List<Capsule> getActiveCapsulesByOwnerOrThrow(UUID ownerId);

    @Transactional(readOnly = true)
    List<Capsule> getAllCapsulesByUser(UUID userId);

    @Transactional
    Capsule createCapsule(Capsule capsule, UUID ownerId);

    @Transactional
    Capsule deleteCapsule(UUID capsuleId, UUID ownerId);

    @Transactional
    Capsule editCapsule(Capsule dataIn, UUID ownerId);

    @Transactional
    Capsule openCapsule(UUID id, UUID ownerId);

    boolean isReadyToOpen(Capsule capsule);
}
