/*

package com.vvu981.chronoslink.service.impl;

import com.vvu981.chronoslink.model.User;
import com.vvu981.chronoslink.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User existingUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("original@test.com");
        existingUser.setUsername("originaluser");
        existingUser.setPassword("oldPassword");
        existingUser.setDeletedAt(null); // Usuario activo por defecto
    }

    // --- CREATE ---
    @Test
    @DisplayName("Create: Éxito con limpieza de datos")
    void create_Success() {
        User input = new User();
        input.setEmail(" NEW@test.com ");
        input.setUsername(" NewUser ");

        when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.createUser(input);

        assertEquals("new@test.com", result.getEmail());
        assertEquals("newuser", result.getUsername());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    @DisplayName("Create: Error por email duplicado")
    void create_Fail_EmailExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));
        assertThrows(RuntimeException.class, () -> userService.createUser(existingUser));
    }

    // --- FIND METHODS ---
    @Test
    @DisplayName("FindByEmail: Error si es vacío y éxito con clean")
    void findByEmail_AllBranches() {
        assertThrows(RuntimeException.class, () -> userService.findByEmail(""));
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(existingUser));
        assertTrue(userService.findByEmail(" TEST@test.com ").isPresent());
    }

    @Test
    @DisplayName("FindByUsername: Error si es vacío y éxito con clean")
    void findByUsername_AllBranches() {
        assertThrows(RuntimeException.class, () -> userService.findByUsername(""));
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(existingUser));
        assertTrue(userService.findByUsername(" USER ").isPresent());
    }

    // --- EDIT USER ---
    @Test
    @DisplayName("EditUser: Éxito cambiando password")
    void editUser_WithPassword() {
        User dataIn = new User();
        dataIn.setEmail("new@test.com");
        dataIn.setUsername("newuser");
        dataIn.setPassword("newPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(existingUser);

        User result = userService.editUser(dataIn, userId);
        assertEquals("newPass", result.getPassword());
    }

    @Test
    @DisplayName("EditUser: Password null o vacío no deben cambiar el existente")
    void editUser_PasswordBranches() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenReturn(existingUser);

        User dataNull = new User();
        dataNull.setPassword(null);
        userService.editUser(dataNull, userId);
        assertEquals("oldPassword", existingUser.getPassword());

        User dataEmpty = new User();
        dataEmpty.setPassword("");
        userService.editUser(dataEmpty, userId);
        assertEquals("oldPassword", existingUser.getPassword());
    }

    // --- DELETE & GET ACTIVE ---
    @Test
    @DisplayName("DeleteUser: Éxito al asignar fecha de borrado")
    void deleteUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenReturn(existingUser);

        User result = userService.deleteUser(userId);
        assertNotNull(result.getDeletedAt());
    }

    @Test
    @DisplayName("getActiveUserOrThrow: Error si no existe o tiene fecha de borrado")
    void getActiveUserOrThrow_Fail_Branches() {
        // Caso: No existe en la DB
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getActiveUserOrThrow(userId));

        // Caso: Existe pero tiene fecha de borrado
        existingUser.setDeletedAt(LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        assertThrows(RuntimeException.class, () -> userService.getActiveUserOrThrow(userId));
    }

    // --- ACTIVATE USER ---
    @Test
    @DisplayName("ActivateUser: Éxito y Errores de rama (deletedAt)")
    void activateUser_AllBranches() {
        // Error: No existe
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.activateUser(userId));

        // Error: Ya está activado (deletedAt es null)
        existingUser.setDeletedAt(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        assertThrows(RuntimeException.class, () -> userService.activateUser(userId));

        // Éxito: Tenía fecha y ahora es null
        existingUser.setDeletedAt(LocalDateTime.now());
        when(userRepository.save(any())).thenReturn(existingUser);
        User result = userService.activateUser(userId);
        assertNull(result.getDeletedAt());
    }

    // --- UNIQUENESS BRANCHES (El "Amarillo" de IntelliJ) ---
    @Test
    @DisplayName("Uniqueness: Editando con mismos datos propios (No lanza error)")
    void validateUniqueness_OwnData_Branch() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenReturn(existingUser);

        assertDoesNotThrow(() -> userService.editUser(existingUser, userId));
    }

    @Test
    @DisplayName("Uniqueness: Conflicto de Username con OTRO usuario")
    void validateUniqueness_UsernameConflict_Branch() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(otherUser));

        assertThrows(RuntimeException.class, () -> userService.editUser(existingUser, userId));
    }

    // --- LISTS & CLEAN ---
    @Test
    @DisplayName("Lists: Cobertura de métodos de listado")
    void lists_Coverage() {
        when(userRepository.findActiveUsers()).thenReturn(Collections.emptyList());
        when(userRepository.findDeletedUsers()).thenReturn(Collections.emptyList());
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        assertNotNull(userService.getActiveUsers());
        assertNotNull(userService.getDeletedUsers());
        assertNotNull(userService.getAllUsers());
    }

    @Test
    @DisplayName("Clean: Rama de valor nulo")
    void clean_NullBranch() {
        User userNull = new User();
        userNull.setEmail(null);
        userNull.setUsername(null);

        when(userRepository.findByEmail("")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("")).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(userNull);

        assertNotNull(userService.createUser(userNull));
    }

    // 1. Forzar 'id == null' específicamente para el bloque de USERNAME
    // (Tenemos que hacer que el email pase limpio para llegar al username)
    @Test
    @DisplayName("Uniqueness: Create - Error cuando solo el Username está duplicado")
    void validateUniqueness_Create_UsernameDuplicate_Branch() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty()); // Email libre
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(existingUser)); // Username ocupado

        User newUser = new User();
        newUser.setEmail("new@test.com");
        newUser.setUsername("originaluser");

        assertThrows(RuntimeException.class, () -> userService.createUser(newUser));
    }

    // 2. Forzar '!u.getId().equals(id)' para el bloque de EMAIL en edición
    @Test
    @DisplayName("Uniqueness: Edit - Error cuando el Email lo tiene OTRO usuario")
    void validateUniqueness_Edit_EmailConflictOther_Branch() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID()); // ID diferente al nuestro

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(otherUser));

        assertThrows(RuntimeException.class, () -> userService.editUser(existingUser, userId));
    }

    // 3. Forzar que ambos bloques (Email y Username) vean que el ID es el MISMO
    // (Esto cubre la parte 'false' de los dos condicionales)
    @Test
    @DisplayName("Uniqueness: Edit - Éxito cuando el usuario mantiene sus propios datos")
    void validateUniqueness_Edit_SameUserEverything_Branch() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        // El repo devuelve al mismo usuario (mismo ID) para ambos campos
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenReturn(existingUser);

        assertDoesNotThrow(() -> userService.editUser(existingUser, userId));
    }
}

*/