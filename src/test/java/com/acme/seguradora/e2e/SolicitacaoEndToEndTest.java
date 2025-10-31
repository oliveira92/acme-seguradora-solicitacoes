package com.acme.seguradora.e2e;

import com.acme.seguradora.application.dto.FraudeResponseDto;
import com.acme.seguradora.application.port.EventPublisherPort;
import com.acme.seguradora.application.port.FraudeApiPort;
import com.acme.seguradora.domain.enums.CanalVenda;
import com.acme.seguradora.domain.enums.CategoriaSegurado;
import com.acme.seguradora.domain.enums.ClassificacaoRisco;
import com.acme.seguradora.domain.enums.EstadoSolicitacao;
import com.acme.seguradora.domain.enums.MetodoPagamento;
import com.acme.seguradora.domain.repository.SolicitacaoRepository;
import com.acme.seguradora.interfaces.dto.request.CriarSolicitacaoRequestDto;
import com.acme.seguradora.interfaces.dto.response.CriarSolicitacaoResponseDto;
import com.acme.seguradora.interfaces.dto.response.SolicitacaoResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.TestConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SolicitacaoEndToEndTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private InMemoryEventPublisher eventPublisher;

    @Autowired
    private StubFraudeApiClient fraudeApiClient;

    @Test
    void deveExecutarFluxoCompletoDeSolicitacao() {
        CriarSolicitacaoRequestDto request = CriarSolicitacaoRequestDto.builder()
                .customer_id(UUID.randomUUID())
                .product_id(UUID.randomUUID())
                .category(CategoriaSegurado.AUTO)
                .salesChannel(CanalVenda.APP)
                .paymentMethod(MetodoPagamento.PIX)
                .total_monthly_premium_amount(new BigDecimal("100.00"))
                .insured_amount(new BigDecimal("50000.00"))
                .coverages(Map.of("Colisão", new BigDecimal("50000.00")))
                .assistances(List.of("Guincho"))
                .build();

        ResponseEntity<CriarSolicitacaoResponseDto> postResponse = restTemplate.postForEntity(
                url("/orders"),
                request,
                CriarSolicitacaoResponseDto.class
        );

        assertThat(postResponse.getStatusCode().value()).isEqualTo(201);
        assertThat(postResponse.getBody()).isNotNull();
        UUID solicitacaoId = postResponse.getBody().getId();
        assertThat(eventPublisher.publicacoes).hasSize(1);
        assertThat(eventPublisher.publicacoes.getFirst().topico()).isEqualTo("solicitacao-recebida");

        ResponseEntity<SolicitacaoResponseDto> getResponse = restTemplate.getForEntity(
                url("/orders/" + solicitacaoId),
                SolicitacaoResponseDto.class
        );

        assertThat(getResponse.getStatusCode().value()).isEqualTo(200);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(solicitacaoId);

        ResponseEntity<SolicitacaoResponseDto[]> listResponse = restTemplate.getForEntity(
                url("/orders?customer_id=" + request.getCustomer_id()),
                SolicitacaoResponseDto[].class
        );

        assertThat(listResponse.getStatusCode().value()).isEqualTo(200);
        assertThat(listResponse.getBody()).isNotNull();
        assertThat(listResponse.getBody()).hasSize(1);

        // Simula processamento de fraude aprovando a solicitação
        fraudeApiClient.resposta = FraudeResponseDto.builder()
                .classification(ClassificacaoRisco.REGULAR)
                .build();

        solicitacaoRepository.buscarPorId(solicitacaoId).ifPresent(solicitacao -> {
            solicitacao.adicionarHistorico(EstadoSolicitacao.APPROVED);
            solicitacaoRepository.salvar(solicitacao);
        });

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                url("/orders/" + solicitacaoId),
                HttpMethod.DELETE,
                new HttpEntity<>(new HttpHeaders()),
                Void.class
        );

        assertThat(deleteResponse.getStatusCode().value()).isEqualTo(204);
        assertThat(eventPublisher.publicacoes.stream().map(Publicacao::topico))
                .contains("solicitacao-cancelada");
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        @Primary
        InMemoryEventPublisher eventPublisher() {
            return new InMemoryEventPublisher();
        }

        @Bean
        @Primary
        StubFraudeApiClient fraudeApiPort() {
            return new StubFraudeApiClient();
        }
    }

    static class InMemoryEventPublisher implements EventPublisherPort {
        private final List<Publicacao> publicacoes = new CopyOnWriteArrayList<>();

        @Override
        public void publicar(String topico, Object evento) {
            publicacoes.add(new Publicacao(topico, evento));
        }
    }

    static class StubFraudeApiClient implements FraudeApiPort {
        private FraudeResponseDto resposta = FraudeResponseDto.builder()
                .classification(ClassificacaoRisco.REGULAR)
                .build();

        @Override
        public FraudeResponseDto consultarFraude(UUID solicitacaoId, UUID customerId) {
            return resposta;
        }
    }

    record Publicacao(String topico, Object payload) {
    }
}
