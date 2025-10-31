package com.acme.seguradora.application.usecase;

import com.acme.seguradora.application.event.SolicitacaoAprovadaEvent;
import com.acme.seguradora.application.event.SolicitacaoRejeitadaEvent;
import com.acme.seguradora.application.port.EventPublisherPort;
import com.acme.seguradora.application.service.GerenciadorEstadoService;
import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import com.acme.seguradora.domain.exception.SolicitacaoNaoEncontradaException;
import com.acme.seguradora.domain.repository.SolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessarEventoPagamentoUseCase {

    private final SolicitacaoRepository solicitacaoRepository;
    private final GerenciadorEstadoService gerenciadorEstado;
    private final EventPublisherPort eventPublisher;

    @Transactional
    public void executar(UUID solicitacaoId, boolean pagamentoAprovado) {
        Solicitacao solicitacao = solicitacaoRepository.buscarPorId(solicitacaoId)
                .orElseThrow(() -> new SolicitacaoNaoEncontradaException(
                        "Solicitação não encontrada: " + solicitacaoId));

        if (!pagamentoAprovado) {
            gerenciadorEstado.atualizarEstado(solicitacao, EstadoSolicitacao.REJECTED);
            solicitacaoRepository.salvar(solicitacao);

            eventPublisher.publicar(
                    "solicitacao-rejeitada",
                    SolicitacaoRejeitadaEvent.builder()
                            .solicitacaoId(solicitacao.getId())
                            .customerId(solicitacao.getCustomerId())
                            .motivo("Pagamento negado")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            return;
        }

        gerenciadorEstado.atualizarEstado(solicitacao, EstadoSolicitacao.APPROVED);
        solicitacaoRepository.salvar(solicitacao);

        eventPublisher.publicar(
                "solicitacao-aprovada",
                SolicitacaoAprovadaEvent.builder()
                        .solicitacaoId(solicitacao.getId())
                        .customerId(solicitacao.getCustomerId())
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        log.info("Solicitação {} processada com sucesso: {}", solicitacaoId, pagamentoAprovado ? "APROVADA" : "REJEITADA");
    }
}
