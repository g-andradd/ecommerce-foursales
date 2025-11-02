package com.foursales.ecommerce.application.exceptions;

public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException(String emailUsuario) {
        super("Credenciais invalidas");
    }
}
