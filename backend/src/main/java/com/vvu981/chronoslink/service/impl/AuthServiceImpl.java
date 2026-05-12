package com.vvu981.chronoslink.service.impl;

import com.vvu981.chronoslink.dto.LoginRequest;
import com.vvu981.chronoslink.dto.UserResponseDTO;
import com.vvu981.chronoslink.model.User;
import com.vvu981.chronoslink.repository.UserRepository;
import com.vvu981.chronoslink.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDTO login(LoginRequest loginData) {
        User user = userRepository.findByUsernameOrEmail(loginData.nameOrEmail(), loginData.nameOrEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(loginData.password(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return new UserResponseDTO(user);
    }
}