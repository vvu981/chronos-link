package com.vvu981.chronoslink.service;

import com.vvu981.chronoslink.model.Capsule;
import com.vvu981.chronoslink.model.User;

import java.util.List;
import java.util.Optional;

public interface CapsuleService {

    List<Capsule> getCapsulesByUser(User user);
}
