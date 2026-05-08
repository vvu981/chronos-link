package com.vvu981.chronoslink.service.impl;

import com.vvu981.chronoslink.model.*;
import com.vvu981.chronoslink.service.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final UserService userService;

    public SecurityServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void validateOwnership(OwnedResource resource, UUID userId) {
        User requester = userService.getActiveUserOrThrow(userId);

        if (!resource.getOwner().getId().equals(requester.getId())) {
            throw new RuntimeException("Acceso denegado: No eres el propietario de este recurso.");
        }
    }

    @Override
    public User getAuthenticatedUser(UUID userId) {
        return userService.getActiveUserOrThrow(userId);
    }
}
