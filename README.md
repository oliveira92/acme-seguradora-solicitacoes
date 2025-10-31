# ACME Seguradora - Microsserviço de Solicitações de Apólice

## 📋 Visão Geral

Microsserviço para gerenciamento de solicitações de apólice de seguros da ACME Seguradora, desenvolvido com arquitetura orientada a eventos (Event-Driven Architecture - EDA) e seguindo os princípios de Clean Architecture, Clean Code e SOLID.

## 🏗️ Arquitetura

O projeto segue **Clean Architecture** com separação em 4 camadas por pacotes:

```
acme-seguradora-solicitacoes/
├── src/
│   ├── main/
│   │   ├── java/com/acme/seguradora/
│   │   │   ├── domain/              # Camada de Domínio
│   │   │   │   ├── entity/         # Entidades de negócio (Solicitacao, HistoricoEstado)
│   │   │   │   ├── enums/          # Enumerações (EstadoSolicitacao, ClassificacaoRisco, etc)
│   │   │   │   ├── exception/      # Exceções de domínio
│   │   │   │   └── repository/     # Interfaces de repositório
│   │   │   ├── application/         # Camada de Aplicação
│   │   │   │   ├── usecase/        # Casos de uso (Criar, Processar, Consultar, Cancelar)
│   │   │   │   ├── service/        # Serviços (ValidadorRegrasNegocio, GerenciadorEstado)
│   │   │   │   ├── event/          # Eventos de domínio
│   │   │   │   ├── port/           # Portas (EventPublisher, FraudeApi)
│   │   │   │   └── dto/            # DTOs de aplicação
│   │   │   ├── infrastructure/      # Camada de Infraestrutura
│   │   │   │   ├── persistence/    # JPA entities, repositories e adapters
│   │   │   │   ├── messaging/      # SQS publisher e consumer
│   │   │   │   ├── external/       # Clientes de APIs externas (Fraude)
│   │   │   │   └── config/         # Configurações (SQS, Jackson, Scheduling)
│   │   │   └── interfaces/          # Camada de Interface
│   │   │       ├── controller/     # Controllers REST
│   │   │       ├── dto/            # DTOs de request/response
│   │   │       ├── mapper/         # Mapeadores DTO ↔ Entity
│   │   │       ├── exception/      # Exception handlers
│   │   │       └── config/         # Configurações (OpenAPI/Swagger)
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/acme/seguradora/
│           ├── domain/
│           ├── application/
│           ├── infrastructure/
│           └── interfaces/
├── pom.xml                  # Maven único com todas as dependências
├── docker-compose.yml
├── Dockerfile
├── localstack-init/
└── wiremock/
```

### Princípios Aplicados

- **Clean Architecture**: Separação clara de responsabilidades entre camadas
- **SOLID**: 
  - Single Responsibility: Cada classe tem uma única responsabilidade
  - Open/Closed: Extensível via interfaces e abstrações
  - Liskov Substitution: Interfaces bem definidas
  - Interface Segregation: Portas específicas (EventPublisher, FraudeApi)
  - Dependency Inversion: Dependências apontam para abstrações
- **Clean Code**: Código limpo, nomes descritivos, funções pequenas
- **Dependency Inversion**: Uso de ports e adapters (Hexagonal Architecture)

## 🚀 Tecnologias Utilizadas

- **Java 21** - LTS com recursos modernos
- **Spring Boot 3.2.0** - Framework principal
- **Maven** - Gerenciamento de dependências
- **MySQL 8.0** - Banco de dados relacional
- **AWS SDK v2 (SQS)** - Mensageria via LocalStack
- **Wiremock 3.3.1** - Mock da API de Fraudes
- **JPA/Hibernate** - Persistência de dados
- **JUnit 5** - Testes unitários
- **Mockito** - Mocking para testes
- **Testcontainers** - Testes de integração
- **Lombok** - Redução de boilerplate
- **MapStruct** - Mapeamento de objetos (planejado)
- **SpringDoc OpenAPI** - Documentação da API
- **JaCoCo** - Cobertura de código (mínimo 80%)
- **Docker & Docker Compose** - Containerização

