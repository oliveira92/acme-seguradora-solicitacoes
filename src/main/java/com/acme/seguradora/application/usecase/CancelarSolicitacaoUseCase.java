package com.acme.seguradora.application.usecase;

import com.acme.seguradora.application.event.SolicitacaoCanceladaEvent;
import com.acme.seguradora.application.port.EventPublisherPort;
import com.acme.seguradora.application.service.GerenciadorEstadoService;
import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import com.acme.seguradora.domain.exception.CancelamentoNaoPermitidoException;
import com.acme.seguradora.domain.exception.SolicitacaoNaoEncontradaException;
import com.acme.seguradora.domain.repository.SolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelarSolicitacaoUseCase {

    private final SolicitacaoRepository solicitacaoRepository;
    private final GerenciadorEstadoService gerenciadorEstado;
    private final EventPublisherPort eventPublisher;

    @Transactional
    public void executar(UUID solicitacaoId) {
        Solicitacao solicitacao = solicitacaoRepository.buscarPorId(solicitacaoId)
                .orElseThrow(() -> new SolicitacaoNaoEncontradaException(
                        "Solicitação não encontrada: " + solicitacaoId));

        if (!solicitacao.podeSerCancelada()) {
            throw new CancelamentoNaoPermitidoException(
                    "Não é possível cancelar solicitação no estado: " + solicitacao.getStatus());
        }

        gerenciadorEstado.atualizarEstado(solicitacao, EstadoSolicitacao.CANCELED);

        eventPublisher.publicar(
                "solicitacao-cancelada",
                SolicitacaoCanceladaEvent.builder()
                        .solicitacaoId(solicitacao.getId())
                        .customerId(solicitacao.getCustomerId())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
