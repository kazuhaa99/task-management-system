package com.akenzhan.taskmanagement.service;

import com.akenzhan.taskmanagement.dto.request.AkenzhanLoginRequest;
import com.akenzhan.taskmanagement.dto.request.AkenzhanRegisterRequest;
import com.akenzhan.taskmanagement.dto.response.AkenzhanAuthResponse;
import com.akenzhan.taskmanagement.entity.AkenzhanUser;
import com.akenzhan.taskmanagement.enums.Role;
import com.akenzhan.taskmanagement.exception.AkenzhanBadRequestException;
import com.akenzhan.taskmanagement.repository.AkenzhanUserRepository;
import com.akenzhan.taskmanagement.security.AkenzhanJwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AkenzhanAuthService {

    private static final Logger logger = LoggerFactory.getLogger(AkenzhanAuthService.class);

    private final AkenzhanUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AkenzhanJwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AkenzhanAuthService(AkenzhanUserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                AuthenticationManager authenticationManager,
                                AkenzhanJwtUtil jwtUtil,
                                UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    public AkenzhanAuthResponse register(AkenzhanRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AkenzhanBadRequestException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AkenzhanBadRequestException("Email already exists: " + request.getEmail());
        }

        AkenzhanUser user = AkenzhanUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.ROLE_USER)
                .enabled(true)
                .build();

        userRepository.save(user);
        logger.info("New user registered: {}", user.getUsername());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        return new AkenzhanAuthResponse(token, user.getUsername(), user.getRole().name());
    }

    public AkenzhanAuthResponse login(AkenzhanLoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        logger.info("User logged in: {}", request.getUsername());
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        AkenzhanUser user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        return new AkenzhanAuthResponse(token, user.getUsername(), user.getRole().name());
    }
}
