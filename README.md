# 💰 InteliWallet Backend

> API REST para o InteliWallet - Carteira Financeira Gamificada com Sistema de Pontos e Social Features

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Sobre o Projeto

InteliWallet é uma aplicação de gestão financeira gamificada que combina controle de finanças pessoais com elementos de jogos e recursos sociais. O backend foi desenvolvido com Spring Boot e oferece uma API REST completa para gerenciamento de transações, metas financeiras, sistema de amizades e gamificação.

### 🎯 Principais Funcionalidades

- **Gestão Financeira**: CRUD completo de transações (receitas e despesas)
- **Estatísticas em Tempo Real**: Dashboard com análises e gráficos
- **Metas Financeiras**: Sistema de objetivos com acompanhamento de progresso
- **Sistema Social**: Adicione amigos, envie convites e compare resultados
- **Gamificação**: Sistema de pontos, níveis e conquistas
- **Autenticação Segura**: JWT com tokens seguros
- **Segurança**: Criptografia de senhas com BCrypt

## 🛠️ Tecnologias

- **Java 17** - Linguagem de programação
- **Spring Boot 3.2.0** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL 15** - Banco de dados relacional
- **JWT (JSON Web Tokens)** - Autenticação stateless
- **Lombok** - Redução de boilerplate
- **ModelMapper** - Mapeamento de DTOs
- **Maven** - Gerenciador de dependências

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

- `GET /api/users/profile` - Obter perfil do usuário autenticado
- `PUT /api/users/profile` - Atualizar perfil (username, email, avatar)
- `PUT /api/users/change-password` - **Alterar senha** (requer senha atual)
- `DELETE /api/users/profile` - **Deletar conta permanentemente**

### Transações

- `GET /api/transactions` - Listar todas as transações do usuário
- `GET /api/transactions/stats` - **Obter estatísticas** (dashboard com métricas)
- `GET /api/transactions/{id}` - Obter uma transação específica
- `POST /api/transactions` - Criar nova transação
- `PUT /api/transactions/{id}` - Atualizar transação existente
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

## 🔐 Autenticação

Todos os endpoints (exceto `/api/auth/register` e `/api/auth/login`) requerem autenticação via JWT.

**Envie o token no header:**

```
Authorization: Bearer {seu-token-jwt}
```

### Exemplo de Registro e Login

**Registro:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "joao",
    "email": "joao@example.com",
    "password": "senha123"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "password": "senha123"
  }'
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "joao",
    "email": "joao@example.com",
    "avatar": "👤",
    "totalPoints": 0,
    "level": 1,
    "hasCompletedOnboarding": false,
    "createdAt": "2025-10-25T14:30:00"
  }
}
```

## 📊 Modelo de Dados

### Entidades Principais

- **User**: Usuários do sistema com pontos, nível e perfil
- **Transaction**: Transações financeiras (receitas e despesas)
- **Goal**: Metas financeiras com progresso
- **Friendship**: Relacionamento bidirecional entre usuários
- **FriendInvite**: Convites de amizade pendentes
- **Achievement**: Conquistas disponíveis (gamificação)
- **Challenge**: Desafios ativos (gamificação)

### Relacionamentos

```
User (1) ----< (N) Transaction
User (1) ----< (N) Goal
User (M) ----< (N) Friendship (N) >---- (M) User
User (1) ----< (N) FriendInvite >---- (1) User
```

## 💡 Exemplos de Uso

### Criar Transação

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Authorization: Bearer {seu-token}" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "expense",
    "amount": 50.00,
    "title": "Almoço",
    "category": "Alimentação",
    "date": "2025-10-25T12:30:00"
  }'
```

### Obter Estatísticas

```bash
curl -X GET http://localhost:8080/api/transactions/stats \
  -H "Authorization: Bearer {seu-token}"
```

