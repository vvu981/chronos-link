package com.vvu981.chronoslink.service.impl;

import com.vvu981.chronoslink.model.User;
import com.vvu981.chronoslink.repository.UserRepository;
import com.vvu981.chronoslink.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
        String emailLower = clean(email);
        return userRepository.findByEmail(emailLower);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        if (username.isEmpty()) throw new RuntimeException("Error: username vacio");
        String usernameLower = clean(username);
        return userRepository.findByUsername(usernameLower);
    }

    @Transactional
    @Override
    public User editUser(User dataIn, UUID id) {

        User existingUser = getActiveUserOrThrow(id);

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
        User user = getActiveUserOrThrow(id);

        user.setDeletedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public List<User> getActiveUsers() {
        return userRepository.findActiveUsers();
    }

    @Override
    public List<User> getDeletedUsers() {
        return userRepository.findDeletedUsers();
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private String clean(String value) {
        return value == null ? "" : value.toLowerCase().trim();
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

    @Override
    public User activateUser(UUID id) {
        User deletedUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error al activar usuario: no existe el usuario con ese id"));

        if (userIsActive(deletedUser)) throw new RuntimeException("Error al activar usuario: el usuario ya esta activado");

        deletedUser.setDeletedAt(null);

        return userRepository.save(deletedUser);

    }

    // En UserServiceImpl.java
    @Override
    public User getActiveUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .filter(user -> userIsActive(user)) // Filtramos aquí directamente para ser elegantes
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o inactivo"));
    }

    public boolean userIsActive(User user) {
        return user.getDeletedAt() == null;
    }
}