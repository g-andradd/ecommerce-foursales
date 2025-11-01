package com.foursales.ecommerce.domain.usuario;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 120, unique = true)
    private String email;

    @Column(nullable = false, length = 120)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PapelUsuario papel;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    protected Usuario() {
        // construtor padrao para JPA
    }

    private Usuario(String nome, String email, String senha, PapelUsuario papel) {
        this.nome = Objects.requireNonNull(nome, "nome");
        this.email = Objects.requireNonNull(email, "email");
        this.senha = Objects.requireNonNull(senha, "senha");
        this.papel = Objects.requireNonNull(papel, "papel");
    }

    public static Usuario novo(String nome, String email, String senha, PapelUsuario papel) {
        return new Usuario(nome, email, senha, papel);
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public PapelUsuario getPapel() {
        return papel;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public Set<String> autoridades() {
        return Set.of(papel.autoridade());
    }

    public void atualizarSenha(String senhaHash) {
        this.senha = Objects.requireNonNull(senhaHash, "senhaHash");
    }
}
