package com.vvu981.chronoslink.service;

import com.vvu981.chronoslink.model.OwnedResource;
import com.vvu981.chronoslink.model.User;
import java.util.UUID;

public interface SecurityService {
    void validateOwnership(OwnedResource resource, UUID userId);
    User getAuthenticatedUser(UUID userId);
}