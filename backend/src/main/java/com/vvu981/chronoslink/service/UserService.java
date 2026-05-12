package com.vvu981.chronoslink.service;

import com.vvu981.chronoslink.dto.UserUpdateDTO;
import com.vvu981.chronoslink.model.Capsule;
import com.vvu981.chronoslink.model.User;
import com.vvu981.chronoslink.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface UserService {

    User createUser(User user);
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Transactional
    User editUser(UUID id, UserUpdateDTO dto);

    User deleteUser(UUID id);

    List<User> getActiveUsers();

    List<User> getDeletedUsers();

    List<User> getAllUsers();

    User activateUser(UUID id);

    User getActiveUserOrThrow(UUID id);

    User updateAdminStatus(UUID userId, Boolean adminStatus);
}