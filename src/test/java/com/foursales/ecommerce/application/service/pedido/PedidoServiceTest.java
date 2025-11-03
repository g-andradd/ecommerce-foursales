package com.foursales.ecommerce.application.service.pedido;

import com.foursales.ecommerce.api.dto.pedido.CriarPedidoRequest;
import com.foursales.ecommerce.api.dto.pedido.PedidoResponse;
import com.foursales.ecommerce.application.auth.AutenticacaoFacade;
import com.foursales.ecommerce.application.exception.RecursoNaoEncontradoException;
import com.foursales.ecommerce.application.exception.RegraNegocioException;
import com.foursales.ecommerce.domain.entity.ItemPedido;
import com.foursales.ecommerce.domain.entity.Pedido;
import com.foursales.ecommerce.domain.entity.Produto;
import com.foursales.ecommerce.domain.entity.StatusPedido;
import com.foursales.ecommerce.domain.repository.PedidoRepository;
import com.foursales.ecommerce.domain.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private AutenticacaoFacade autenticacaoFacade;
    @Mock
    private PedidoStatusService pedidoStatusService;

    private PedidoService service;

    @BeforeEach
    void setUp() {
        service = new PedidoService(pedidoRepository, produtoRepository, autenticacaoFacade, pedidoStatusService);
    }

    @Test
    void criarDevePersistirPedidoComItens() {
        UUID usuarioId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        CriarPedidoRequest.ItemPedidoRequest item = new CriarPedidoRequest.ItemPedidoRequest(produtoId, 2);
        CriarPedidoRequest request = new CriarPedidoRequest(List.of(item));

        Produto produto = Produto.builder()
                .id(produtoId)
                .preco(new BigDecimal("10.00"))
                .quantidadeEstoque(20)
                .build();

        when(autenticacaoFacade.getUsuarioId()).thenReturn(usuarioId);
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(UUID.randomUUID());
            pedido.setCriadoEm(LocalDateTime.now());
            return pedido;
        });

        PedidoResponse response = service.criar(request);

        assertThat(response.itens()).hasSize(1);
        assertThat(response.total()).isEqualByComparingTo("20.00");
        verify(produtoRepository).findById(produtoId);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void criarDeveRejeitarPedidoSemItens() {
        CriarPedidoRequest request = new CriarPedidoRequest(List.of());

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Pedido sem itens");

        verifyNoInteractions(pedidoRepository, produtoRepository, autenticacaoFacade);
    }

    @Test
    void criarDeveLancarExcecaoQuandoProdutoNaoExiste() {
        UUID produtoId = UUID.randomUUID();
        CriarPedidoRequest.ItemPedidoRequest item = new CriarPedidoRequest.ItemPedidoRequest(produtoId, 1);
        CriarPedidoRequest request = new CriarPedidoRequest(List.of(item));

        when(autenticacaoFacade.getUsuarioId()).thenReturn(UUID.randomUUID());
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(request))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Produto nao encontrado");

        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void pagarDeveAtualizarEstoqueEStatusQuandoSucesso() {
        UUID usuarioId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .usuarioId(usuarioId)
                .status(StatusPedido.PENDENTE)
                .total(BigDecimal.ZERO)
                .build();
        pedido.adicionarItem(ItemPedido.of(produtoId, 3, new BigDecimal("5.00")));

        Produto produto = Produto.builder()
                .id(produtoId)
                .quantidadeEstoque(5)
                .preco(new BigDecimal("5.00"))
                .build();

        when(autenticacaoFacade.getUsuarioId()).thenReturn(usuarioId);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(produtoRepository.findAllByIdForUpdate(anySet())).thenReturn(List.of(produto));
        when(produtoRepository.decrementarEstoqueSeDisponivel(produtoId, 3)).thenReturn(1);
        doAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.marcarPago();
            return null;
        }).when(pedidoStatusService).marcarPago(any(Pedido.class));

        PedidoResponse response = service.pagar(pedidoId);

        assertThat(response.status()).isEqualTo(StatusPedido.PAGO);
        verify(produtoRepository).findAllByIdForUpdate(anySet());
        verify(produtoRepository).decrementarEstoqueSeDisponivel(produtoId, 3);
        verify(pedidoStatusService).marcarPago(pedido);
        verify(pedidoStatusService, never()).marcarCancelado(any());
    }

    @Test
    void pagarDeveLancarExcecaoQuandoPedidoNaoPertenceAoUsuario() {
        UUID usuarioId = UUID.randomUUID();
        UUID outroUsuario = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();

        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .usuarioId(outroUsuario)
                .status(StatusPedido.PENDENTE)
                .total(BigDecimal.ZERO)
                .build();

        when(autenticacaoFacade.getUsuarioId()).thenReturn(usuarioId);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> service.pagar(pedidoId))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Pedido nao pertence ao usuario");

        verify(produtoRepository, never()).findAllByIdForUpdate(anySet());
        verify(pedidoStatusService, never()).marcarCancelado(any());
    }

    @Test
    void pagarDeveLancarExcecaoQuandoStatusNaoPendente() {
        UUID usuarioId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();

        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .usuarioId(usuarioId)
                .status(StatusPedido.PAGO)
                .total(BigDecimal.ZERO)
                .build();

        when(autenticacaoFacade.getUsuarioId()).thenReturn(usuarioId);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> service.pagar(pedidoId))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Pedido nao esta pendente");

        verify(produtoRepository, never()).findAllByIdForUpdate(anySet());
        verify(pedidoStatusService, never()).marcarCancelado(any());
    }

    @Test
    void pagarDeveCancelarQuandoProdutoNaoEncontrado() {
        UUID usuarioId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .usuarioId(usuarioId)
                .status(StatusPedido.PENDENTE)
                .total(BigDecimal.ZERO)
                .build();
        pedido.adicionarItem(ItemPedido.of(produtoId, 1, new BigDecimal("1.00")));

        when(autenticacaoFacade.getUsuarioId()).thenReturn(usuarioId);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(produtoRepository.findAllByIdForUpdate(anySet())).thenReturn(List.of());
        doAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.cancelar();
            return null;
        }).when(pedidoStatusService).marcarCancelado(any(Pedido.class));

        assertThatThrownBy(() -> service.pagar(pedidoId))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Produto do item nao existe");

        verify(pedidoStatusService).marcarCancelado(pedido);
        verify(produtoRepository, never()).decrementarEstoqueSeDisponivel(any(), anyInt());
    }

    @Test
    void pagarDeveCancelarQuandoEstoqueInsuficiente() {
        UUID usuarioId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .usuarioId(usuarioId)
                .status(StatusPedido.PENDENTE)
                .total(BigDecimal.ZERO)
                .build();
        pedido.adicionarItem(ItemPedido.of(produtoId, 5, new BigDecimal("2.00")));

        Produto produto = Produto.builder()
                .id(produtoId)
                .quantidadeEstoque(3)
                .preco(new BigDecimal("2.00"))
                .build();

        when(autenticacaoFacade.getUsuarioId()).thenReturn(usuarioId);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(produtoRepository.findAllByIdForUpdate(anySet())).thenReturn(List.of(produto));
        doAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.cancelar();
            return null;
        }).when(pedidoStatusService).marcarCancelado(any(Pedido.class));

        assertThatThrownBy(() -> service.pagar(pedidoId))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Estoque insuficiente para produto");

        verify(pedidoStatusService).marcarCancelado(pedido);
        verify(produtoRepository, never()).decrementarEstoqueSeDisponivel(any(), anyInt());
    }

    @Test
    void pagarDeveCancelarQuandoConcorrenciaImpedeAtualizacaoDoEstoque() {
        UUID usuarioId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        Pedido pedido = Pedido.builder()
                .id(pedidoId)
                .usuarioId(usuarioId)
                .status(StatusPedido.PENDENTE)
                .total(BigDecimal.ZERO)
                .build();
        pedido.adicionarItem(ItemPedido.of(produtoId, 2, new BigDecimal("3.00")));

        Produto produto = Produto.builder()
                .id(produtoId)
                .quantidadeEstoque(5)
                .preco(new BigDecimal("3.00"))
                .build();

        when(autenticacaoFacade.getUsuarioId()).thenReturn(usuarioId);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(produtoRepository.findAllByIdForUpdate(anySet())).thenReturn(List.of(produto));
        when(produtoRepository.decrementarEstoqueSeDisponivel(produtoId, 2)).thenReturn(0);
        doAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.cancelar();
            return null;
        }).when(pedidoStatusService).marcarCancelado(any(Pedido.class));

        assertThatThrownBy(() -> service.pagar(pedidoId))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Estoque insuficiente (concorrencia)");

        verify(produtoRepository).decrementarEstoqueSeDisponivel(produtoId, 2);
        verify(pedidoStatusService).marcarCancelado(pedido);
    }

    @Test
    void listarDoUsuarioDeveAplicarFiltroPorUsuarioAutenticado() {
        UUID usuarioId = UUID.randomUUID();
        Pedido pedido = Pedido.builder()
                .id(UUID.randomUUID())
                .usuarioId(usuarioId)
                .status(StatusPedido.PENDENTE)
                .total(new BigDecimal("50.00"))
                .build();
        pedido.adicionarItem(ItemPedido.of(UUID.randomUUID(), 1, new BigDecimal("50.00")));
        Page<Pedido> page = new PageImpl<>(List.of(pedido));

        when(autenticacaoFacade.getUsuarioId()).thenReturn(usuarioId);
        when(pedidoRepository.findByUsuarioId(eq(usuarioId), any(PageRequest.class))).thenReturn(page);

        Page<PedidoResponse> response = service.listarDoUsuario(PageRequest.of(0, 10));

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).total()).isEqualByComparingTo("50.00");
        verify(pedidoRepository).findByUsuarioId(eq(usuarioId), any(PageRequest.class));
    }

}