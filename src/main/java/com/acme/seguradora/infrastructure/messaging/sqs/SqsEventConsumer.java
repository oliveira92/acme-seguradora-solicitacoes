package com.acme.seguradora.infrastructure.messaging.sqs;

import com.acme.seguradora.application.usecase.ProcessarEventoPagamentoUseCase;
import com.acme.seguradora.application.usecase.ProcessarEventoSubscricaoUseCase;
import com.acme.seguradora.application.usecase.ProcessarSolicitacaoUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsEventConsumer {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final ProcessarSolicitacaoUseCase processarSolicitacaoUseCase;
    private final ProcessarEventoPagamentoUseCase processarEventoPagamentoUseCase;
    private final ProcessarEventoSubscricaoUseCase processarEventoSubscricaoUseCase;

    @Value("${aws.sqs.queue-url-prefix:http://localhost:4566/000000000000/}")
    private String queueUrlPrefix;

    @Scheduled(fixedDelay = 5000)
    public void consumirMensagensSolicitacao() {
        consumirMensagens("solicitacao-recebida", this::processarEventoSolicitacao);
    }

    @Scheduled(fixedDelay = 5000)
    public void consumirMensagensPagamento() {
        consumirMensagens("pagamento-aprovado", this::processarEventoPagamento);
    }

    @Scheduled(fixedDelay = 5000)
    public void consumirMensagensSubscricao() {
        consumirMensagens("subscricao-aprovada", this::processarEventoSubscricao);
    }

    private void consumirMensagens(String queueName, MessageProcessor processor) {
        String queueUrl = queueUrlPrefix + queueName;

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .waitTimeSeconds(5)
                .build();

        try {
            List<Message> messages = sqsClient.receiveMessage(request).messages();

            for (Message message : messages) {
                try {
                    processor.process(message.body());

                    DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build();
                    sqsClient.deleteMessage(deleteRequest);
                } catch (Exception e) {
                    log.error("Erro ao processar mensagem da fila {}: {}", queueName, e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Erro ao consumir mensagens da fila {}: {}", queueName, e.getMessage(), e);
        }
    }

    private void processarEventoSolicitacao(String message) throws Exception {
        UUID solicitacaoId = UUID.fromString(objectMapper.readTree(message).get("solicitacaoId").asText());
        processarSolicitacaoUseCase.executar(solicitacaoId);
    }

    private void processarEventoPagamento(String message) throws Exception {
        var json = objectMapper.readTree(message);
        UUID solicitacaoId = UUID.fromString(json.get("solicitacaoId").asText());
        boolean aprovado = json.get("aprovado").asBoolean();
        processarEventoPagamentoUseCase.executar(solicitacaoId, aprovado);
    }

    private void processarEventoSubscricao(String message) throws Exception {
        var json = objectMapper.readTree(message);
        UUID solicitacaoId = UUID.fromString(json.get("solicitacaoId").asText());
        boolean aprovado = json.get("aprovado").asBoolean();
        processarEventoSubscricaoUseCase.executar(solicitacaoId, aprovado);
    }

    @FunctionalInterface
    private interface MessageProcessor {
        void process(String message) throws Exception;
    }
}
