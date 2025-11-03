package com.foursales.ecommerce.application.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private static final String BASE64_SECRET = Base64.getEncoder()
            .encodeToString("super-secret-signing-key-which-is-long".getBytes(StandardCharsets.UTF_8));
    private static final String ALTERNATE_SECRET = Base64.getEncoder()
            .encodeToString("alternate-secret-signing-key-1234".getBytes(StandardCharsets.UTF_8));

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", BASE64_SECRET);
        ReflectionTestUtils.setField(tokenService, "expiration", 60_000L);
        tokenService.initKey();
    }

    @Test
    void gerarTokenDeveAssinarETrazerUsuario() {
        String token = tokenService.gerarToken("user@example.com", Map.of("claim", "value"));

        assertThat(token).isNotBlank();
        assertThat(tokenService.validarETrazerUsuario(token)).isEqualTo("user@example.com");
    }

    @Test
    void validarETrazerUsuarioDeveRetornarNullQuandoAssinaturaForInvalida() {
        TokenService outroService = new TokenService();
        ReflectionTestUtils.setField(outroService, "secret", ALTERNATE_SECRET);
        ReflectionTestUtils.setField(outroService, "expiration", 60_000L);
        outroService.initKey();

        String tokenComOutraAssinatura = outroService.gerarToken("user@example.com", Map.of());

        assertThat(tokenService.validarETrazerUsuario(tokenComOutraAssinatura)).isNull();
    }

    @Test
    void tokenValidoDeveRetornarFalsoParaTokenExpirado() {
        ReflectionTestUtils.setField(tokenService, "expiration", -1L);
        String expirado = tokenService.gerarToken("user@example.com", Map.of());

        var userDetails = new org.springframework.security.core.userdetails.User(
                "user@example.com",
                "senha",
                java.util.List.of()
        );

        assertThat(tokenService.tokenValido(expirado, userDetails)).isFalse();
    }

    @Test
    void tokenValidoDeveConfirmarTokenQuandoUsuarioEExpiracaoForemValidos() {
        String token = tokenService.gerarToken("user@example.com", Map.of());

        var userDetails = org.mockito.Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class);
        org.mockito.Mockito.when(userDetails.getUsername()).thenReturn("user@example.com");

        assertThat(tokenService.tokenValido(token, userDetails)).isTrue();
    }

    @Test
    void initKeyDeveLancarExcecaoQuandoSegredoNaoForBase64Valido() {
        TokenService semSegredoValido = new TokenService();
        ReflectionTestUtils.setField(semSegredoValido, "secret", "segredo-invalido");

        assertThatThrownBy(semSegredoValido::initKey)
                .isInstanceOf(IllegalArgumentException.class);
    }

}