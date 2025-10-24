# 📚 Documentação da API InteliWallet

## 🎯 Visão Geral

A API InteliWallet oferece endpoints para gerenciar transações financeiras, metas, amizades e gamificação em uma carteira digital.

**Base URL:** `http://localhost:3001/api`

---

## 🚀 Quick Start

### 1. Iniciando o Projeto

```bash
# Subir PostgreSQL
docker-compose up -d

# Rodar aplicação
mvn spring-boot:run
```

### 2. Acessar Swagger UI

Após iniciar a aplicação, acesse:

**Swagger UI:** http://localhost:3001/api/swagger-ui.html

O Swagger oferece:
- ✅ Documentação interativa de todos os endpoints
- ✅ Testar requisições diretamente no browser
- ✅ Ver schemas de request/response
- ✅ Suporte a autenticação JWT

---

## 📮 Importar Collection do Postman

### Passo 1: Abrir Postman

Se não tem o Postman instalado:
- **Download:** https://www.postman.com/downloads/

### Passo 2: Importar Collection

1. Abra o Postman
2. Click em **Import** (canto superior esquerdo)
3. Selecione o arquivo: `InteliWallet_API.postman_collection.json`
4. Click em **Import**

### Passo 3: Configurar Variáveis

A collection já vem configurada com variáveis:

| Variável | Valor Padrão | Descrição |
|----------|--------------|-----------|
| `base_url` | `http://localhost:3001/api` | URL base da API |
| `jwt_token` | (vazio) | Preenchido automaticamente após login |
| `user_id` | (vazio) | Preenchido automaticamente após login |

### Passo 4: Testar a API

**Ordem recomendada de testes:**

1. **Register** → Cria conta e salva token automaticamente
2. **Login** → Autentica e salva token
3. **Get Me** → Verifica autenticação
4. **Create Transaction** → Cria uma transação
5. **List Transactions** → Lista todas as transações

---

## 🔐 Autenticação

### Como Autenticar

A API usa **JWT (JSON Web Token)** para autenticação.

#### 1. Registrar Novo Usuário

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "joaosilva",
  "email": "joao@example.com",
  "password": "senha123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "id": "uuid-do-usuario",
    "username": "joaosilva",
    "email": "joao@example.com",
    "avatar": "👤",
    "totalPoints": 0,
    "level": 1,
    "hasCompletedOnboarding": false
  }
}
```

#### 2. Fazer Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "password": "senha123"
}
```

#### 3. Usar o Token nas Requisições

Adicione o token no header de todas as requisições protegidas:

```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**No Postman:** O token é salvo automaticamente na variável `jwt_token`
**No Swagger:** Click em "Authorize" e cole o token (sem "Bearer")

---

## 📋 Endpoints Disponíveis

### Authentication
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/auth/register` | Registrar novo usuário | ❌ |
| POST | `/auth/login` | Fazer login | ❌ |
| GET | `/auth/me` | Dados do usuário autenticado | ✅ |
| POST | `/auth/logout` | Logout (stateless) | ✅ |

### Users
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/users/profile` | Ver perfil | ✅ |
| PUT | `/users/profile` | Atualizar perfil | ✅ |
| DELETE | `/users/profile` | Deletar conta | ✅ |

### Transactions
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/transactions` | Listar transações | ✅ |
| GET | `/transactions/{id}` | Ver transação específica | ✅ |
| POST | `/transactions` | Criar transação | ✅ |
| PUT | `/transactions/{id}` | Atualizar transação | ✅ |
| DELETE | `/transactions/{id}` | Deletar transação | ✅ |

### Goals
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/goals` | Listar metas | ✅ |
| GET | `/goals/{id}` | Ver meta específica | ✅ |
| POST | `/goals` | Criar meta | ✅ |
| PUT | `/goals/{id}` | Atualizar meta | ✅ |
| POST | `/goals/{id}/contribute` | Contribuir para meta | ✅ |
| DELETE | `/goals/{id}` | Deletar meta | ✅ |

### Friends
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/friends` | Listar amigos | ✅ |
| POST | `/friends/add` | Enviar convite | ✅ |
| DELETE | `/friends/{friendId}` | Remover amigo | ✅ |
| GET | `/friends/invites` | Listar convites recebidos | ✅ |
| POST | `/friends/invites/{id}/accept` | Aceitar convite | ✅ |
| POST | `/friends/invites/{id}/decline` | Recusar convite | ✅ |

---

## 💡 Exemplos de Uso

### Criar Transação de Receita

```http
POST /api/transactions
Authorization: Bearer {seu-token}
Content-Type: application/json

{
  "type": "income",
  "amount": 5000.00,
  "title": "Salário",
  "category": "Trabalho",
  "date": "2025-10-24T15:00:00.000Z"
}
```

### Criar Meta Financeira

```http
POST /api/goals
Authorization: Bearer {seu-token}
Content-Type: application/json

{
  "title": "Viagem para Europa",
  "targetAmount": 15000.00,
  "currentAmount": 2000.00,
  "category": "Viagem",
  "deadline": "2026-06-30T23:59:59.000Z"
}
```

