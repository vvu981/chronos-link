package com.vvu981.chronoslink.service;

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

    User create(User user);
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsUser(User user);

    @Transactional
    User editUser(User dataIn, UUID id);

    User deleteUser(UUID id);
}