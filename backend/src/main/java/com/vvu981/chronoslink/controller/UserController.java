package com.vvu981.chronoslink.controller;

import com.vvu981.chronoslink.dto.LoginRequest;
import com.vvu981.chronoslink.dto.UserCreateDTO;
import com.vvu981.chronoslink.dto.UserResponseDTO;
import com.vvu981.chronoslink.dto.UserUpdateDTO;
import com.vvu981.chronoslink.model.User;
import com.vvu981.chronoslink.service.AuthService;
import com.vvu981.chronoslink.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserCreateDTO userDTO) {
        User userToCreate = new User();
        userToCreate.setEmail(userDTO.email());
        userToCreate.setUsername(userDTO.username());
        userToCreate.setPassword(userDTO.password());
        User createdUser = userService.createUser(userToCreate);

        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(createdUser));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginRequest loginData) {
        UserResponseDTO response = authService.login(loginData);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/delete-user")
    public ResponseEntity<UserResponseDTO> deleteUser(@RequestBody UUID userId) {
        User deletedUser = userService.deleteUser(userId);

        return ResponseEntity.ok(new UserResponseDTO(deletedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> editUser(
            @PathVariable UUID id,
            @RequestBody UserUpdateDTO updateDto
    ) {
        User editedUser = userService.editUser(id, updateDto);
        return ResponseEntity.ok(new UserResponseDTO(editedUser));
    }

    @PatchMapping("/{id}/admin-status")
    @PreAuthorize("hasRole('ADMIN')") // Aquí es donde tu UserDetailsService hace la magia
    public ResponseEntity<UserResponseDTO> updateAdminStatus(
            @PathVariable UUID id,
            @RequestBody boolean isAdmin) {

        User updatedUser = userService.updateAdminStatus(id, isAdmin);

        return ResponseEntity.ok(new UserResponseDTO(updatedUser));
    }


}
