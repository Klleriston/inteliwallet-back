# 🚀 InteliWallet API

API REST para sistema de carteira financeira gamificada com gestão de transações, metas, amizades e pontuação.

## 📦 O que foi criado

Este projeto agora inclui:

### ✅ Documentação Completa
- 📮 **Postman Collection** - Collection completa com todos os endpoints
- 📖 **Swagger/OpenAPI** - Documentação interativa da API
- 🔒 **Análise de Segurança** - Relatório completo de vulnerabilidades
- 📚 **Guia de API** - Exemplos e instruções de uso

### 📄 Arquivos Criados

```
backend/
├── InteliWallet_API.postman_collection.json   # Collection do Postman
├── API_DOCUMENTATION.md                        # Documentação da API
├── SECURITY_ANALYSIS.md                        # Análise de segurança
├── .env.example                                # Template de variáveis de ambiente
└── src/main/java/com/inteliwallet/config/
    └── OpenApiConfig.java                      # Configuração do Swagger
```

---

## 🎯 Quick Start

### 1. Configurar Variáveis de Ambiente

```bash
# Copiar template
cp .env.example .env

# Editar .env com suas configurações
nano .env
```

### 2. Iniciar Banco de Dados

```bash
docker-compose up -d
```

### 3. Iniciar Aplicação

```bash
mvn spring-boot:run
```

### 4. Acessar Documentação

- **Swagger UI:** http://localhost:3001/api/swagger-ui.html
- **API Docs JSON:** http://localhost:3001/api/v3/api-docs

---

## 📮 Usar Postman

### Importar Collection

1. Abra o Postman
2. Click em **Import**
3. Selecione: `InteliWallet_API.postman_collection.json`
4. Pronto! Todos os endpoints estão configurados

### Testar Endpoints

1. Execute **Register** ou **Login**
2. O token JWT é salvo automaticamente
3. Teste qualquer endpoint protegido

**Variáveis disponíveis:**
- `{{base_url}}` - URL da API
- `{{jwt_token}}` - Token JWT (auto-preenchido)
- `{{user_id}}` - ID do usuário (auto-preenchido)

---

## 📖 Documentação Interativa (Swagger)

### Acessar Swagger UI

```
http://localhost:3001/api/swagger-ui.html
```

### Como Usar

1. **Autenticar:**
   - Click em **Authorize** (canto superior direito)
   - Cole seu token JWT
   - Click **Authorize**

2. **Testar Endpoints:**
   - Expanda o endpoint desejado
   - Click em **Try it out**
   - Edite o JSON
   - Click em **Execute**

---

## 🔐 Segurança

### ⚠️ ATENÇÃO: Vulnerabilidades Críticas

**ANTES DE IR PARA PRODUÇÃO, corrija:**

1. 🔴 **JWT Secret exposto** → Mover para variável de ambiente
2. 🔴 **DB Password exposto** → Mover para variável de ambiente
3. 🟠 **Rate Limiting ausente** → Implementar proteção contra brute force
4. 🟠 **DDL Auto = update** → Trocar para `validate` em produção

**Leia o relatório completo:** [SECURITY_ANALYSIS.md](./SECURITY_ANALYSIS.md)

### Checklist Pré-Produção

- [ ] Mover secrets para variáveis de ambiente
- [ ] Gerar novo JWT secret
- [ ] Configurar `application-prod.properties`
- [ ] Desabilitar logs SQL em produção
- [ ] Implementar rate limiting
- [ ] Configurar HTTPS/TLS

---

## 📋 Endpoints Disponíveis

### Authentication (Público)
```
POST   /api/auth/register     # Registrar usuário
POST   /api/auth/login        # Fazer login
POST   /api/auth/logout       # Logout
GET    /api/auth/me           # Dados do usuário (requer auth)
```

### Users (Protegido)
```
GET    /api/users/profile     # Ver perfil
PUT    /api/users/profile     # Atualizar perfil
DELETE /api/users/profile     # Deletar conta
```

### Transactions (Protegido)
```
GET    /api/transactions          # Listar todas
GET    /api/transactions/{id}     # Ver específica
POST   /api/transactions          # Criar
PUT    /api/transactions/{id}     # Atualizar
DELETE /api/transactions/{id}     # Deletar
```