**Resposta:**
```json
{
  "totalIncome": 5000.00,
  "totalExpenses": 3200.00,
  "balance": 1800.00,
  "savingsRate": 36.00,
  "monthlyData": [
    {"month": "Oct", "income": 5000, "expenses": 3200}
  ],
  "categoryData": [
    {"name": "Alimentação", "value": 800, "color": "#FF6384"}
  ],
  "weeklySpending": [
    {"day": "Mon", "amount": 150}
  ]
}
```

### Alterar Senha

```bash
curl -X PUT http://localhost:8080/api/users/change-password \
  -H "Authorization: Bearer {seu-token}" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "senha123",
    "newPassword": "novaSenha456"
  }'
```

### Deletar Conta

```bash
curl -X DELETE http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer {seu-token}"
```

## 🏗️ Estrutura do Projeto

```
src/main/java/com/inteliwallet/
├── InteliwalletApplication.java
├── config/              # Configurações (Security, CORS, etc)
├── controller/          # Controllers REST
├── dto/                 # DTOs (Request/Response)
│   ├── request/         # DTOs de entrada
│   └── response/        # DTOs de saída
├── entity/              # Entidades JPA
├── exception/           # Exceções customizadas
├── repository/          # Repositórios JPA
├── security/            # Segurança e JWT
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── CurrentUser.java
└── service/             # Serviços de negócio
```

## ⚙️ Variáveis de Ambiente

Configure as seguintes variáveis em `application.properties`:

```properties
# Server
server.port=8080
server.servlet.context-path=/api

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/inteliwallet
spring.datasource.username=postgres
spring.datasource.password=postgres

# JWT
jwt.secret=seu-secret-key-super-seguro-aqui-com-minimo-256-bits
jwt.expiration=86400000

# CORS
cors.allowed-origins=http://localhost:3000,http://localhost:3001
```

**⚠️ IMPORTANTE**: Em produção, **NÃO** use valores padrão. Configure variáveis de ambiente:

```bash
export JWT_SECRET="seu-secret-super-seguro-em-producao"
export DB_URL="jdbc:postgresql://seu-servidor:5432/db"
export DB_USERNAME="usuario"
export DB_PASSWORD="senha-segura"
```

## 🔒 Segurança

O projeto implementa várias camadas de segurança:

### Autenticação
- **JWT (HS512)**: Tokens assinados com algoritmo HMAC SHA-512
- **Stateless**: Sem sessões no servidor, escalável horizontalmente
- **Expiração**: Tokens expiram em 24 horas (configurável)

### Criptografia
- **BCrypt**: Senhas criptografadas com salt aleatório
- **Custo**: Fator de trabalho padrão do Spring Security

### Proteção de Rotas
- **Spring Security**: Filtros de autenticação e autorização
- **CORS**: Configuração explícita de origens permitidas
- **CSRF**: Desabilitado (API stateless com JWT)

### Validações
- **Bean Validation**: Validação de entrada nos DTOs
- **Exception Handling**: Tratamento global de exceções

### Boas Práticas
- Não expõe stack traces em produção
- Mensagens de erro genéricas para segurança
- Validação de ownership (usuário só acessa seus próprios dados)
- Cascade delete para limpeza de dados relacionados


## 🚀 Desenvolvimento

Para desenvolvimento, você pode usar o profile `dev`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Hot Reload

Com Spring Boot DevTools, a aplicação recarrega automaticamente ao detectar mudanças.

## 📦 Build para Produção

```bash
# Build JAR
mvn clean package -DskipTests

# Executar JAR
java -jar target/inteliwallet-api-1.0.0.jar
```

## 🐳 Docker

### Build da Imagem

```bash
docker build -t inteliwallet-api .
```

### Executar Container

```bash
docker run -d \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/inteliwallet \
  -e JWT_SECRET=seu-secret-seguro \
  --name inteliwallet-api \
  inteliwallet-api
```

### Docker Compose

```bash
# Iniciar todos os serviços
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar serviços
docker-compose down
```

## 📝 Documentação da API

A API está documentada com **Swagger/OpenAPI**.

Acesse: `http://localhost:8080/api/swagger-ui.html`

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request
