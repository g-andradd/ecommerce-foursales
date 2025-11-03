package com.foursales.ecommerce.application.auth;

import com.foursales.ecommerce.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticacaoFacadeTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AutenticacaoFacade autenticacaoFacade;

    @AfterEach
    void cleanUpContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getUsuarioIdDeveRetornarIdQuandoAutenticado() {
        UUID esperado = UUID.randomUUID();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "user@example.com",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(usuarioRepository.findIdByEmail("user@example.com")).thenReturn(Optional.of(esperado));

        UUID resultado = autenticacaoFacade.getUsuarioId();

        assertThat(resultado).isEqualTo(esperado);
    }

    @Test
    void getUsuarioIdDeveLancarExcecaoQuandoUsuarioNaoForEncontrado() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("user@example.com", "", List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(usuarioRepository.findIdByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> autenticacaoFacade.getUsuarioId())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Usuario nao encontrado");
    }

    @Test
    void getUsuarioIdDeveLancarExcecaoQuandoNaoAutenticado() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> autenticacaoFacade.getUsuarioId())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Usuario nao autenticado");
    }

    @Test
    void getEmailDeveRetornarEmailDoUsuarioAutenticado() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("user@example.com", "", List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(autenticacaoFacade.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void getEmailDeveLancarExcecaoQuandoNaoAutenticado() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> autenticacaoFacade.getEmail())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Usuario nao autenticado");
    }

    @Test
    void hasRoleDeveRetornarVerdadeiroQuandoUsuarioPossuiPapel() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "user@example.com",
                "",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(autenticacaoFacade.hasRole("ADMIN")).isTrue();
        assertThat(autenticacaoFacade.hasRole("USER")).isFalse();
    }

}