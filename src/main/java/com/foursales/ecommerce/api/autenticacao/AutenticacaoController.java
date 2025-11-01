package com.foursales.ecommerce.api.autenticacao;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foursales.ecommerce.api.autenticacao.dto.RequisicaoCadastroUsuario;
import com.foursales.ecommerce.api.autenticacao.dto.RequisicaoLogin;
import com.foursales.ecommerce.api.autenticacao.dto.RespostaToken;
import com.foursales.ecommerce.application.autenticacao.AutenticarUsuarioService;
import com.foursales.ecommerce.application.autenticacao.GerenciadorTokenAutenticacao;
import com.foursales.ecommerce.application.autenticacao.TokenAutenticacao;
import com.foursales.ecommerce.application.usuario.CadastroUsuarioService;
import com.foursales.ecommerce.application.usuario.ComandoCadastroUsuario;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/autenticacao")
@Validated
public class AutenticacaoController {

    private final AutenticarUsuarioService autenticarUsuarioService;
    private final CadastroUsuarioService cadastroUsuarioService;
    private final GerenciadorTokenAutenticacao gerenciadorTokenAutenticacao;

    public AutenticacaoController(AutenticarUsuarioService autenticarUsuarioService,
            CadastroUsuarioService cadastroUsuarioService,
            GerenciadorTokenAutenticacao gerenciadorTokenAutenticacao) {
        this.autenticarUsuarioService = autenticarUsuarioService;
        this.cadastroUsuarioService = cadastroUsuarioService;
        this.gerenciadorTokenAutenticacao = gerenciadorTokenAutenticacao;
    }

    @PostMapping("/login")
    public ResponseEntity<RespostaToken> login(@RequestBody @Valid RequisicaoLogin requisicao) {
        var token = autenticarUsuarioService.autenticar(requisicao.email(), requisicao.senha());
        return ResponseEntity.ok(mapear(token));
    }

    @PostMapping("/registrar")
    public ResponseEntity<RespostaToken> registrar(@RequestBody @Valid RequisicaoCadastroUsuario requisicao) {
        var comando = new ComandoCadastroUsuario(requisicao.nome(), requisicao.email(), requisicao.senha(),
                requisicao.papel());
        var usuario = cadastroUsuarioService.cadastrar(comando);
        var token = gerenciadorTokenAutenticacao.gerar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapear(token));
    }

    private RespostaToken mapear(TokenAutenticacao token) {
        return new RespostaToken(token.token(), token.tipo(), token.expiraEm());
    }
}
