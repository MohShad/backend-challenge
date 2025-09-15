package com.simplesdental.product.service;

import com.simplesdental.product.model.User;
import com.simplesdental.product.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("$2a$12$encoded.password.hash");
        testUser.setRole("user");
    }

    @Test
    void shouldFindUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(999L);

        assertFalse(result.isPresent());
        verify(userRepository).findById(999L);
    }

    @Test
    void shouldFindUserByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void shouldSaveUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.save(testUser);

        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$12$encoded.password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser("New User", "newuser@example.com", "password123", "user");

        assertNotNull(result);
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("User", "existing@example.com", "password", "user");
        });

        assertEquals("Email já existe: existing@example.com", exception.getMessage());
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdatePassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("$2a$12$encoded.new.password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updatePassword(1L, "newPassword");

        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPasswordForNonExistentUser() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updatePassword(999L, "newPassword");
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldCheckIfEmailExists() {
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        assertTrue(userService.existsByEmail("existing@example.com"));
        assertFalse(userService.existsByEmail("nonexistent@example.com"));

        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }

    @Test
    void shouldEncodePassword() {
        when(passwordEncoder.encode("rawPassword")).thenReturn("$2a$12$encoded");

        String result = userService.encodePassword("rawPassword");

        assertEquals("$2a$12$encoded", result);
        verify(passwordEncoder).encode("rawPassword");
    }

    @Test
    void shouldValidatePassword() {
        when(passwordEncoder.matches("rawPassword", "$2a$12$encoded")).thenReturn(true);
        when(passwordEncoder.matches("wrongPassword", "$2a$12$encoded")).thenReturn(false);

        assertTrue(userService.isPasswordValid("rawPassword", "$2a$12$encoded"));
        assertFalse(userService.isPasswordValid("wrongPassword", "$2a$12$encoded"));

        verify(passwordEncoder).matches("rawPassword", "$2a$12$encoded");
        verify(passwordEncoder).matches("wrongPassword", "$2a$12$encoded");
    }

    @Test
    void shouldFindAllUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        List<User> users = Arrays.asList(testUser, user2);

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();

        assertEquals(2, result.size());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());
        assertEquals(user2.getEmail(), result.get(1).getEmail());
        verify(userRepository).findAll();
    }

    @Test
    void shouldDeleteUserById() {
        userService.deleteById(1L);

        verify(userRepository).deleteById(1L);
    }
}