package com.akenzhan.taskmanagement.service;

import com.akenzhan.taskmanagement.dto.request.RegisterRequest;
import com.akenzhan.taskmanagement.dto.response.AuthResponse;
import com.akenzhan.taskmanagement.entity.User;
import com.akenzhan.taskmanagement.enums.Role;
import com.akenzhan.taskmanagement.exception.BadRequestException;
import com.akenzhan.taskmanagement.repository.UserRepository;
import com.akenzhan.taskmanagement.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private UserDetailsService userDetailsService;

    @InjectMocks private AuthService authService;

    private RegisterRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("secret123");
        request.setFullName("Alice Smith");
    }

    @Test
    void registerRejectsDuplicateUsername() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Username already exists");
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email already exists");
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerHashesPasswordAndReturnsToken() {
        UserDetails details = mock(UserDetails.class);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(details);
        when(jwtUtil.generateToken(details)).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getPassword()).isEqualTo("hashed");
        assertThat(saved.getValue().getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("alice");
        assertThat(response.getRole()).isEqualTo("ROLE_USER");
    }
}
