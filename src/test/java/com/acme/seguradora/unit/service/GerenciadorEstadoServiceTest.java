package com.acme.seguradora.unit.service;

import com.acme.seguradora.application.service.GerenciadorEstadoService;
import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import com.acme.seguradora.domain.exception.ValidacaoNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GerenciadorEstadoServiceTest {

    private GerenciadorEstadoService service;
    private Solicitacao solicitacao;

    @BeforeEach
    void setUp() {
        service = new GerenciadorEstadoService();
        solicitacao = new Solicitacao();
        solicitacao.setId(UUID.randomUUID());
        solicitacao.setStatus(EstadoSolicitacao.RECEIVED);
    }

    @DisplayName("Deve validar todas as transições permitidas")
    @ParameterizedTest
    @CsvSource({
            "RECEIVED, VALIDATED",
            "RECEIVED, REJECTED",
            "RECEIVED, CANCELED",
            "VALIDATED, PENDING",
            "VALIDATED, CANCELED",
            "PENDING, APPROVED",
            "PENDING, REJECTED"
    })
    void devePermitirTransicoesValidas(EstadoSolicitacao atual, EstadoSolicitacao novo) {
        service.validarTransicao(atual, novo);
    }

    @Test
    void deveLancarExcecaoParaTransicaoInvalida() {
        assertThatThrownBy(() -> service.validarTransicao(EstadoSolicitacao.APPROVED, EstadoSolicitacao.REJECTED))
                .isInstanceOf(ValidacaoNegocioException.class)
                .hasMessageContaining("Transição inválida");
    }

    @Test
    void atualizarEstadoDeveAdicionarHistorico() {
        service.atualizarEstado(solicitacao, EstadoSolicitacao.VALIDATED);

        assertThat(solicitacao.getStatus()).isEqualTo(EstadoSolicitacao.VALIDATED);
        assertThat(solicitacao.getHistory()).hasSize(1);
    }
}