## 📊 Ciclo de Vida da Solicitação

O sistema gerencia o seguinte fluxo de estados:

```
RECEIVED → VALIDATED → PENDING → APPROVED
                    ↓           ↓
                REJECTED    REJECTED
                    ↓           ↓
                CANCELED    CANCELED
```

### Estados

- **RECEIVED**: Solicitação criada, aguardando validação
- **VALIDATED**: Aprovada pela API de Fraudes e regras de negócio
- **PENDING**: Aguardando confirmação de pagamento e subscrição
- **APPROVED**: Pagamento e subscrição aprovados (estado final)
- **REJECTED**: Reprovada por validação, pagamento ou subscrição (estado final)
- **CANCELED**: Cancelada pelo cliente (estado final)

### Regras de Transição

- RECEIVED pode ir para: VALIDATED, REJECTED, CANCELED
- VALIDATED pode ir para: PENDING, CANCELED
- PENDING pode ir para: APPROVED, REJECTED, CANCELED, PENDING (reprocessamento)
- APPROVED, REJECTED, CANCELED são estados finais (não permitem transições)

## 🎯 Regras de Negócio

### Classificação de Risco e Limites

A API de Fraudes retorna uma das 4 classificações de risco:

#### 1. Cliente REGULAR
- **Vida/Residencial**: Até R$ 500.000,00
- **Auto**: Até R$ 350.000,00
- **Outros**: Até R$ 255.000,00

#### 2. Cliente HIGH_RISK (Alto Risco)
- **Auto**: Até R$ 250.000,00
- **Residencial**: Até R$ 150.000,00
- **Outros**: Até R$ 125.000,00

#### 3. Cliente PREFERENTIAL (Preferencial)
- **Vida**: Até R$ 800.000,00 (exclusivo)
- **Auto/Residencial**: Até R$ 450.000,00
- **Outros**: Até R$ 375.000,00

#### 4. Cliente NO_INFO (Sem Informação)
- **Vida/Residencial**: Até R$ 200.000,00
- **Auto**: Até R$ 75.000,00
- **Outros**: Até R$ 55.000,00

### Regras de Cancelamento

- ✅ Permitido: RECEIVED, VALIDATED, PENDING
- ❌ Bloqueado: APPROVED, REJECTED

## 🔌 API REST

### Base URL
```
http://localhost:8090
```

### Swagger UI
```
http://localhost:8090/swagger-ui.html
```

### Endpoints

#### 1. Criar Solicitação
```http
POST /orders
Content-Type: application/json

{
  "customer_id": "adc56d77-348c-4bf0-908f-22d402ee715c",
  "product_id": "1b2da7cc-b367-4196-8a78-9cfeec21f587",
  "category": "AUTO",
  "salesChannel": "MOBILE",
  "paymentMethod": "CREDIT_CARD",
  "total_monthly_premium_amount": 75.25,
  "insured_amount": 275000.50,
  "coverages": {
    "Roubo": 100000.25,
    "Perda Total": 100000.25,
    "Colisão com Terceiros": 75000.00
  },
  "assistances": [
    "Guincho até 250km",
    "Troca de Óleo",
    "Chaveiro 24h"
  ]
}
```

**Response (201 Created):**
```json
{
  "id": "89846cee-c6d5-4320-92e9-16e122d5c672",
  "createdAt": "2024-10-30T14:00:00Z"
}
```

#### 2. Consultar Solicitação por ID
```http
GET /orders/{id}
```

