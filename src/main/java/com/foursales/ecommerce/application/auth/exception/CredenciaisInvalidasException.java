package com.foursales.ecommerce.application.auth.exception;

public class CredenciaisInvalidasException extends RuntimeException {
    private static final String DEFAULT_MSG = "Credenciais invalidas";

    public CredenciaisInvalidasException(String message) {
        super(message == null ? DEFAULT_MSG : message);
    }
}
