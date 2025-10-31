package com.acme.seguradora.infrastructure.persistence.adapter;

import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.repository.SolicitacaoRepository;
import com.acme.seguradora.infrastructure.persistence.entity.HistoricoEstadoEntity;
import com.acme.seguradora.infrastructure.persistence.entity.SolicitacaoEntity;
import com.acme.seguradora.infrastructure.persistence.repository.SolicitacaoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SolicitacaoRepositoryAdapter implements SolicitacaoRepository {

    private final SolicitacaoJpaRepository jpaRepository;

    @Override
    public Solicitacao salvar(Solicitacao solicitacao) {
        SolicitacaoEntity entity = toEntity(solicitacao);
        SolicitacaoEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Solicitacao> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Solicitacao> buscarPorCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerId(customerId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private SolicitacaoEntity toEntity(Solicitacao domain) {
        SolicitacaoEntity entity = SolicitacaoEntity.builder()
                .id(domain.getId())
                .customerId(domain.getCustomerId())
                .productId(domain.getProductId())
                .category(domain.getCategory())
                .salesChannel(domain.getSaleChannel())
                .paymentMethod(domain.getPaymentMethod())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .finishedAt(domain.getFinishedAt())
                .totalMonthlyPremiumAmount(domain.getTotalMonthlyPremiumAmount())
                .insuredAmount(domain.getInsuredAmount())
                .coverages(domain.getCoverages())
                .assistances(domain.getAssistances())
                .build();

        List<HistoricoEstadoEntity> historicoEntities = domain.getHistory().stream()
                .map(h -> HistoricoEstadoEntity.builder()
                        .status(h.getStatus())
                        .timestamp(h.getTimestamp())
                        .solicitacao(entity)
                        .build())
                .collect(Collectors.toList());

        entity.setHistory(historicoEntities);
        return entity;
    }

    private Solicitacao toDomain(SolicitacaoEntity entity) {
        List<com.acme.seguradora.domain.entity.HistoricoEstado> historicos = entity.getHistory().stream()
                .map(h -> com.acme.seguradora.domain.entity.HistoricoEstado.builder()
                        .status(h.getStatus())
                        .timestamp(h.getTimestamp())
                        .build())
                .collect(Collectors.toList());

        return Solicitacao.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .productId(entity.getProductId())
                .category(entity.getCategory())
                .saleChannel(entity.getSalesChannel())
                .paymentMethod(entity.getPaymentMethod())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .finishedAt(entity.getFinishedAt())
                .totalMonthlyPremiumAmount(entity.getTotalMonthlyPremiumAmount())
                .insuredAmount(entity.getInsuredAmount())
                .coverages(entity.getCoverages())
                .assistances(entity.getAssistances())
                .history(historicos)
                .build();
    }
}
