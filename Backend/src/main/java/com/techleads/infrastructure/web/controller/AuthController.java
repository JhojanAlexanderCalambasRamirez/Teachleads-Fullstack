package com.techleads.infrastructure.web.controller;

import com.techleads.application.dto.request.LoginRequest;
import com.techleads.application.dto.response.ApiResponse;
import com.techleads.application.dto.response.LoginResponse;
import com.techleads.application.usecase.auth.AuthUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authUseCase.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", response));
    }
}