**Response (200 OK):**
```json
{
  "id": "89846cee-c6d5-4320-92e9-16e122d5c672",
  "customer_id": "adc56d77-348c-4bf0-908f-22d402ee715c",
  "product_id": "1b2da7cc-b367-4196-8a78-9cfeec21f587",
  "category": "AUTO",
  "salesChannel": "MOBILE",
  "paymentMethod": "CREDIT_CARD",
  "status": "APPROVED",
  "createdAt": "2024-10-30T14:00:00Z",
  "finishedAt": "2024-10-30T14:05:30Z",
  "total_monthly_premium_amount": 75.25,
  "insured_amount": 275000.50,
  "coverages": {
    "Roubo": 100000.25,
    "Perda Total": 100000.25,
    "Colisão com Terceiros": 75000.00
  },
  "assistances": [
    "Guincho até 250km",
    "Troca de Óleo",
    "Chaveiro 24h"
  ],
  "history": [
    {
      "status": "RECEIVED",
      "timestamp": "2024-10-30T14:00:00Z"
    },
    {
      "status": "VALIDATED",
      "timestamp": "2024-10-30T14:00:30Z"
    },
    {
      "status": "PENDING",
      "timestamp": "2024-10-30T14:01:12Z"
    },
    {
      "status": "APPROVED",
      "timestamp": "2024-10-30T14:02:15Z"
    }
  ]
}
```

#### 3. Consultar Solicitações por Cliente
```http
GET /orders?customer_id={customer_id}
```

**Response (200 OK):** Array de solicitações

#### 4. Cancelar Solicitação
```http
DELETE /orders/{id}
```

**Response (204 No Content):** Sucesso

**Response (422 Unprocessable Entity):** Cancelamento não permitido

## 📨 Eventos e Mensageria

### Filas SQS Utilizadas

#### Eventos Publicados (Producers)
1. **solicitacao-recebida** - Quando uma solicitação é criada
2. **solicitacao-validada** - Após validação bem-sucedida
3. **solicitacao-pendente** - Quando passa para estado PENDING
4. **solicitacao-aprovada** - Quando aprovada
5. **solicitacao-rejeitada** - Quando rejeitada
6. **solicitacao-cancelada** - Quando cancelada

#### Eventos Consumidos (Consumers)
1. **solicitacao-recebida** - Processa validação via API de Fraudes
2. **pagamento-confirmado** - Processa confirmação de pagamento
3. **subscricao-autorizada** - Processa autorização de subscrição

### Exemplo de Evento
```json
{
  "solicitacaoId": "89846cee-c6d5-4320-92e9-16e122d5c672",
  "customerId": "adc56d77-348c-4bf0-908f-22d402ee715c",
  "timestamp": "2024-10-30T14:00:00Z"
}
```

## 🔍 API de Fraudes (Mock via Wiremock)

### Endpoint
```http
GET /api/fraude/analisar?orderId={orderId}&customerId={customerId}
```

### Response Mockado
```json
{
  "orderId": "e053467f-bd48-4fd2-9481-75bd4e88ee40",
  "customerId": "7c2a27ba-71ef-4dd8-a3cf-5e094316ffd8",
  "analyzedAt": "2024-05-10T12:00:00Z",
  "classification": "REGULAR",
  "occurrences": []
}
```

O Wiremock retorna aleatoriamente uma das classificações: REGULAR, HIGH_RISK, PREFERENTIAL, NO_INFO

## 🐳 Execução com Docker Compose

### Pré-requisitos
- Docker 20.10+
- Docker Compose 2.0+
- Pelo menos 4GB de RAM disponível

### Iniciar Todos os Serviços

```bash
cd ~/repos/acme-seguradora-solicitacoes
docker-compose up -d
```

### Verificar Status dos Serviços

```bash
docker-compose ps
```

Serviços disponíveis:
- **mysql**: MySQL 8.0 na porta 3306
- **localstack**: LocalStack (SQS) na porta 4566
- **wiremock**: Wiremock (API de Fraudes) na porta 8080
- **app**: Aplicação Spring Boot na porta 8090

### Visualizar Logs

```bash
# Todos os serviços
docker-compose logs -f

# Serviço específico
docker-compose logs -f app
docker-compose logs -f mysql
docker-compose logs -f localstack
docker-compose logs -f wiremock
```

