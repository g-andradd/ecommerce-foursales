package com.foursales.ecommerce.application.mapper;

import com.foursales.ecommerce.api.dto.produto.AtualizarProdutoRequest;
import com.foursales.ecommerce.api.dto.produto.CriarProdutoRequest;
import com.foursales.ecommerce.api.dto.produto.ProdutoResponse;
import com.foursales.ecommerce.domain.entity.Produto;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    public Produto toEntity(CriarProdutoRequest r) {
        return Produto.builder()
                .nome(r.nome())
                .descricao(r.descricao())
                .preco(r.preco())
                .categoria(r.categoria())
                .quantidadeEstoque(r.quantidadeEstoque())
                .build();
    }

    public void copy(AtualizarProdutoRequest r, Produto p) {
        p.setNome(r.nome());
        p.setDescricao(r.descricao());
        p.setPreco(r.preco());
        p.setCategoria(r.categoria());
        p.setQuantidadeEstoque(r.quantidadeEstoque());
    }

    public ProdutoResponse toResponse(Produto p) {
        return new ProdutoResponse(
                p.getId(), p.getNome(), p.getDescricao(), p.getPreco(),
                p.getCategoria(), p.getQuantidadeEstoque(),
                p.getCriadoEm(), p.getAtualizadoEm()
        );
    }

}
