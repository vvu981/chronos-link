package com.vvu981.chronoslink.service.impl;

import com.vvu981.chronoslink.model.Capsule;
import com.vvu981.chronoslink.model.User;
import com.vvu981.chronoslink.repository.CapsuleRepository;
import com.vvu981.chronoslink.repository.UserRepository;
import com.vvu981.chronoslink.service.CapsuleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CapsuleServiceImpl implements CapsuleService {

    private final CapsuleRepository capsuleRepository;
    private final UserRepository userRepository;

    public CapsuleServiceImpl(CapsuleRepository capsuleRepository, UserRepository userRepository) {
        this.capsuleRepository = capsuleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Capsule> getCapsulesByUser(User user) {
        // 1. Verificamos si el usuario existe (usando tu lógica)
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("El usuario no existe");
        }

        // 2. Pedimos las cápsulas al repositorio
        return capsuleRepository.findByOwner(user);
    }
}