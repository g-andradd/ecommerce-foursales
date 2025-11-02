package com.foursales.ecommerce.api.exception;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException; // do Spring Security (APENAS se quiser mapear fallback 403 aqui)
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.foursales.ecommerce.application.auth.exception.CredenciaisInvalidasException;
import com.foursales.ecommerce.application.auth.exception.EmailUsuarioDuplicadoException;
import com.foursales.ecommerce.application.exception.RecursoNaoEncontradoException;
import com.foursales.ecommerce.application.exception.RegraNegocioException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        List<ErroCampo> campos = ex.getBindingResult().getFieldErrors()
                .stream().map(this::mapearCampo).toList();
        return resposta(HttpStatus.BAD_REQUEST, "VALIDACAO",
                "Existem campos invalidos", campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarBodyInvalido(HttpMessageNotReadableException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "REQUISICAO_INVALIDA",
                "Corpo da requisicao invalido", List.of());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponse> tratarParametroFaltando(MissingServletRequestParameterException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "REQUISICAO_INVALIDA",
                "Parametro obrigatorio ausente: " + ex.getParameterName(), List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tratarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "REQUISICAO_INVALIDA",
                "Tipo invalido para parametro: " + ex.getName(), List.of());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return resposta(HttpStatus.NOT_FOUND, "NAO_ENCONTRADO", ex.getMessage(), List.of());
    }

    @ExceptionHandler(EmailUsuarioDuplicadoException.class)
    public ResponseEntity<ErroResponse> tratarEmailDuplicado(EmailUsuarioDuplicadoException ex) {
        return resposta(HttpStatus.CONFLICT, "CONFLITO", ex.getMessage(), List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarIntegridade(DataIntegrityViolationException ex) {
        return resposta(HttpStatus.CONFLICT, "CONFLITO",
                "Violacao de integridade de dados", List.of());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraNegocio(RegraNegocioException ex) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, "REGRA_NEGOCIO", ex.getMessage(), List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> tratarMetodoNaoSuportado(HttpRequestMethodNotSupportedException ex) {
        return resposta(HttpStatus.METHOD_NOT_ALLOWED, "METODO_NAO_SUPORTADO",
                "Metodo HTTP nao suportado", List.of());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponse> tratarConstraint(ConstraintViolationException ex) {
        return resposta(HttpStatus.BAD_REQUEST, "VALIDACAO",
                "Parametros invalidos", List.of());
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResponse> tratarCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        return resposta(HttpStatus.UNAUTHORIZED, "NAO_AUTORIZADO", ex.getMessage(), List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> tratarAccessDenied(AccessDeniedException ex) {
        return resposta(HttpStatus.FORBIDDEN, "ACESSO_NEGADO",
                "Permissoes insuficientes", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroGenerico(Exception ex) {
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "ERRO_INTERNO",
                "Erro inesperado", List.of());
    }

    private ErroCampo mapearCampo(FieldError erro) {
        return new ErroCampo(erro.getField(), erro.getDefaultMessage());
    }

    private ResponseEntity<ErroResponse> resposta(HttpStatus status, String codigo,
                                                  String mensagem, List<ErroCampo> campos) {
        var body = new ErroResponse(codigo, mensagem, OffsetDateTime.now(ZoneOffset.UTC), campos);
        return ResponseEntity.status(status).body(body);
    }
}
