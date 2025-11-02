package com.foursales.ecommerce.api.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foursales.ecommerce.api.auth.dto.RegistrarUsuarioRequest;
import com.foursales.ecommerce.api.auth.dto.LoginRequest;
import com.foursales.ecommerce.api.auth.dto.LoginResponse;
import com.foursales.ecommerce.application.auth.AutenticacaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AutenticacaoV1Controller {

    private final AutenticacaoService autenticacaoService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(autenticacaoService.login(request));
    }

    @PostMapping("/registrar")
    public ResponseEntity<Void> registrar(@RequestBody @Valid RegistrarUsuarioRequest request) {
        autenticacaoService.registrarUsuario(request);
        return ResponseEntity.ok().build();
    }
}