### Goals (Protegido)
```
GET    /api/goals                    # Listar todas
GET    /api/goals/{id}               # Ver específica
POST   /api/goals                    # Criar
PUT    /api/goals/{id}               # Atualizar
POST   /api/goals/{id}/contribute    # Contribuir
DELETE /api/goals/{id}               # Deletar
```

### Friends (Protegido)
```
GET    /api/friends                        # Listar amigos
POST   /api/friends/add                    # Enviar convite
DELETE /api/friends/{friendId}             # Remover amigo
GET    /api/friends/invites                # Convites recebidos
POST   /api/friends/invites/{id}/accept    # Aceitar convite
POST   /api/friends/invites/{id}/decline   # Recusar convite
```

---

## 🧪 Exemplo de Uso

### 1. Registrar

```bash
curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "joao",
    "email": "joao@example.com",
    "password": "senha123"
  }'
```

### 2. Criar Transação

```bash
curl -X POST http://localhost:3001/api/transactions \
  -H "Authorization: Bearer {seu-token}" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "income",
    "amount": 5000.00,
    "title": "Salário",
    "category": "Trabalho",
    "date": "2025-10-24T15:00:00.000Z"
  }'
```

### 3. Criar Meta

```bash
curl -X POST http://localhost:3001/api/goals \
  -H "Authorization: Bearer {seu-token}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Viagem Europa",
    "targetAmount": 15000.00,
    "currentAmount": 0,
    "category": "Viagem",
    "deadline": "2026-06-30T23:59:59.000Z"
  }'
```

---

## 🛠️ Stack Tecnológica

- **Framework:** Spring Boot 3.2.0
- **Linguagem:** Java 17
- **Banco de Dados:** PostgreSQL 16
- **Autenticação:** JWT (jjwt 0.11.5)
- **Documentação:** Springdoc OpenAPI 2.3.0
- **ORM:** Hibernate/JPA
- **Validação:** Jakarta Validation
- **Build:** Maven

---

## 📂 Estrutura do Projeto

```
src/main/java/com/inteliwallet/
├── config/              # Configurações (Security, CORS, Swagger)
├── controller/          # REST Controllers
├── dto/
│   ├── request/        # DTOs de requisição
│   └── response/       # DTOs de resposta
├── entity/             # Entidades JPA
├── exception/          # Tratamento de exceções
├── repository/         # Repositórios Spring Data
├── security/           # JWT, Filters, UserDetails
└── service/            # Lógica de negócio
```

---

## 🌐 Ambientes

### Desenvolvimento
- URL: http://localhost:3001/api
- Swagger: http://localhost:3001/api/swagger-ui.html
- PostgreSQL: localhost:5432

### Produção (futuro)
- URL: https://api.inteliwallet.com/api
- Swagger: https://api.inteliwallet.com/api/swagger-ui.html
- PostgreSQL: Gerenciado

---

## 📚 Documentação Adicional

- **[API_DOCUMENTATION.md](./API_DOCUMENTATION.md)** - Guia completo de uso da API
- **[SECURITY_ANALYSIS.md](./SECURITY_ANALYSIS.md)** - Análise detalhada de segurança
- **[CLAUDE.md](./CLAUDE.md)** - Guia de implementação do backend

---

## 🚨 Troubleshooting

### Erro de Conexão com PostgreSQL
```bash
# Verificar se está rodando
docker ps

# Reiniciar
docker-compose down
docker-compose up -d
```

### Erro 401 Unauthorized
- Token expirou → Faça login novamente
- Token inválido → Verifique o formato: `Bearer {token}`

### Swagger não carrega
- Verifique se está acessando: `http://localhost:3001/api/swagger-ui.html`
- Confirme que a aplicação está rodando na porta 3001

---

## 📞 Suporte

- **Documentação:** Swagger UI + arquivos .md
- **Issues:** GitHub Issues
- **Email:** dev@inteliwallet.com

---

## 📄 Licença

MIT License - veja [LICENSE](./LICENSE) para detalhes

---

**Versão:** 1.0.0
**Última Atualização:** 24/10/2025
**Status:** MVP - Pronto para desenvolvimento/teste
