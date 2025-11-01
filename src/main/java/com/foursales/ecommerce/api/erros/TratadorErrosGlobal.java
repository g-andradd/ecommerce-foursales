package com.foursales.ecommerce.api.erros;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.foursales.ecommerce.application.autenticacao.CredenciaisInvalidasException;
import com.foursales.ecommerce.application.usuario.EmailUsuarioDuplicadoException;

@RestControllerAdvice
public class TratadorErrosGlobal {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(MethodArgumentNotValidException ex) {
        List<ErroCampo> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapearCampo)
                .toList();
        var resposta = new ErroResposta("VALIDACAO", "Existem campos invalidos", agora(), campos);
        return ResponseEntity.badRequest().body(resposta);
    }

    @ExceptionHandler(EmailUsuarioDuplicadoException.class)
    public ResponseEntity<ErroResposta> tratarEmailDuplicado(EmailUsuarioDuplicadoException ex) {
        var resposta = new ErroResposta("CONFLITO", ex.getMessage(), agora(), List.of());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resposta);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResposta> tratarCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        var resposta = new ErroResposta("NAO_AUTORIZADO", ex.getMessage(), agora(), List.of());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> tratarErroGenerico(Exception ex) {
        var resposta = new ErroResposta("ERRO_INTERNO", "Erro inesperado", agora(), List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
    }

    private ErroCampo mapearCampo(FieldError erro) {
        return new ErroCampo(erro.getField(), erro.getDefaultMessage());
    }

    private OffsetDateTime agora() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