### Contribuir para Meta

```http
POST /api/goals/{goal-id}/contribute
Authorization: Bearer {seu-token}
Content-Type: application/json

{
  "amount": 500.00
}
```

### Adicionar Amigo

```http
POST /api/friends/add
Authorization: Bearer {seu-token}
Content-Type: application/json

{
  "username": "mariasilva"
}
```

---

## 📊 Códigos de Status HTTP

| Código | Significado | Quando Ocorre |
|--------|-------------|---------------|
| 200 | OK | Requisição bem sucedida |
| 204 | No Content | Deleção bem sucedida |
| 400 | Bad Request | Dados inválidos na requisição |
| 401 | Unauthorized | Token ausente ou inválido |
| 403 | Forbidden | Sem permissão para este recurso |
| 404 | Not Found | Recurso não encontrado |
| 409 | Conflict | Conflito (ex: email já existe) |
| 500 | Internal Server Error | Erro no servidor |

---

## 🧪 Testando no Swagger

### 1. Acessar Swagger UI
http://localhost:3001/api/swagger-ui.html

### 2. Autenticar

1. Click no botão **Authorize** (canto superior direito)
2. Cole seu JWT token (obtido do `/auth/login`)
3. Click em **Authorize**
4. Click em **Close**

### 3. Testar Endpoints

1. Expanda o endpoint desejado (ex: POST /api/transactions)
2. Click em **Try it out**
3. Edite o JSON de exemplo
4. Click em **Execute**
5. Veja a resposta abaixo

---

## 🔧 Troubleshooting

### Erro 401 Unauthorized

**Problema:** Token JWT inválido ou expirado

**Solução:**
1. Faça login novamente: `POST /api/auth/login`
2. Copie o novo token
3. Atualize o header `Authorization: Bearer {novo-token}`

### Erro 400 Bad Request

**Problema:** Dados inválidos na requisição

**Solução:**
1. Verifique o formato dos campos obrigatórios
2. Veja os erros de validação no response body
3. Consulte a documentação do endpoint

### Erro 404 Not Found

**Problema:** Recurso não existe ou não pertence ao usuário

**Solução:**
1. Verifique se o ID está correto
2. Confirme que o recurso pertence ao seu usuário
3. Liste os recursos disponíveis primeiro (GET)

### Erro CORS

**Problema:** Requisição bloqueada por CORS

**Solução:**
1. Verifique se está usando a porta correta (3001)
2. Confirme que o frontend está em `localhost:3000`
3. Veja as configurações CORS em `application.properties`

---

## 📖 Schemas de Dados

### User
```json
{
  "id": "uuid",
  "username": "string",
  "email": "string",
  "avatar": "emoji",
  "totalPoints": 0,
  "level": 1,
  "hasCompletedOnboarding": false,
  "createdAt": "2025-10-24T15:00:00.000Z"
}
```

### Transaction
```json
{
  "id": "uuid",
  "type": "income|expense",
  "amount": 1000.00,
  "title": "string",
  "category": "string",
  "date": "2025-10-24T15:00:00.000Z",
  "createdAt": "2025-10-24T15:00:00.000Z",
  "updatedAt": "2025-10-24T15:00:00.000Z"
}
```

### Goal
```json
{
  "id": "uuid",
  "title": "string",
  "targetAmount": 15000.00,
  "currentAmount": 2000.00,
  "category": "string",
  "deadline": "2026-06-30T23:59:59.000Z",
  "status": "active|completed|overdue",
  "createdAt": "2025-10-24T15:00:00.000Z",
  "updatedAt": "2025-10-24T15:00:00.000Z"
}
```

### Friend
```json
{
  "id": "uuid",
  "username": "string",
  "avatar": "emoji",
  "totalPoints": 1500,
  "rank": 1,
  "status": "active"
}
```

### FriendInvite
```json
{
  "id": "uuid",
  "fromUser": {
    "id": "uuid",
    "username": "string",
    "avatar": "emoji"
  },
  "toUserId": "uuid",
  "status": "pending|accepted|declined",
  "createdAt": "2025-10-24T15:00:00.000Z"
}
```

---

## 🌐 Ambientes

### Desenvolvimento
- **URL:** http://localhost:3001/api
- **Swagger:** http://localhost:3001/api/swagger-ui.html
- **Database:** PostgreSQL (docker-compose)

### Produção (futuro)
- **URL:** https://api.inteliwallet.com/api
- **Swagger:** https://api.inteliwallet.com/api/swagger-ui.html
- **Database:** PostgreSQL (gerenciado)

---

## 📞 Suporte

**Documentação:** Este arquivo + Swagger UI
**Issues:** https://github.com/inteliwallet/backend/issues
**Email:** dev@inteliwallet.com

---

## 🔄 Changelog

### v1.0.0 (24/10/2025)
- ✅ Autenticação JWT
- ✅ CRUD de Transações
- ✅ CRUD de Metas
- ✅ Sistema de Amizades
- ✅ Swagger UI
- ✅ Postman Collection

---

**Última atualização:** 24/10/2025
**Versão da API:** 1.0.0