package com.acme.seguradora.infrastructure.external.fraude;

import com.acme.seguradora.application.dto.FraudeResponseDto;
import com.acme.seguradora.application.port.FraudeApiPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudeApiClient implements FraudeApiPort {

    private final RestTemplate restTemplate;

    @Value("${fraude.api.url:http://wiremock:8080}")
    private String fraudeApiUrl;

    @Override
    public FraudeResponseDto consultarFraude(UUID solicitacaoId, UUID customerId) {
        try {
            String url = String.format("%s/api/fraude/analisar?orderId=%s&customerId=%s",
                    fraudeApiUrl, solicitacaoId, customerId);

            log.info("Consultando API de fraudes: {}", url);
            FraudeResponseDto response = restTemplate.getForObject(url, FraudeResponseDto.class);
            log.info("Resposta da API de Fraudes: {}", response);

            return response;
        } catch (Exception e) {
            log.error("Erro ao consultar API de Fraudes", e);
            throw new RuntimeException("Erro ao consultar API de Fraudes", e);
        }
    }
}
