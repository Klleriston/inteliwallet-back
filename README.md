# InteliWallet Backend

API REST para o InteliWallet - Carteira Financeira Gamificada.

## Tecnologias

- Java 17
- Spring Boot 3.2.0
- PostgreSQL 15
- JWT para autenticação
- Maven

## Requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL 15+ (ou Docker)

## Configuração

### Banco de Dados

#### Opção 1: Docker (Recomendado)

```bash
docker-compose up -d
```

#### Opção 2: PostgreSQL Local

1. Instale o PostgreSQL
2. Crie o banco de dados:

```sql
CREATE DATABASE inteliwallet;
```

3. Configure as credenciais em `src/main/resources/application.properties`

### Executar a Aplicação

```bash
# Compilar
mvn clean install

# Executar
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:3001/api`

## Endpoints

### Autenticação

- `POST /api/auth/register` - Registrar novo usuário
- `POST /api/auth/login` - Login
- `POST /api/auth/logout` - Logout
- `GET /api/auth/me` - Obter usuário autenticado

### Usuário

- `GET /api/users/profile` - Obter perfil
- `PUT /api/users/profile` - Atualizar perfil
- `DELETE /api/users/profile` - Deletar conta

### Transações

- `GET /api/transactions` - Listar transações
- `GET /api/transactions/{id}` - Obter transação
- `POST /api/transactions` - Criar transação
- `PUT /api/transactions/{id}` - Atualizar transação
- `DELETE /api/transactions/{id}` - Deletar transação

### Metas

- `GET /api/goals` - Listar metas
- `GET /api/goals/{id}` - Obter meta
- `POST /api/goals` - Criar meta
- `PUT /api/goals/{id}` - Atualizar meta
- `POST /api/goals/{id}/contribute` - Contribuir para meta
- `DELETE /api/goals/{id}` - Deletar meta

### Amigos

- `GET /api/friends` - Listar amigos
- `POST /api/friends/add` - Adicionar amigo
- `DELETE /api/friends/{friendId}` - Remover amigo
- `GET /api/friends/invites` - Listar convites
- `POST /api/friends/invites/{inviteId}/accept` - Aceitar convite
- `POST /api/friends/invites/{inviteId}/decline` - Recusar convite

## Autenticação

Todos os endpoints (exceto `/api/auth/register` e `/api/auth/login`) requerem autenticação via JWT.

Envie o token no header:

```
Authorization: Bearer {seu-token-jwt}
```

## Estrutura do Projeto

```
src/main/java/com/inteliwallet/
├── InteliwalletApplication.java
├── config/              # Configurações (Security, CORS, etc)
├── controller/          # Controllers REST
├── dto/                 # DTOs (Request/Response)
├── entity/              # Entidades JPA
├── exception/           # Exceções customizadas
├── repository/          # Repositórios JPA
├── security/            # Segurança e JWT
└── service/             # Serviços de negócio
```

## Desenvolvimento

Para desenvolvimento, você pode usar o profile `dev`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Build para Produção

```bash
mvn clean package
java -jar target/inteliwallet-api-1.0.0.jar
```

## Docker

### Build

```bash
docker build -t inteliwallet-api .
```

### Run

```bash
docker run -p 3001:3001 inteliwallet-api
```

## Licença

MIT