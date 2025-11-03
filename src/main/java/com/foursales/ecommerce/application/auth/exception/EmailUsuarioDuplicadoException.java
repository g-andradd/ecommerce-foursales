package com.foursales.ecommerce.application.auth.exception;

public class EmailUsuarioDuplicadoException extends RuntimeException {
    public EmailUsuarioDuplicadoException(String email) {
        super("Email ja cadastrado: " + email);
    }
}
