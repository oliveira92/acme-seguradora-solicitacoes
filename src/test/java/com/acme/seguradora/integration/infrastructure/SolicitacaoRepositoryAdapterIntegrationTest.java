package com.acme.seguradora.integration.infrastructure;

import com.acme.seguradora.domain.entity.HistoricoEstado;
import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.enums.CanalVenda;
import com.acme.seguradora.domain.enums.CategoriaSegurado;
import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import com.acme.seguradora.domain.enums.MetodoPagamento;
import com.acme.seguradora.infrastructure.persistence.adapter.SolicitacaoRepositoryAdapter;
import com.acme.seguradora.infrastructure.persistence.entity.HistoricoEstadoEntity;
import com.acme.seguradora.infrastructure.persistence.entity.SolicitacaoEntity;
import com.acme.seguradora.infrastructure.persistence.repository.SolicitacaoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitacaoRepositoryAdapterIntegrationTest {

    @Mock
    private SolicitacaoJpaRepository jpaRepository;

    @InjectMocks
    private SolicitacaoRepositoryAdapter adapter;

    private Solicitacao solicitacao;
    private SolicitacaoEntity entity;

    @BeforeEach
    void setUp() {
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();

        solicitacao = Solicitacao.builder()
                .id(id)
                .customerId(customerId)
                .productId(productId)
                .category(CategoriaSegurado.AUTO)
                .saleChannel(CanalVenda.MOBILE)
                .paymentMethod(MetodoPagamento.CREDIT_CARD)
                .status(EstadoSolicitacao.RECEIVED)
                .createdAt(agora)
                .finishedAt(null)
                .totalMonthlyPremiumAmount(new BigDecimal("120.50"))
                .insuredAmount(new BigDecimal("50000.00"))
                .coverages(Map.of("Cobertura", new BigDecimal("50000.00")))
                .assistances(List.of("Assistência 24h"))
                .history(List.of(HistoricoEstado.builder()
                        .status(EstadoSolicitacao.RECEIVED)
                        .timestamp(agora)
                        .build()))
                .build();

        entity = SolicitacaoEntity.builder()
                .id(id)
                .customerId(customerId)
                .productId(productId)
                .category(CategoriaSegurado.AUTO)
                .salesChannel(CanalVenda.WHATSAPP)
                .paymentMethod(MetodoPagamento.CREDIT_CARD)
                .status(EstadoSolicitacao.RECEIVED)
                .createdAt(agora)
                .finishedAt(null)
                .totalMonthlyPremiumAmount(new BigDecimal("120.50"))
                .insuredAmount(new BigDecimal("50000.00"))
                .coverages(Map.of("Cobertura", new BigDecimal("50000.00")))
                .assistances(List.of("Assistência 24h"))
                .history(List.of(HistoricoEstadoEntity.builder()
                        .status(EstadoSolicitacao.RECEIVED)
                        .timestamp(agora)
                        .build()))
                .build();

        entity.getHistory().forEach(h -> h.setSolicitacao(entity));
    }

    @Test
    void deveSalvarEConverterDeDominioParaEntidade() {
        when(jpaRepository.save(any(SolicitacaoEntity.class))).thenReturn(entity);

        Solicitacao resultado = adapter.salvar(solicitacao);

        assertThat(resultado.getId()).isEqualTo(solicitacao.getId());
        assertThat(resultado.getHistory()).hasSize(1);
        assertThat(resultado.getHistory().getFirst().getStatus()).isEqualTo(EstadoSolicitacao.RECEIVED);
    }

    @Test
    void deveBuscarPorIdEConverterParaDominio() {
        when(jpaRepository.findById(solicitacao.getId())).thenReturn(Optional.of(entity));

        Optional<Solicitacao> resultado = adapter.buscarPorId(solicitacao.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getAssistances()).containsExactly("Assistência 24h");
    }

    @Test
    void deveBuscarPorCustomerIdEConverterLista() {
        when(jpaRepository.findByCustomerId(solicitacao.getCustomerId())).thenReturn(List.of(entity));

        List<Solicitacao> resultado = adapter.buscarPorCustomerId(solicitacao.getCustomerId());

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getCoverages()).containsKey("Cobertura");
    }
}
