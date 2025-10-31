package com.acme.seguradora.application.service;

import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.enums.CategoriaSegurado;
import com.acme.seguradora.domain.enums.ClassificacaoRisco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o ValidadorRegrasNegocioService.
 * Valida o comportamento de aprovação/rejeição de solicitações conforme:
 * - Categoria do segurado (VIDA, AUTO, etc)
 * - Classificação de risco (REGULAR, ALTO_RISCO, etc)
 * - Valor segurado
 */
class ValidadorRegrasNegocioServiceTest {

    private ValidadorRegrasNegocioService validador;

    @BeforeEach
    void setup() {
        validador = new ValidadorRegrasNegocioService();
    }

    @Test
    void deveValidarSeguradoComCapitalVidaDentroLimite() {
        Solicitacao solicitacao = mock(Solicitacao.class);
        when(solicitacao.getInsuredAmount()).thenReturn(new BigDecimal("40000.00"));
        when(solicitacao.getCategory()).thenReturn(CategoriaSegurado.VIDA);

        boolean resultado = validador.validarSolicitacao(solicitacao, ClassificacaoRisco.REGULAR);

        assertThat(resultado).isTrue();
    }

    @Test
    void deveReprovarSeguradoComCapitalVidaAcimaLimite() {
        Solicitacao solicitacao = mock(Solicitacao.class);
        when(solicitacao.getInsuredAmount()).thenReturn(new BigDecimal("60000.00"));
        when(solicitacao.getCategory()).thenReturn(CategoriaSegurado.VIDA);

        boolean resultado = validador.validarSolicitacao(solicitacao, ClassificacaoRisco.REGULAR);

        assertThat(resultado).isTrue();
    }

    @Test
    void deveValidarClienteAutoComCapitalDentroLimite() {
        Solicitacao solicitacao = mock(Solicitacao.class);
        when(solicitacao.getInsuredAmount()).thenReturn(new BigDecimal("90000.00"));
        when(solicitacao.getCategory()).thenReturn(CategoriaSegurado.AUTO);

        boolean resultado = validador.validarSolicitacao(solicitacao, ClassificacaoRisco.ALTO_RISCO);

        assertThat(resultado).isTrue();
    }

    @Test
    void deveValidarClientePreferencialComCapitalVidaDentroLimite() {
        Solicitacao solicitacao = mock(Solicitacao.class);
        when(solicitacao.getInsuredAmount()).thenReturn(new BigDecimal("70000.00"));
        when(solicitacao.getCategory()).thenReturn(CategoriaSegurado.VIDA);

        boolean resultado = validador.validarSolicitacao(solicitacao, ClassificacaoRisco.PREFERENCIAL);

        assertThat(resultado).isTrue();
    }

    @Test
    void deveReprovarClienteSemInformacaoComCapitalAcimaLimite() {
        Solicitacao solicitacao = mock(Solicitacao.class);
        when(solicitacao.getInsuredAmount()).thenReturn(new BigDecimal("90000.00"));
        when(solicitacao.getCategory()).thenReturn(CategoriaSegurado.AUTO);

        boolean resultado = validador.validarSolicitacao(solicitacao, ClassificacaoRisco.SEM_INFO);

        assertThat(resultado).isFalse();
    }

    @Test
    void deveAprovarClienteSemInformacaoComCapitalDentroLimite() {
        Solicitacao solicitacao = mock(Solicitacao.class);
        when(solicitacao.getInsuredAmount()).thenReturn(new BigDecimal("50000.00"));
        when(solicitacao.getCategory()).thenReturn(CategoriaSegurado.AUTO);

        boolean resultado = validador.validarSolicitacao(solicitacao, ClassificacaoRisco.SEM_INFO);

        assertThat(resultado).isTrue();
    }
}
