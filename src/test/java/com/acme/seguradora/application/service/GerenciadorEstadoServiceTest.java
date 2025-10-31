package com.acme.seguradora.application.service;

import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import com.acme.seguradora.domain.exception.ValidacaoNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o serviço GerenciadorEstadoService.
 * Valida as regras de transição entre estados de uma solicitação.
 */
class GerenciadorEstadoServiceTest {

    private GerenciadorEstadoService gerenciador;

    @BeforeEach
    void setup() {
        gerenciador = new GerenciadorEstadoService();
    }

    @Test
    void devePermitirTransicaoDeReceivedParaValidated() {
        assertThat(gerenciador).isNotNull();
        gerenciador.validarTransicao(EstadoSolicitacao.RECEIVED, EstadoSolicitacao.VALIDATED);
    }

    @Test
    void devePermitirTransicaoDeReceivedParaRejected() {
        assertThat(gerenciador).isNotNull();
        gerenciador.validarTransicao(EstadoSolicitacao.RECEIVED, EstadoSolicitacao.REJECTED);
    }

    @Test
    void devePermitirTransicaoDeValidatedParaPending() {
        assertThat(gerenciador).isNotNull();
        gerenciador.validarTransicao(EstadoSolicitacao.VALIDATED, EstadoSolicitacao.PENDING);
    }

    @Test
    void devePermitirTransicaoDePendingParaApproved() {
        assertThat(gerenciador).isNotNull();
        gerenciador.validarTransicao(EstadoSolicitacao.PENDING, EstadoSolicitacao.APPROVED);
    }

    @Test
    void naoDevePermitirTransicaoDeApprovedParaOutroEstado() {
        assertThatThrownBy(() ->
                gerenciador.validarTransicao(EstadoSolicitacao.APPROVED, EstadoSolicitacao.PENDING))
                .isInstanceOf(ValidacaoNegocioException.class);
    }

    @Test
    void naoDevePermitirTransicaoDeRejectedParaOutroEstado() {
        assertThatThrownBy(() ->
                gerenciador.validarTransicao(EstadoSolicitacao.REJECTED, EstadoSolicitacao.VALIDATED))
                .isInstanceOf(ValidacaoNegocioException.class);
    }

    @Test
    void deveAtualizarEstadoDaSolicitacao() {
        Solicitacao solicitacao = mock(Solicitacao.class);
        when(solicitacao.getStatus()).thenReturn(EstadoSolicitacao.RECEIVED);

        gerenciador.atualizarEstado(solicitacao, EstadoSolicitacao.VALIDATED);

        verify(solicitacao).adicionarHistorico(EstadoSolicitacao.VALIDATED);
    }
}
