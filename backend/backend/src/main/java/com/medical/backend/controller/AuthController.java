package com.medical.backend.controller;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.dto.AuthDtos;
import com.medical.backend.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/patient/register")
    public ApiResponse<AuthDtos.RegisterResponse> register(@RequestBody AuthDtos.PatientRegisterRequest req) {
        return authService.registerPatient(req);
    }

    @PostMapping("/login")
    public ApiResponse<AuthDtos.LoginResponse> login(@RequestBody AuthDtos.LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestHeader("X-User-Id") String userId,
                                            @RequestBody AuthDtos.ChangePasswordRequest req) {
        return authService.changePassword(userId, req);
    }
}

