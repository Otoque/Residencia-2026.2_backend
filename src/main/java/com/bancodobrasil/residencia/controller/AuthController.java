package com.bancodobrasil.residencia.controller;

import com.bancodobrasil.residencia.dto.request.LoginRequestDTO;
import com.bancodobrasil.residencia.dto.response.LoginResponseDTO;
import com.bancodobrasil.residencia.dto.response.RegisterResponseDTO;
import com.bancodobrasil.residencia.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request.username(), request.password()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request,
                                                  HttpServletRequest http) {
        return ResponseEntity.ok(
                authService.login(request.username(), request.password(), http.getRemoteAddr()));
    }
}
