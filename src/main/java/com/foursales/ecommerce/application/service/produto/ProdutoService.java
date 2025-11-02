package com.foursales.ecommerce.application.service.produto;

import com.foursales.ecommerce.api.dto.produto.AtualizarProdutoRequest;
import com.foursales.ecommerce.api.dto.produto.CriarProdutoRequest;
import com.foursales.ecommerce.api.dto.produto.ProdutoResponse;
import com.foursales.ecommerce.application.exception.RecursoNaoEncontradoException;
import com.foursales.ecommerce.application.mapper.ProdutoMapper;
import com.foursales.ecommerce.domain.entity.Produto;
import com.foursales.ecommerce.domain.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProdutoResponse criar(CriarProdutoRequest request) {
        Produto produto = produtoMapper.toEntity(request);
        produto = produtoRepository.save(produto);
        return produtoMapper.toResponse(produto);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProdutoResponse atualizar(UUID id, AtualizarProdutoRequest request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado"));

        var precoAnterior = produto.getPreco();

        produtoMapper.copy(request, produto);
        Produto salvo = produtoRepository.save(produto);

        if (precoAnterior == null || salvo.getPreco().compareTo(precoAnterior) != 0) {
            publisher.publishEvent(new ProdutoPrecoAtualizadoEvent(salvo.getId(), salvo.getPreco()));
        }

        return produtoMapper.toResponse(salvo);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deletar(UUID id) {
        if (!produtoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Produto nao encontrado");
        }
        produtoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ProdutoResponse buscar(UUID id) {
        return produtoRepository.findById(id)
                .map(produtoMapper::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado"));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<ProdutoResponse> listar(Pageable pageable) {
        return produtoRepository.findAll(pageable).map(produtoMapper::toResponse);
    }

}
