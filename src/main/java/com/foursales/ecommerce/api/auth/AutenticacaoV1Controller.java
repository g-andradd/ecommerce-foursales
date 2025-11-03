package com.foursales.ecommerce.api.auth;

import com.foursales.ecommerce.api.exception.ErroResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Autenticação", description = "Fluxos de autenticação e registro de usuários")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AutenticacaoV1Controller {

    private final AutenticacaoService autenticacaoService;

    @Operation(
            summary = "Realiza login",
            description = "Autentica um usuário válido e gera um token JWT para acesso às rotas protegidas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credenciais válidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(autenticacaoService.login(request));
    }

    @Operation(
            summary = "Registra um novo usuário",
            description = "Cria um usuário com perfil padrão para acessar o e-commerce."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário registrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping("/registrar")
    public ResponseEntity<Void> registrar(@RequestBody @Valid RegistrarUsuarioRequest request) {
        autenticacaoService.registrarUsuario(request);
        return ResponseEntity.ok().build();
    }
}
