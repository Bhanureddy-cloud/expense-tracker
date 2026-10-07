package com.bhanureddy.expense_tracker.controller;

import com.bhanureddy.expense_tracker.dto.AuthResponse;
import com.bhanureddy.expense_tracker.dto.LoginRequest;
import com.bhanureddy.expense_tracker.dto.SignupRequest;
import com.bhanureddy.expense_tracker.model.User;
import com.bhanureddy.expense_tracker.repository.UserRepository;
import com.bhanureddy.expense_tracker.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @InjectMocks AuthController authController;

    @Test
    void signup_hashesPasswordAndReturnsToken() {
        when(userRepository.existsByEmail("a@x.com")).thenReturn(false);
        when(passwordEncoder.encode("Test@1234")).thenReturn("HASHED");
        when(jwtService.generateToken("a@x.com")).thenReturn("token123");

        AuthResponse res = authController.signup(new SignupRequest("Asha", "a@x.com", "Test@1234"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("HASHED", captor.getValue().getPassword()); // never the plain password
        assertEquals("token123", res.token());
    }

    @Test
    void signup_duplicateEmail_returns409() {
        when(userRepository.existsByEmail("a@x.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authController.signup(new SignupRequest("Asha", "a@x.com", "Test@1234")));

        assertEquals(409, ex.getStatusCode().value());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_wrongPassword_returns401() {
        User user = User.builder().name("Asha").email("a@x.com").password("HASHED").build();
        when(userRepository.findByEmail("a@x.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "HASHED")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authController.login(new LoginRequest("a@x.com", "wrong")));

        assertEquals(401, ex.getStatusCode().value());
    }
}