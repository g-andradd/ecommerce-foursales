package com.foursales.ecommerce.application.auth;

import com.foursales.ecommerce.api.auth.dto.LoginRequest;
import com.foursales.ecommerce.api.auth.dto.LoginResponse;
import com.foursales.ecommerce.api.auth.dto.RegistrarUsuarioRequest;
import com.foursales.ecommerce.application.auth.exception.CredenciaisInvalidasException;
import com.foursales.ecommerce.application.auth.exception.EmailUsuarioDuplicadoException;
import com.foursales.ecommerce.domain.entity.TipoPerfil;
import com.foursales.ecommerce.domain.entity.Usuario;
import com.foursales.ecommerce.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    private Usuario usuarioAtivo;

    @BeforeEach
    void setUp() {
        usuarioAtivo = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Maria")
                .email("maria@example.com")
                .senha("123")
                .perfil(TipoPerfil.USER)
                .ativo(true)
                .build();
    }

    @Test
    void loginDeveRetornarTokenQuandoCredenciaisForemValidas() {
        LoginRequest request = new LoginRequest("  MARIA@example.com  ", "senha");

        when(usuarioRepository.findByEmail("maria@example.com")).thenReturn(Optional.of(usuarioAtivo));
        when(tokenService.gerarToken(eq("maria@example.com"), any())).thenReturn("jwt-token");

        LoginResponse response = autenticacaoService.login(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        UsernamePasswordAuthenticationToken authToken = captor.getValue();
        assertThat(authToken.getName()).isEqualTo("maria@example.com");
        assertThat(authToken.getCredentials()).isEqualTo("senha");
        verify(tokenService).gerarToken(eq("maria@example.com"), any());
    }

    @Test
    void loginDeveLancarExcecaoQuandoCredenciaisForemInvalidas() {
        LoginRequest request = new LoginRequest("user@example.com", "senha");
        doThrow(new BadCredentialsException("bad")).when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> autenticacaoService.login(request))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessageContaining("Credenciais inválidas");
    }

    @Test
    void loginDeveLancarExcecaoQuandoContaEstiverDesativada() {
        LoginRequest request = new LoginRequest("user@example.com", "senha");
        doThrow(new DisabledException("disabled")).when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> autenticacaoService.login(request))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessageContaining("Conta desativada");
    }

    @Test
    void loginDeveLancarExcecaoQuandoContaEstiverBloqueada() {
        LoginRequest request = new LoginRequest("user@example.com", "senha");
        doThrow(new LockedException("locked")).when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> autenticacaoService.login(request))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessageContaining("Conta bloqueada");
    }

    @Test
    void loginDeveLancarExcecaoQuandoUsuarioNaoExistir() {
        LoginRequest request = new LoginRequest("user@example.com", "senha");
        when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autenticacaoService.login(request))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    void loginDeveLancarExcecaoQuandoUsuarioEstiverInativo() {
        LoginRequest request = new LoginRequest("user@example.com", "senha");
        Usuario inativo = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("João")
                .email("user@example.com")
                .senha("123")
                .perfil(TipoPerfil.USER)
                .ativo(false)
                .build();
        when(usuarioRepository.findByEmail("user@example.com")).thenReturn(Optional.of(inativo));

        assertThatThrownBy(() -> autenticacaoService.login(request))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessageContaining("Conta desativada");
    }

    @Test
    void loginDeveLancarExcecaoQuandoEmailForNuloOuVazio() {
        LoginRequest requestComNull = new LoginRequest(null, "senha");
        LoginRequest requestComVazio = new LoginRequest("   ", "senha");

        assertThatThrownBy(() -> autenticacaoService.login(requestComNull))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessageContaining("Email é obrigatório");

        assertThatThrownBy(() -> autenticacaoService.login(requestComVazio))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessageContaining("Email é obrigatório");
    }

    @Test
    void loginDeveLancarExcecaoQuandoSenhaForNulaOuVazia() {
        LoginRequest requestComNull = new LoginRequest("user@example.com", null);
        LoginRequest requestComVazio = new LoginRequest("user@example.com", " ");

        assertThatThrownBy(() -> autenticacaoService.login(requestComNull))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessageContaining("Senha é obrigatória");

        assertThatThrownBy(() -> autenticacaoService.login(requestComVazio))
                .isInstanceOf(CredenciaisInvalidasException.class)
                .hasMessageContaining("Senha é obrigatória");
    }

    @Test
    void registrarUsuarioDeveLancarExcecaoQuandoEmailJaExistir() {
        RegistrarUsuarioRequest request = new RegistrarUsuarioRequest("Maria", "maria@example.com", "senha", TipoPerfil.USER);
        when(usuarioRepository.existsByEmail("maria@example.com")).thenReturn(true);

        assertThatThrownBy(() -> autenticacaoService.registrarUsuario(request))
                .isInstanceOf(EmailUsuarioDuplicadoException.class)
                .hasMessageContaining("Email já cadastrado");
    }

    @Test
    void registrarUsuarioDeveUsarPerfilUserQuandoNaoInformado() {
        RegistrarUsuarioRequest request = new RegistrarUsuarioRequest("Maria", "  MARIA@example.com  ", "senha", null);
        when(usuarioRepository.existsByEmail("maria@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senha")).thenReturn("hash");

        autenticacaoService.registrarUsuario(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario salvo = captor.getValue();
        assertThat(salvo.getEmail()).isEqualTo("maria@example.com");
        assertThat(salvo.getPerfil()).isEqualTo(TipoPerfil.USER);
        assertThat(salvo.isAtivo()).isTrue();
        verify(passwordEncoder).encode("senha");
    }

    @Test
    void registrarUsuarioDevePersistirDadosComPerfilInformado() {
        RegistrarUsuarioRequest request = new RegistrarUsuarioRequest("Admin", "admin@example.com", "senha", TipoPerfil.ADMIN);
        when(usuarioRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senha")).thenReturn("hash");

        autenticacaoService.registrarUsuario(request);

        verify(usuarioRepository).save(argThat(usuario ->
                usuario.getPerfil() == TipoPerfil.ADMIN &&
                        usuario.getEmail().equals("admin@example.com") &&
                        usuario.getSenha().equals("hash")));
    }

}