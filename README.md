# MonetraAPI - Sistema de Gestão Financeira

API REST para gerenciamento financeiro pessoal com suporte a múltiplas contas, transações, categorias e análises de despesas.

## Arquitetura de Microserviços

O projeto implementa uma arquitetura de **3 microserviços** conforme o diagrama:

```
                           ┌─────────────────┐
                           │    Cliente      │
                           │  (Postman/App)  │
                           └────────┬────────┘
                                    │
            ┌───────────────────────┼───────────────────────┐
            │                       │                       │
            ▼                       ▼                       ▼
    ┌───────────────┐      ┌───────────────┐      ┌───────────────┐
    │  MS1 - User   │      │MS2 - Transaction│    │ MS3 - Processor│
    │   Service     │      │    Service     │      │   Service     │
    │   :8081       │      │    :8082       │      │    :8083      │
    ├───────────────┤      ├───────────────┤      ├───────────────┤
    │ • Auth/Login  │      │ • CRUD Trans  │      │ • Kafka       │
    │ • CRUD Users  │      │ • Kafka Prod  │      │   Consumer    │
    │ • Excel Import│      │ • Câmbio API  │      │ • Validação   │
    │ • Migrations  │      │ • Análises    │      │   Saldo       │
    └───────┬───────┘      └───────┬───────┘      └───────┬───────┘
            │                      │                      │
            │                      │    ┌─────────────────┤
            │                      │    │                 │
            ▼                      ▼    ▼                 ▼
    ┌───────────────────────────────────────┐    ┌───────────────┐
    │           PostgreSQL :5432            │    │   MockAPI     │
    │              (monetra)                │    │    :3001      │
    └───────────────────────────────────────┘    └───────────────┘
                           │
                           │
            ┌──────────────┴──────────────┐
            │                             │
            ▼                             ▼
    ┌───────────────┐            ┌───────────────┐
    │    Kafka      │            │   BrasilAPI   │
    │    :9092      │            │   (Câmbio)    │
    └───────────────┘            └───────────────┘
```

### Responsabilidades dos Serviços

| Serviço | Porta | Responsabilidades |
|---------|-------|-------------------|
| **MS1 - User Service** | 8081 | Autenticação JWT, CRUD usuários, Upload Excel, Migrations Flyway |
| **MS2 - Transaction Service** | 8082 | CRUD transações, Categorias, Contas, Análises, Producer Kafka, Câmbio |
| **MS3 - Processor Service** | 8083 | Consumer Kafka, Validação de saldo, Integração MockAPI, DLQ |

### Fluxo de Transações com Kafka

```
1. Cliente → POST /transactions (MS2:8082)
            ↓
2. Transação salva (status=PENDING)
            ↓
3. Evento publicado → topic: transaction.requested
            ↓
4. MS3 - TransactionProcessor (Consumer)
            ↓
5. Consulta saldo (interno + MockAPI para contas tipo BANCO)
            ↓
6. Atualiza status → APPROVED ou REJECTED
            ↓
7. Em caso de erro → transaction.dlq (Dead Letter Queue)
```

### Clean Architecture (Interna)

Cada serviço segue **Clean Architecture** internamente:

```
┌─────────────────────────────────────────────────────────────┐
│                      INFRAESTRUTURA                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │ Controllers │  │   Kafka     │  │    Persistence      │ │
│  │   (REST)    │  │ (Producer/  │  │  (JPA/PostgreSQL)   │ │
│  │             │  │  Consumer)  │  │                     │ │
│  └──────┬──────┘  └──────┬──────┘  └──────────┬──────────┘ │
└─────────┼────────────────┼─────────────────────┼───────────┘
          │                │                     │
┌─────────▼────────────────▼─────────────────────▼───────────┐
│                       APPLICATION                           │
│  ┌─────────────────────┐  ┌──────────────────────────────┐ │
│  │      Use Cases      │  │          Gateways            │ │
│  └──────────┬──────────┘  └──────────────────────────────┘ │
└─────────────┼──────────────────────────────────────────────┘
              │
┌─────────────▼──────────────────────────────────────────────┐
│                         DOMAIN                              │
│  ┌──────────┐  ┌───────────┐  ┌──────────┐  ┌───────────┐ │
│  │   User   │  │  Account  │  │Transaction│  │ Category  │ │
│  └──────────┘  └───────────┘  └──────────┘  └───────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## Stack Tecnológica

- **Java 17**
- **Spring Boot 4.0.1**
- **Spring Security + JWT**
- **Spring Data JPA**
- **Spring Kafka**
- **PostgreSQL 15**
- **Apache Kafka**
- **Apache POI** (Excel)
- **Springdoc OpenAPI** (Swagger)

## Pré-requisitos

- Docker e Docker Compose
- Java 17+ (para desenvolvimento local)
- Maven 3.8+

## Como Executar

### Com Docker Compose (Recomendado)

```bash
# Subir toda a infraestrutura
docker compose up -d

