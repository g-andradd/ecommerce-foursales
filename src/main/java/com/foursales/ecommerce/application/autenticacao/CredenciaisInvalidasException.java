package com.foursales.ecommerce.application.autenticacao;

public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException() {
        super("Credenciais invalidas");
    }
}
