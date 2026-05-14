package com.akenzhan.taskmanagement.controller;

import com.akenzhan.taskmanagement.dto.request.AkenzhanLoginRequest;
import com.akenzhan.taskmanagement.dto.request.AkenzhanRegisterRequest;
import com.akenzhan.taskmanagement.dto.response.AkenzhanAuthResponse;
import com.akenzhan.taskmanagement.service.AkenzhanAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AkenzhanAuthController {

    private final AkenzhanAuthService authService;

    public AkenzhanAuthController(AkenzhanAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AkenzhanAuthResponse> register(@Valid @RequestBody AkenzhanRegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public ResponseEntity<AkenzhanAuthResponse> login(@Valid @RequestBody AkenzhanLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