# Verificar logs
docker compose logs -f api

# Parar os serviços
docker compose down
```

Se for sua primeira vez com o projeto, use esse fluxo completo com Docker Compose para evitar dependências faltando.
Para um passo a passo mais detalhado (com cURLs e validações), veja `DEMO.md`.

### Desenvolvimento Local

```bash
# Subir apenas infraestrutura (DB, Kafka)
docker compose up -d db zookeeper kafka mockapi

# Rodar a aplicação
./mvnw spring-boot:run
```

## Serviços e Portas

| Serviço | Porta | URL | Descrição |
|---------|-------|-----|-----------|
| **User Service (MS1)** | 8081 | http://localhost:8081 | Auth, Users, Excel |
| **Transaction Service (MS2)** | 8082 | http://localhost:8082 | Transactions, Accounts |
| **Processor Service (MS3)** | 8083 | http://localhost:8083 | Kafka Consumer |
| PostgreSQL | 5432 | localhost:5432 | Database |
| Kafka | 29092 | localhost:29092 | Message Broker |
| Kafka UI | 8090 | http://localhost:8090 | Kafka Dashboard |
| MockAPI | 3001 | http://localhost:3001 | Bank Balance Mock |
| Swagger MS1 | 8081 | http://localhost:8081/swagger-ui.html | Documentação da User API |
| Swagger MS2 | 8082 | http://localhost:8082/swagger-ui.html | Documentação da Transaction API |

## Endpoints da API

### MS1 - User Service (:8081)

#### Autenticação

| Método | Endpoint         | Descrição          |
|--------|------------------|--------------------|
| POST   | /auth/register   | Registrar usuário  |
| POST   | /auth/login      | Login (retorna JWT)|

#### Usuários

| Método | Endpoint       | Descrição                      |
|--------|----------------|--------------------------------|
| GET    | /users         | Listar todos usuários [ADMIN]  |
| GET    | /users/me      | Perfil do usuário autenticado  |
| PUT    | /users/me      | Atualizar perfil               |
| DELETE | /users/me      | Deletar conta                  |
| POST   | /users/import  | Import em massa (Excel) [ADMIN]|

### MS2 - Transaction Service (:8082)

### Contas

| Método | Endpoint       | Descrição           |
|--------|----------------|---------------------|
| POST   | /accounts      | Criar conta         |
| GET    | /accounts      | Listar contas       |
| PUT    | /accounts/{id} | Atualizar conta     |
| DELETE | /accounts/{id} | Deletar conta       |

**Tipos de Conta:** `BANCO`, `FISICO`, `CARTAO_CREDITO`

### Transações

| Método | Endpoint             | Descrição                    |
|--------|----------------------|------------------------------|
| POST   | /transactions        | Criar transação              |
| GET    | /transactions        | Listar (query: accountId)    |
| DELETE | /transactions/{id}   | Deletar transação            |
| POST   | /transactions/transfer | Transferência entre contas |

**Tipos:** `RECEITA`, `DESPESA`, `TRANSFERENCIA`
**Status:** `PENDING`, `APPROVED`, `REJECTED`

### Categorias

| Método | Endpoint         | Descrição         |
|--------|------------------|-------------------|
| POST   | /categories      | Criar categoria   |
| GET    | /categories      | Listar categorias |
| PUT    | /categories/{id} | Atualizar         |
| DELETE | /categories/{id} | Deletar           |

### Análises

| Método | Endpoint         | Query Params    | Descrição                    |
|--------|------------------|-----------------|------------------------------|
| GET    | /analysis/monthly| month, year     | Despesas por categoria (mês) |
| GET    | /analysis/daily  | month, year     | Despesas por dia             |
| GET    | /analysis/total  | month, year     | Total receitas/despesas      |

### Relatórios

| Método | Endpoint       | Descrição                    |
|--------|----------------|------------------------------|
| GET    | /reports/excel | Download relatório em Excel  |

## Autenticação

A API usa JWT (JSON Web Token). Após o login, inclua o token no header:

```
Authorization: Bearer <token>
```

## Exemplos de Uso

### 1. Registrar Usuário

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "password": "senha123",
    "cpf": "123.456.789-00"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "password": "senha123"
  }'
```

