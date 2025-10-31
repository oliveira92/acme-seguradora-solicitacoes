#!/bin/bash
set -e

echo "🚀 Inicializando filas SQS no LocalStack..."

# Criação das filas SQS utilizadas pelo microsserviço de solicitações de apólice
awslocal sqs create-queue --queue-name solicitacao-recebida
awslocal sqs create-queue --queue-name solicitacao-validada
awslocal sqs create-queue --queue-name solicitacao-pendente
awslocal sqs create-queue --queue-name solicitacao-aprovada
awslocal sqs create-queue --queue-name solicitacao-rejeitada
awslocal sqs create-queue --queue-name solicitacao-cancelada
awslocal sqs create-queue --queue-name pagamento-confirmado
awslocal sqs create-queue --queue-name subscricao-autorizada

echo "✅ Filas SQS criadas com sucesso!"