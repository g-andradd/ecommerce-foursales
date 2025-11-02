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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final AutenticacaoFacade auth;
    private final PedidoStatusService pedidoStatusService;

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public PedidoResponse criar(CriarPedidoRequest request) {
        if (request.itens().isEmpty()) throw new RegraNegocioException("Pedido sem itens");

        UUID usuarioId = auth.getUsuarioId();
        Pedido pedido = Pedido.builder()
                .usuarioId(usuarioId)
                .status(StatusPedido.PENDENTE)
                .build();

        for (var itemReq : request.itens()) {
            Produto prod = produtoRepository.findById(itemReq.produtoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado: " + itemReq.produtoId()));
            ItemPedido item = ItemPedido.of(prod.getId(), itemReq.quantidade(), prod.getPreco());
            pedido.adicionarItem(item);
        }
        Pedido salvo = pedidoRepository.save(pedido);
        return toResponse(salvo);
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public PedidoResponse pagar(UUID pedidoId) {
        UUID usuarioId = auth.getUsuarioId();

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido nao encontrado"));

        if (!pedido.getUsuarioId().equals(usuarioId))
            throw new RegraNegocioException("Pedido nao pertence ao usuario");

        if (pedido.getStatus() != StatusPedido.PENDENTE)
            throw new RegraNegocioException("Pedido nao esta pendente");

        var idsProdutos = pedido.getItens().stream()
                .map(ItemPedido::getProdutoId).collect(Collectors.toSet());

        var produtosBloqueados = produtoRepository.findAllByIdForUpdate(idsProdutos);
        Map<UUID, Produto> porId = produtosBloqueados.stream()
                .collect(Collectors.toMap(Produto::getId, Function.identity()));

        for (ItemPedido item : pedido.getItens()) {
            Produto prod = porId.get(item.getProdutoId());
            if (prod == null) {
                pedidoStatusService.marcarCancelado(pedido);
                throw new RegraNegocioException("Produto do item nao existe: " + item.getProdutoId());
            }
            if (prod.getQuantidadeEstoque() < item.getQuantidade()) {
                pedidoStatusService.marcarCancelado(pedido);
                throw new RegraNegocioException("Estoque insuficiente para produto: " + prod.getId());
            }
        }

        for (ItemPedido item : pedido.getItens()) {
            int atualizadas = produtoRepository
                    .decrementarEstoqueSeDisponivel(item.getProdutoId(), item.getQuantidade());
            if (atualizadas == 0) {
                pedidoStatusService.marcarCancelado(pedido);
                throw new RegraNegocioException("Estoque insuficiente (concorrencia) para produto: " + item.getProdutoId());
            }
        }

        pedidoStatusService.marcarPago(pedido);
        return toResponse(pedido);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('USER')")
    public Page<PedidoResponse> listarDoUsuario(Pageable pageable) {
        UUID usuarioId = auth.getUsuarioId();
        return pedidoRepository.findByUsuarioId(usuarioId, pageable).map(this::toResponse);
    }

    private PedidoResponse toResponse(Pedido p) {
        var itens = p.getItens().stream()
                .map(i -> new PedidoResponse.ItemResponse(
                        i.getProdutoId(), i.getQuantidade(), i.getPrecoUnitario(), i.getSubtotal()))
                .toList();
        p.recalcularTotal();
        return new PedidoResponse(p.getId(), p.getStatus(), p.getTotal(), p.getCriadoEm(), p.getPagoEm(), itens);
    }
}
