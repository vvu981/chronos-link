package com.vvu981.chronoslink.service.impl;

import com.vvu981.chronoslink.dto.UserUpdateDTO;
import com.vvu981.chronoslink.model.User;
import com.vvu981.chronoslink.repository.UserRepository;
import com.vvu981.chronoslink.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        String email = clean(user.getEmail());
        String username = clean(user.getUsername());

        validateUniqueness(username, email, null);

        user.setEmail(email);
        user.setUsername(username);
        user.setAdmin(false);
        user.setCreatedAt(LocalDateTime.now());

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
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
    public User editUser(UUID id, UserUpdateDTO dto) {
        User existingUser = getActiveUserOrThrow(id);

        if (dto.username() != null) existingUser.setUsername(dto.username());
        if (dto.email() != null) existingUser.setEmail(dto.email());

        if (dto.password() != null && !dto.password().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(dto.password()));
        }
        // Si dto.password() es null, no hacemos nada y se mantiene la que ya tenía

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

    @Override
    public User getActiveUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .filter(user -> userIsActive(user)) // Filtramos aquí directamente para ser elegantes
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado o inactivo"));
    }

    @Override
    @Transactional
    public User updateAdminStatus(UUID userId, Boolean adminStatus) { // Usamos Boolean para detectar el null
        if (adminStatus == null) {
            throw new IllegalArgumentException("El estado de administrador no puede ser nulo.");
        }

        User userToChange = getActiveUserOrThrow(userId);

        if (userToChange.isAdmin() != adminStatus) {
            userToChange.setAdmin(adminStatus);
            return userRepository.save(userToChange);
        }

        return userToChange;
    }

    public boolean userIsActive(User user) {
        return user.getDeletedAt() == null;
    }
}