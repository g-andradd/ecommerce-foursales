package com.foursales.ecommerce.api.auth.dto;

public record LoginResponse(String token, String tipo, long expiraEm) {}
