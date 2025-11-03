package com.foursales.ecommerce.application.service.produto;

import com.foursales.ecommerce.api.dto.produto.AtualizarProdutoRequest;
import com.foursales.ecommerce.api.dto.produto.CriarProdutoRequest;
import com.foursales.ecommerce.api.dto.produto.ProdutoResponse;
import com.foursales.ecommerce.application.exception.RecursoNaoEncontradoException;
import com.foursales.ecommerce.application.mapper.ProdutoMapper;
import com.foursales.ecommerce.domain.entity.Produto;
import com.foursales.ecommerce.domain.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private ApplicationEventPublisher publisher;

    private ProdutoService service;

    @BeforeEach
    void setUp() {
        service = new ProdutoService(produtoRepository, new ProdutoMapper(), publisher);
    }

    @Test
    void criarDevePersistirProdutoERetornarResponse() {
        CriarProdutoRequest request = new CriarProdutoRequest(
                "Teclado",
                "Teclado mecânico",
                new BigDecimal("199.90"),
                "Periféricos",
                10
        );

        UUID produtoId = UUID.randomUUID();
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto produto = invocation.getArgument(0);
            produto.setId(produtoId);
            produto.setCriadoEm(LocalDateTime.now());
            produto.setAtualizadoEm(LocalDateTime.now());
            return produto;
        });

        ProdutoResponse response = service.criar(request);

        verify(produtoRepository).save(any(Produto.class));
        assertThat(response.id()).isEqualTo(produtoId);
        assertThat(response.nome()).isEqualTo("Teclado");
        assertThat(response.preco()).isEqualByComparingTo("199.90");
        assertThat(response.quantidadeEstoque()).isEqualTo(10);
        verifyNoInteractions(publisher);
    }

    @Test
    void atualizarDevePublicarEventoQuandoPrecoForAlterado() {
        UUID produtoId = UUID.randomUUID();
        Produto existente = Produto.builder()
                .id(produtoId)
                .nome("Mouse")
                .descricao("Mouse óptico")
                .preco(new BigDecimal("89.90"))
                .categoria("Periféricos")
                .quantidadeEstoque(15)
                .build();

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(existente));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AtualizarProdutoRequest request = new AtualizarProdutoRequest(
                "Mouse",
                "Mouse óptico",
                new BigDecimal("99.90"),
                "Periféricos",
                15
        );

        service.atualizar(produtoId, request);

        ArgumentCaptor<ProdutoPrecoAtualizadoEvent> captor = ArgumentCaptor.forClass(ProdutoPrecoAtualizadoEvent.class);
        verify(publisher).publishEvent(captor.capture());
        ProdutoPrecoAtualizadoEvent event = captor.getValue();
        assertThat(event.produtoId()).isEqualTo(produtoId);
        assertThat(event.novoPreco()).isEqualByComparingTo("99.90");
    }

    @Test
    void atualizarNaoDevePublicarEventoQuandoPrecoNaoMudar() {
        UUID produtoId = UUID.randomUUID();
        Produto existente = Produto.builder()
                .id(produtoId)
                .nome("Mouse")
                .descricao("Mouse óptico")
                .preco(new BigDecimal("89.90"))
                .categoria("Periféricos")
                .quantidadeEstoque(15)
                .build();

        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(existente));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AtualizarProdutoRequest request = new AtualizarProdutoRequest(
                "Mouse Gamer",
                "Mouse óptico",
                new BigDecimal("89.90"),
                "Periféricos",
                20
        );

        ProdutoResponse response = service.atualizar(produtoId, request);

        assertThat(response.nome()).isEqualTo("Mouse Gamer");
        assertThat(response.quantidadeEstoque()).isEqualTo(20);
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void atualizarDeveLancarExcecaoQuandoProdutoNaoExiste() {
        UUID produtoId = UUID.randomUUID();
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        AtualizarProdutoRequest request = new AtualizarProdutoRequest(
                "Mouse",
                "Mouse óptico",
                new BigDecimal("99.90"),
                "Periféricos",
                15
        );

        assertThatThrownBy(() -> service.atualizar(produtoId, request))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Produto nao encontrado");

        verify(produtoRepository, never()).save(any());
        verifyNoInteractions(publisher);
    }

    @Test
    void deletarDeveLancarExcecaoQuandoProdutoNaoExiste() {
        UUID produtoId = UUID.randomUUID();
        when(produtoRepository.existsById(produtoId)).thenReturn(false);

        assertThatThrownBy(() -> service.deletar(produtoId))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Produto nao encontrado");

        verify(produtoRepository, never()).deleteById(any());
    }

    @Test
    void buscarDeveLancarExcecaoQuandoProdutoNaoExiste() {
        UUID produtoId = UUID.randomUUID();
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(produtoId))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Produto nao encontrado");
    }

}