package com.foursales.ecommerce.infra.security;

import com.foursales.ecommerce.domain.entity.Usuario;
import com.foursales.ecommerce.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DetalhesUsuarioService implements UserDetailsService{

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final String normalizedEmail = (email == null) ? null : email.trim().toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name());
        Set<GrantedAuthority> authorities = Set.of(authority);

        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.isAtivo(),
                true,
                true,
                true,
                authorities
        );
    }


}
