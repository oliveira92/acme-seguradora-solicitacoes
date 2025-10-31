package com.acme.seguradora.application.usecase;

import com.acme.seguradora.application.dto.FraudeResponseDto;
import com.acme.seguradora.application.event.SolicitacaoPendenteEvent;
import com.acme.seguradora.application.event.SolicitacaoRejeitadaEvent;
import com.acme.seguradora.application.event.SolicitacaoValidadaEvent;
import com.acme.seguradora.application.port.EventPublisherPort;
import com.acme.seguradora.application.port.FraudeApiPort;
import com.acme.seguradora.application.service.GerenciadorEstadoService;
import com.acme.seguradora.application.service.ValidadorRegrasNegocioService;
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
public class ProcessarSolicitacaoUseCase {

    private final SolicitacaoRepository solicitacaoRepository;
    private final FraudeApiPort fraudeApiPort;
    private final ValidadorRegrasNegocioService validadorRegras;
    private final GerenciadorEstadoService gerenciadorEstado;
    private final EventPublisherPort eventPublisher;

    @Transactional
    public void executar(UUID solicitacaoId) {
        Solicitacao solicitacao = solicitacaoRepository.buscarPorId(solicitacaoId)
                .orElseThrow(() -> new SolicitacaoNaoEncontradaException(
                        "Solicitação não encontrada: " + solicitacaoId));

        FraudeResponseDto fraudeResponse = fraudeApiPort.consultarFraude(
                solicitacao.getId(),
                solicitacao.getCustomerId()
        );

        boolean aprovado = validadorRegras.validarSolicitacao(
                solicitacao,
                fraudeResponse.getClassification()
        );

        if (aprovado) {
            gerenciadorEstado.atualizarEstado(solicitacao, EstadoSolicitacao.VALIDATED);
            solicitacaoRepository.salvar(solicitacao);

            eventPublisher.publicar("solicitacao-validada",
                    SolicitacaoValidadaEvent.builder()
                            .solicitacaoId(solicitacao.getId())
                            .customerId(solicitacao.getCustomerId())
                            .classification(fraudeResponse.getClassification().name())
                            .timestamp(LocalDateTime.now())
                            .build()
            );

            gerenciadorEstado.atualizarEstado(solicitacao, EstadoSolicitacao.PENDING);
            solicitacaoRepository.salvar(solicitacao);

            eventPublisher.publicar("solicitacao-pendente",
                    SolicitacaoPendenteEvent.builder()
                            .solicitacaoId(solicitacao.getId())
                            .customerId(solicitacao.getCustomerId())
                            .timestamp(LocalDateTime.now())
                            .build()
            );

        } else {
            gerenciadorEstado.atualizarEstado(solicitacao, EstadoSolicitacao.REJECTED);
            solicitacaoRepository.salvar(solicitacao);

            eventPublisher.publicar("solicitacao-rejeitada",
                    SolicitacaoRejeitadaEvent.builder()
                            .solicitacaoId(solicitacao.getId())
                            .customerId(solicitacao.getCustomerId())
                            .motivo("Capital segurado excede limite para classificação: "
                                    + fraudeResponse.getClassification())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }
}
