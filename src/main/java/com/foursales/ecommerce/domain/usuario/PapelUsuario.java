package com.foursales.ecommerce.domain.usuario;

public enum PapelUsuario {
    ADMIN,
    USER;

    public String autoridade() {
        return "ROLE_" + name();
    }
}
