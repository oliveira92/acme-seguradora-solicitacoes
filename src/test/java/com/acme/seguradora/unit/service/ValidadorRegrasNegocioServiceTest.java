package com.acme.seguradora.unit.service;

import com.acme.seguradora.application.service.ValidadorRegrasNegocioService;
import com.acme.seguradora.domain.entity.Solicitacao;
import com.acme.seguradora.domain.enums.CategoriaSegurado;
import com.acme.seguradora.domain.enums.ClassificacaoRisco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ValidadorRegrasNegocioServiceTest {

    private ValidadorRegrasNegocioService service;
    private Solicitacao solicitacao;

    @BeforeEach
    void setUp() {
        service = new ValidadorRegrasNegocioService();
        solicitacao = new Solicitacao();
        solicitacao.setId(UUID.randomUUID());
        solicitacao.setCoverages(Map.of("Cobertura", BigDecimal.TEN));
    }

    @ParameterizedTest
    @MethodSource("cenarioValido")
    void deveValidarSolicitacoesDentroDoLimite(ClassificacaoRisco classificacao, CategoriaSegurado categoria, BigDecimal capital) {
        solicitacao.setCategory(categoria);
        solicitacao.setInsuredAmount(capital);

        boolean resultado = service.validarSolicitacao(solicitacao, classificacao);

        assertThat(resultado).isTrue();
    }

    @ParameterizedTest
    @MethodSource("cenarioInvalido")
    void deveReprovarSolicitacoesForaDoLimite(ClassificacaoRisco classificacao, CategoriaSegurado categoria, BigDecimal capital) {
        solicitacao.setCategory(categoria);
        solicitacao.setInsuredAmount(capital);

        boolean resultado = service.validarSolicitacao(solicitacao, classificacao);

        assertThat(resultado).isFalse();
    }

    private static Stream<Arguments> cenarioValido() {
        return Stream.of(
                Arguments.of(ClassificacaoRisco.REGULAR, CategoriaSegurado.AUTO, new BigDecimal("75000.00")),
                Arguments.of(ClassificacaoRisco.REGULAR, CategoriaSegurado.RESIDENCIAL, new BigDecimal("200000.00")),
                Arguments.of(ClassificacaoRisco.REGULAR, CategoriaSegurado.VIDA, new BigDecimal("200000.00")),
                Arguments.of(ClassificacaoRisco.ALTO_RISCO, CategoriaSegurado.AUTO, new BigDecimal("250000.00")),
                Arguments.of(ClassificacaoRisco.ALTO_RISCO, CategoriaSegurado.RESIDENCIAL, new BigDecimal("100000.00")),
                Arguments.of(ClassificacaoRisco.ALTO_RISCO, CategoriaSegurado.VIDA, new BigDecimal("100000.00")),
                Arguments.of(ClassificacaoRisco.PREFERENCIAL, CategoriaSegurado.VIDA, new BigDecimal("800000.00")),
                Arguments.of(ClassificacaoRisco.PREFERENCIAL, CategoriaSegurado.AUTO, new BigDecimal("450000.00")),
                Arguments.of(ClassificacaoRisco.PREFERENCIAL, CategoriaSegurado.RESIDENCIAL, new BigDecimal("450000.00")),
                Arguments.of(ClassificacaoRisco.PREFERENCIAL, CategoriaSegurado.EMPRESARIAL, new BigDecimal("350000.00")),
                Arguments.of(ClassificacaoRisco.SEM_INFO, CategoriaSegurado.VIDA, new BigDecimal("200000.00")),
                Arguments.of(ClassificacaoRisco.SEM_INFO, CategoriaSegurado.RESIDENCIAL, new BigDecimal("200000.00")),
                Arguments.of(ClassificacaoRisco.SEM_INFO, CategoriaSegurado.AUTO, new BigDecimal("75000.00")),
                Arguments.of(ClassificacaoRisco.SEM_INFO, CategoriaSegurado.EMPRESARIAL, new BigDecimal("50000.00"))
        );
    }

    private static Stream<Arguments> cenarioInvalido() {
        return Stream.of(
                Arguments.of(ClassificacaoRisco.REGULAR, CategoriaSegurado.AUTO, new BigDecimal("75000.01")),
                Arguments.of(ClassificacaoRisco.REGULAR, CategoriaSegurado.RESIDENCIAL, new BigDecimal("250000.01")),
                Arguments.of(ClassificacaoRisco.REGULAR, CategoriaSegurado.VIDA, new BigDecimal("255000.01")),
                Arguments.of(ClassificacaoRisco.ALTO_RISCO, CategoriaSegurado.AUTO, new BigDecimal("250000.01")),
                Arguments.of(ClassificacaoRisco.ALTO_RISCO, CategoriaSegurado.RESIDENCIAL, new BigDecimal("150000.01")),
                Arguments.of(ClassificacaoRisco.ALTO_RISCO, CategoriaSegurado.VIDA, new BigDecimal("125000.01")),
                Arguments.of(ClassificacaoRisco.PREFERENCIAL, CategoriaSegurado.VIDA, new BigDecimal("800000.01")),
                Arguments.of(ClassificacaoRisco.PREFERENCIAL, CategoriaSegurado.AUTO, new BigDecimal("450000.01")),
                Arguments.of(ClassificacaoRisco.PREFERENCIAL, CategoriaSegurado.EMPRESARIAL, new BigDecimal("375000.01")),
                Arguments.of(ClassificacaoRisco.SEM_INFO, CategoriaSegurado.VIDA, new BigDecimal("220000.01")),
                Arguments.of(ClassificacaoRisco.SEM_INFO, CategoriaSegurado.RESIDENCIAL, new BigDecimal("220000.01")),
                Arguments.of(ClassificacaoRisco.SEM_INFO, CategoriaSegurado.AUTO, new BigDecimal("75000.01")),
                Arguments.of(ClassificacaoRisco.SEM_INFO, CategoriaSegurado.EMPRESARIAL, new BigDecimal("55000.01"))
        );
    }
}