### Parar os Serviços

```bash
docker-compose down
```

### Limpar Volumes (Reiniciar do Zero)

```bash
docker-compose down -v
```

## 🛠️ Execução Local (Sem Docker)

### Pré-requisitos
- Java 21 JDK
- Maven 3.8+
- MySQL 8.0 rodando localmente
- LocalStack rodando localmente (opcional, para testes com SQS)

### 1. Configurar Banco de Dados

```sql
CREATE DATABASE acme_seguros;
CREATE USER 'acme_user'@'localhost' IDENTIFIED BY 'acme_password';
GRANT ALL PRIVILEGES ON acme_seguros.* TO 'acme_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Construir o Projeto

```bash
cd ~/repos/acme-seguradora-solicitacoes
mvn clean install
```

### 3. Executar a Aplicação

```bash
mvn spring-boot:run
```

Ou executar o JAR:

```bash
java -jar target/acme-seguradora-solicitacoes-1.0.0.jar
```

### 4. Configurar Variáveis de Ambiente (Opcional)

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=acme_seguros
export DB_USER=acme_user
export DB_PASSWORD=acme_password
export AWS_SQS_ENDPOINT=http://localhost:4566
export FRAUDE_API_URL=http://localhost:8080
```

## 🧪 Testes

### Executar Todos os Testes

```bash
mvn test
```

### Gerar Relatório de Cobertura (JaCoCo)

```bash
mvn clean test jacoco:report
```

Relatório gerado em:
- `target/site/jacoco/index.html`

### Meta de Cobertura

O projeto está configurado para exigir **mínimo de 80% de cobertura de linha** via JaCoCo.

### Tipos de Testes Implementados

1. **Testes Unitários**
   - Testes de entidades de domínio
   - Testes de casos de uso
   - Testes de serviços
   - Testes de validadores

2. **Testes de Integração**
   - Testes com Testcontainers (MySQL)
   - Testes de persistência completos
   - Testes de controllers com MockMvc

3. **Testes de Contrato**
   - Testes de mapeamento DTO ↔ Entity
   - Validação de contratos da API

## 📊 Observabilidade

### Métricas e Health Check

```bash
# Health Check
curl http://localhost:8090/actuator/health

# Métricas Prometheus
curl http://localhost:8090/actuator/prometheus

# Informações da Aplicação
curl http://localhost:8090/actuator/info
```

### Logs

Os logs são configurados com padrão estruturado e níveis apropriados:

- **INFO**: Operações importantes (criação, consulta, cancelamento)
- **WARN**: Situações de atenção
- **ERROR**: Erros e exceções

Exemplo de log:
```
2024-10-30 14:00:00 - Recebendo solicitação de criação de apólice para customer_id: adc56d77...
2024-10-30 14:00:01 - Solicitação criada com sucesso. ID: 89846cee...
```

## 🔐 Segurança

### Validações Implementadas

1. **Validação de Entrada**: Bean Validation (JSR 380) em todos os DTOs
2. **Tratamento de Exceções**: GlobalExceptionHandler para respostas consistentes
3. **Validação de Estados**: Transições de estado validadas pelo GerenciadorEstado
4. **Validação de Negócio**: Regras de capital segurado por classificação

### Melhorias Futuras (Não Implementadas)

- Autenticação e Autorização (Spring Security / OAuth2)
- Rate Limiting
- API Keys
- Criptografia de dados sensíveis
- Audit Trail completo

## 🚦 Fluxo Completo de uma Solicitação

1. **Cliente cria solicitação** via `POST /orders`
   - Sistema persiste com estado RECEIVED
   - Publica evento `solicitacao-recebida`

2. **Consumer processa evento recebido**
   - Consulta API de Fraudes (Wiremock)
   - Valida regras de negócio conforme classificação
   - Se aprovado: muda para VALIDATED → PENDING
   - Se reprovado: muda para REJECTED
   - Publica eventos correspondentes

