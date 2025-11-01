package com.foursales.ecommerce.application.usuario;

public class EmailUsuarioDuplicadoException extends RuntimeException {

    public EmailUsuarioDuplicadoException(String email) {
        super("Email ja cadastrado: " + email);
    }
}
