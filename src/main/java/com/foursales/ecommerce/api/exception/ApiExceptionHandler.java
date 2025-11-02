package com.foursales.ecommerce.api.exception;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.foursales.ecommerce.application.exceptions.CredenciaisInvalidasException;
import com.foursales.ecommerce.application.exceptions.EmailUsuarioDuplicadoException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        List<ErroCampo> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapearCampo)
                .toList();
        var resposta = new ErroResponse("VALIDACAO", "Existem campos invalidos", agora(), campos);
        return ResponseEntity.badRequest().body(resposta);
    }

    @ExceptionHandler(EmailUsuarioDuplicadoException.class)
    public ResponseEntity<ErroResponse> tratarEmailDuplicado(EmailUsuarioDuplicadoException ex) {
        var resposta = new ErroResponse("CONFLITO", ex.getMessage(), agora(), List.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resposta);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResponse> tratarCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        var resposta = new ErroResponse("NAO_AUTORIZADO", ex.getMessage(), agora(), List.of());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroGenerico(Exception ex) {
        var resposta = new ErroResponse("ERRO_INTERNO", "Erro inesperado", agora(), List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
    }

    private ErroCampo mapearCampo(FieldError erro) {
        return new ErroCampo(erro.getField(), erro.getDefaultMessage());
    }

    private OffsetDateTime agora() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
