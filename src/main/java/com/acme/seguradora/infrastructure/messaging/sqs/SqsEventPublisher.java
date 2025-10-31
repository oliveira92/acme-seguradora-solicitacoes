package com.acme.seguradora.infrastructure.messaging.sqs;

import com.acme.seguradora.application.port.EventPublisherPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsEventPublisher implements EventPublisherPort {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queue-url-prefix:http://localhost:4566/000000000000/}")
    private String queueUrlPrefix;

    @Override
    public void publicar(String topico, Object evento) {
        try {
            String mensagemJson = objectMapper.writeValueAsString(evento);
            String queueUrl = queueUrlPrefix + topico;

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(mensagemJson)
                    .build();

            sqsClient.sendMessage(request);

            log.info("Evento publicado no tópico {}: {}", topico, evento.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Erro ao publicar evento no tópico {}", topico, e);
            throw new RuntimeException("Erro ao publicar evento", e);
        }
    }
}
