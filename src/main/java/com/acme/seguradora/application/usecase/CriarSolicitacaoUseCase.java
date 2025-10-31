package com.acme.seguradora.application.usecase;

import com.acme.seguradora.application.event.SolicitacaoRecebidaEvent;
import com.acme.seguradora.application.port.EventPublisherPort;
import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import com.acme.seguradora.domain.repository.SolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CriarSolicitacaoUseCase {

    private final SolicitacaoRepository solicitacaoRepository;
    private final EventPublisherPort eventPublisher;

    @Transactional
    public Solicitacao executar(Solicitacao solicitacao) {
        solicitacao.setId(UUID.randomUUID());
        solicitacao.setStatus(EstadoSolicitacao.RECEIVED);
        solicitacao.setCreatedAt(LocalDateTime.now());
        solicitacao.adicionarHistorico(EstadoSolicitacao.RECEIVED);

        Solicitacao solicitacaoSalva = solicitacaoRepository.salvar(solicitacao);

        SolicitacaoRecebidaEvent evento = SolicitacaoRecebidaEvent.builder()
                .solicitacaoId(solicitacaoSalva.getId())
                .customerId(solicitacaoSalva.getCustomerId())
                .productId(solicitacaoSalva.getProductId())
                .category(solicitacaoSalva.getCategory().name())
                .timestamp(LocalDateTime.now())
                .build();

        eventPublisher.publicar("solicitacao-recebida", evento);

        return solicitacaoSalva;
    }
}