### 3. Criar Conta

```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "Conta Corrente",
    "type": "BANCO",
    "accountNumber": "12345-6",
    "balance": 1000.00,
    "creditLimit": 500.00
  }'
```

### 4. Criar Transação

```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "accountId": 1,
    "categoryId": 1,
    "amount": 50.00,
    "currency": "BRL",
    "type": "DESPESA",
    "description": "Almoço"
  }'
```

### 5. Análise Mensal

```bash
curl http://localhost:8080/analysis/monthly?month=1&year=2026 \
  -H "Authorization: Bearer <token>"
```

## Import de Usuários via Excel

O arquivo Excel deve ter as seguintes colunas:

| name | email | password | cpf |
|------|-------|----------|-----|
| João Silva | joao@email.com | senha123 | 123.456.789-00 |

Template disponível em: `docs/users_template.xlsx`

```bash
curl -X POST http://localhost:8080/users/import \
  -H "Authorization: Bearer <token>" \
  -F "file=@docs/users_template.xlsx"
```

## MockAPI - Saldo Bancário

Para contas do tipo `BANCO`, o sistema consulta um serviço externo para verificar saldo adicional.

**Configuração:**
- URL base: `http://localhost:3001/balances`
- Query: `?accountNumber=12345-6`

**Formato da resposta:**
```json
[
  {
    "accountNumber": "12345-6",
    "balance": 5000.00
  }
]
```

## Câmbio (BrasilAPI)

Transações em moedas diferentes de BRL são convertidas automaticamente usando a API da BrasilAPI.

**Moedas suportadas:** USD, EUR, GBP, etc.

## Migrations (Flyway)

O projeto utiliza Flyway para gerenciamento de schema do banco de dados. As migrations estão em `src/main/resources/db/migration/`.

```bash
# Migrations são executadas automaticamente ao iniciar a aplicação

# Para verificar status das migrations
./mvnw flyway:info

# Para limpar e recriar o banco (cuidado em produção!)
./mvnw flyway:clean flyway:migrate
```

## Executando Testes

```bash
# Todos os testes
./mvnw test

# Testes unitários específicos
./mvnw test -Dtest="CreateUserTest,CreateTransactionTest,TransactionProcessorTest"

# Testes de integração
./mvnw test -Dtest="*IntegrationTest"
```

## Estrutura de Diretórios

```
src/
├── main/
│   ├── java/com/finance/api/
│   │   ├── application/          # Use cases e gateways
│   │   ├── config/               # Beans configuration
│   │   ├── domain/               # Entidades de domínio
│   │   └── infra/                # Controllers, persistence, security
│   └── resources/
│       ├── application.properties
│       └── db/migration/         # Flyway migrations
│           ├── V1__create_users_table.sql
│           ├── V2__create_accounts_table.sql
│           ├── V3__create_categories_table.sql
│           ├── V4__create_transactions_table.sql
│           └── V5__insert_default_categories.sql
├── test/
│   └── java/com/finance/api/
│       ├── integration/          # Testes de integração
│       ├── kafka/                # Testes do Kafka
│       ├── service/              # Testes de serviços
│       └── usecases/             # Testes dos use cases
├── docker-compose.yml
├── Dockerfile
├── docs/
│   └── users_template.xlsx       # Template para import
└── mock-api/
    ├── Dockerfile
    └── db.json                   # Dados do MockAPI
```

## Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| DB_HOST | Host do PostgreSQL | localhost |
| DB_USER | Usuário do banco | postgres |
| DB_PASSWORD | Senha do banco | admin |
| JWT_SECRET | Chave secreta JWT | 12345678 |
| SPRING_KAFKA_BOOTSTRAP_SERVERS | Endereço do Kafka | localhost:29092 |
| API_MOCK | URL do MockAPI | http://localhost:3001/balances |

## Licença

Este projeto foi desenvolvido como parte do Desafio Beca Java.
