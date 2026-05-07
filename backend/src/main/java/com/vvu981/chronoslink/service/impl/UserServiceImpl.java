package com.vvu981.chronoslink.service.impl;

import com.vvu981.chronoslink.model.User;
import com.vvu981.chronoslink.repository.UserRepository;
import com.vvu981.chronoslink.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        String email = clean(user.getEmail());
        String username = clean(user.getUsername());

        validateUniqueness(username, email, null);

        user.setEmail(email);
        user.setUsername(username);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email.isEmpty()) throw new RuntimeException("Error: email vacio");
        String emailLower = email.toLowerCase();
        return userRepository.findByEmail(emailLower);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username.isEmpty()) throw new RuntimeException("Error: username vacio");
        String usernameLower = username.toLowerCase();
        return userRepository.findByUsername(usernameLower);
    }

    @Override
    public boolean existsUser(User user) {
        Optional<User> userEmail = userRepository.findByEmail(user.getEmail());
        Optional<User> userUsername = userRepository.findByUsername(user.getUsername());
        return userEmail.isPresent() || userUsername.isPresent();
    }

    @Transactional
    @Override
    public User editUser(User dataIn, UUID id) {

        User existingUser = findOrThrow(id);

        String newUsername = clean(dataIn.getUsername());
        String newEmail = clean(dataIn.getEmail());

        validateUniqueness(newUsername, newEmail, id);

        existingUser.setUsername(newUsername);
        existingUser.setEmail(newEmail);
        if (dataIn.getPassword() != null && !dataIn.getPassword().isEmpty()) {
            existingUser.setPassword(dataIn.getPassword());
        }

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public User deleteUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Error al eliminar: no se encuentra el usuario con ese id"));

        if (user.isDeleted()) throw new RuntimeException("Error: usuario ya eliminado");
        user.setDeleted(true);
        return userRepository.save(user);
    }

    private String clean(String value) {
        return value == null ? "" : value.toLowerCase().trim();
    }

    private User findOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private void validateUniqueness(String username, String email, UUID id) {
        userRepository.findByEmail(email).ifPresent(u -> {
            if (id == null || !u.getId().equals(id)) {
                throw new RuntimeException("Error: El email ya está en uso.");
            }
        });

        userRepository.findByUsername(username).ifPresent(u -> {
            if (id == null || !u.getId().equals(id)) {
                throw new RuntimeException("Error: El username ya está en uso.");
            }
        });
    }
}