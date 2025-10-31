package com.acme.seguradora.unit.domain;

import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.enums.CategoriaSegurado;
import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitacaoTest {

    private Solicitacao solicitacao;

    @BeforeEach
    void setUp() {
        solicitacao = Solicitacao.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .category(CategoriaSegurado.AUTO)
                .status(EstadoSolicitacao.RECEIVED)
                .createdAt(LocalDateTime.now())
                .totalMonthlyPremiumAmount(new BigDecimal("100.00"))
                .insuredAmount(new BigDecimal("1000.00"))
                .coverages(Map.of("Cobertura A", new BigDecimal("500.00")))
                .assistances(List.of("Assistencia A"))
                .build();
    }

    @Test
    void adicionarHistoricoDeveAtualizarStatusEAdicionarRegistro() {
        solicitacao.adicionarHistorico(EstadoSolicitacao.VALIDATED);

        assertThat(solicitacao.getStatus()).isEqualTo(EstadoSolicitacao.VALIDATED);
        assertThat(solicitacao.getHistory()).hasSize(1);
        assertThat(solicitacao.getHistory().getFirst().getStatus()).isEqualTo(EstadoSolicitacao.VALIDATED);
        assertThat(solicitacao.getHistory().getFirst().getTimestamp()).isNotNull();
        assertThat(solicitacao.getFinishedAt()).isNull();
    }

    @Test
    void adicionarHistoricoComEstadoFinalDevePreencherFinishedAt() {
        solicitacao.adicionarHistorico(EstadoSolicitacao.APPROVED);

        assertThat(solicitacao.getFinishedAt()).isNotNull();
        assertThat(solicitacao.estaFinalizada()).isTrue();
    }

    @Test
    void podeSerCanceladaSomenteQuandoAprovada() {
        solicitacao.setStatus(EstadoSolicitacao.APPROVED);
        assertThat(solicitacao.podeSerCancelada()).isTrue();

        solicitacao.setStatus(EstadoSolicitacao.REJECTED);
        assertThat(solicitacao.podeSerCancelada()).isFalse();
    }

    @Test
    void estaFinalizadaDeveDetectarEstadosFinais() {
        solicitacao.setStatus(EstadoSolicitacao.APPROVED);
        assertThat(solicitacao.estaFinalizada()).isTrue();

        solicitacao.setStatus(EstadoSolicitacao.CANCELED);
        assertThat(solicitacao.estaFinalizada()).isTrue();

        solicitacao.setStatus(EstadoSolicitacao.RECEIVED);
        assertThat(solicitacao.estaFinalizada()).isFalse();
    }
}
