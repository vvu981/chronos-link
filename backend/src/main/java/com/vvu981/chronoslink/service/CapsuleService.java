package com.vvu981.chronoslink.service;

import com.vvu981.chronoslink.model.Capsule;
import com.vvu981.chronoslink.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CapsuleService {

    List<Capsule> getActiveCapsulesByOwnerOrThrow(UUID ownerId);

    List<Capsule> getAllCapsulesByUser(User user);

    Capsule createCapsule(Capsule capsule, UUID userId);

    Capsule deleteCapsule(Capsule capsule, UUID ownerId);
}