3. **Sistemas externos confirmam pagamento**
   - Publicam evento `pagamento-confirmado`
   - Consumer processa e mantém PENDING ou muda para REJECTED

4. **Sistemas externos autorizam subscrição**
   - Publicam evento `subscricao-autorizada`
   - Consumer processa e muda para APPROVED ou REJECTED

5. **Cliente pode consultar** via `GET /orders/{id}`
   - Visualiza estado atual e histórico completo

6. **Cliente pode cancelar** via `DELETE /orders/{id}`
   - Só permitido se não estiver APPROVED ou REJECTED

## 💡 Decisões Técnicas

### Por que Clean Architecture?
- Separação clara de responsabilidades
- Facilita testes unitários (camadas independentes)
- Permite mudança de frameworks sem afetar regras de negócio
- Melhora manutenibilidade a longo prazo

### Por que Event-Driven Architecture?
- Desacoplamento entre serviços
- Escalabilidade horizontal
- Resiliência (retry automático via SQS)
- Rastreabilidade de mudanças de estado

### Por que Separação em Pacotes (Clean Architecture)?
- Enforça separação de responsabilidades entre camadas
- Mantém independência de frameworks e bibliotecas
- Facilita testes unitários e de integração
- Permite evolução e manutenção independente de cada camada
- Melhora organização e clareza do código

### Por que Testcontainers?
- Testes de integração com infraestrutura real (MySQL)
- Evita mocks complexos de banco de dados
- Garante comportamento idêntico ao ambiente produtivo
- Execução reproduzível

### Por que LocalStack para SQS?
- Desenvolvimento local sem custos AWS
- Mesma interface do SQS real
- Facilita testes de integração
- Rápido para desenvolver e iterar

## 🐛 Troubleshooting

### Problema: Aplicação não conecta ao MySQL
```
Solução: Verificar se o MySQL está rodando e as credenciais estão corretas
docker-compose ps mysql
docker-compose logs mysql
```

### Problema: Filas SQS não são criadas
```
Solução: Verificar se o script de inicialização do LocalStack executou
docker-compose logs localstack
docker exec -it acme-localstack awslocal sqs list-queues
```

### Problema: API de Fraudes retorna 404
```
Solução: Verificar se o Wiremock iniciou corretamente
curl http://localhost:8080/__admin/mappings
```

### Problema: Testes falhando
```
Solução: Garantir que o Docker está rodando (para Testcontainers)
docker info
mvn clean test -DskipTests=false
```

## 📝 Premissas e Limitações

### Premissas
- API de Fraudes sempre disponível (mockada)
- Eventos de pagamento e subscrição são publicados por sistemas externos
- UUIDs são gerados automaticamente pelo sistema
- Timestamps em UTC
- Capital segurado em BRL (Real Brasileiro)

### Limitações Conhecidas
- Não há autenticação/autorização implementada
- Não há retry policy configurada para chamadas à API de Fraudes
- Não há implementação de Circuit Breaker
- Histórico de estados não tem limite de tamanho
- Não há paginação nos endpoints de consulta
- Não há versionamento da API

### Não Implementado (Por Escopo)
- Serviços de Pagamento, Subscrição e Notificação (apenas mocks esperados)
- Parametrização de impostos
- Persistência de campos além dos especificados
- Deploy em cloud (apenas local/Docker)
- Pipeline CI/CD
- Monitoramento avançado (APM, distributed tracing)

## 📚 Referências

- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design - Eric Evans](https://www.domainlanguage.com/ddd/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/)
- [Testcontainers](https://www.testcontainers.org/)
- [Testing Strategies in a Microservice Architecture](https://martinfowler.com/articles/microservice-testing/)

## 👥 Autor

Fernando Andrade Oliveira - fernando.a.oliveira-santos@itau-unibanco.com.br

**Desenvolvido para**: ACME Seguradora  
**Data**: Outubro de 2024  
**Versão**: 1.0.0
