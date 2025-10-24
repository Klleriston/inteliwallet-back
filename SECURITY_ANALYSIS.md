# 🔒 Análise de Segurança - InteliWallet API (MVP)

**Data da Análise:** 24/10/2025
**Versão da API:** 1.0.0
**Status:** MVP - Pré-Produção

---

## 📋 Sumário Executivo

Esta análise avalia a segurança da API InteliWallet identificando vulnerabilidades, pontos fortes e recomendações para o MVP e produção.

### Status Geral: ⚠️ **ATENÇÃO NECESSÁRIA**

| Categoria | Status | Prioridade |
|-----------|--------|------------|
| Autenticação | ✅ Bom | Baixa |
| Autorização | ⚠️ Necessita melhorias | Média |
| Dados Sensíveis | ❌ Crítico | **ALTA** |
| Validação de Entrada | ✅ Bom | Baixa |
| Rate Limiting | ❌ Ausente | Média |
| Logging | ⚠️ Necessita ajustes | Baixa |
| CORS | ✅ Configurado | Baixa |
| SQL Injection | ✅ Protegido (JPA) | Baixa |

---

## 🔴 VULNERABILIDADES CRÍTICAS (Corrigir ANTES de produção)

### 1. JWT Secret Exposto no application.properties

**Severidade:** 🔴 **CRÍTICA**
**Arquivo:** `src/main/resources/application.properties:20`

```properties
# ❌ VULNERABILIDADE CRÍTICA
jwt.secret=3e154370da8b05e91a3dab3cb743c1ffea35567cadee9cdcf11bb5954510e5495cd9ab381f92f14d11202a964d6d0bc3a6c4fad55f1df4f8dc636765877fdd97
```

**Risco:**
- Secret está hardcoded no código-fonte
- Qualquer pessoa com acesso ao repositório pode gerar tokens válidos
- Comprometimento total da autenticação

**Solução Imediata:**
```properties
# application.properties
jwt.secret=${JWT_SECRET:seu-secret-apenas-para-dev-local}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

**Configurar variável de ambiente:**
```bash
# Linux/Mac
export JWT_SECRET="sua-chave-super-secreta-de-256-bits"

# Windows
set JWT_SECRET=sua-chave-super-secreta-de-256-bits

# Docker
docker-compose.yml:
  environment:
    JWT_SECRET: ${JWT_SECRET}
```

**Ação Requerida:**
1. ✅ Mover secret para variável de ambiente
2. ✅ Gerar novo secret (trocar o atual que está exposto)
3. ✅ Adicionar `.env` ao `.gitignore`
4. ✅ Revogar todos os tokens existentes após deploy

---

### 2. Senha do Banco de Dados Exposta

**Severidade:** 🔴 **CRÍTICA**
**Arquivo:** `src/main/resources/application.properties:6-8`

```properties
# ❌ VULNERABILIDADE CRÍTICA
spring.datasource.username=postgres
spring.datasource.password=postgres
```

**Risco:**
- Credenciais de banco de dados expostas no código
- Acesso direto ao banco de dados

**Solução:**
```properties
spring.datasource.url=${DATABASE_URL:jdbc:postgresql://localhost:5432/inteliwallet}
spring.datasource.username=${DATABASE_USERNAME:postgres}
spring.datasource.password=${DATABASE_PASSWORD:postgres}
```

---

### 3. DDL Auto = Update em Produção

**Severidade:** 🟠 **ALTA**
**Arquivo:** `src/main/resources/application.properties:12`

```properties
# ⚠️ PERIGOSO EM PRODUÇÃO
spring.jpa.hibernate.ddl-auto=update
```

**Risco:**
- Hibernate pode alterar estrutura do banco automaticamente
- Perda de dados em produção
- Migrações não controladas

**Solução:**
```properties
# Desenvolvimento
spring.jpa.hibernate.ddl-auto=update

# Produção (usar Flyway ou Liquibase)
spring.jpa.hibernate.ddl-auto=validate
```

**Recomendação:** Implementar Flyway para migrations controladas:
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

---

## 🟠 VULNERABILIDADES ALTAS (Corrigir em breve)

### 4. Logs Expondo Informações Sensíveis

**Severidade:** 🟠 **ALTA**
**Arquivo:** `src/main/resources/application.properties:13-15`

```properties
# ⚠️ EXPÕE QUERIES E DADOS
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.springframework.security=DEBUG
```

**Risco:**
- SQL queries podem conter dados sensíveis
- Logs de segurança expõem detalhes do sistema
- Facilita ataques

**Solução:**
```properties
# Desenvolvimento
spring.jpa.show-sql=true
logging.level.com.inteliwallet=DEBUG
logging.level.org.springframework.security=DEBUG

# Produção
spring.jpa.show-sql=false
logging.level.com.inteliwallet=INFO
logging.level.org.springframework.security=WARN
```

---

### 5. Ausência de Rate Limiting

**Severidade:** 🟠 **ALTA**

**Risco:**
- Ataques de força bruta no login
- DoS (Denial of Service)
- Spam de convites de amizade
- Criação massiva de transações/metas

**Solução:** Implementar rate limiting com Bucket4j:

```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>7.6.0</version>
</dependency>
```

```java
// RateLimitInterceptor.java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    // 10 requisições por minuto por IP
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {
        String ip = request.getRemoteAddr();

        Bucket bucket = cache.computeIfAbsent(ip, k -> createBucket());

        if (bucket.tryConsume(1)) {
            return true;
        }

        response.setStatus(429); // Too Many Requests
        return false;
    }

    private Bucket createBucket() {
        return Bucket.builder()
            .addLimit(Bandwidth.simple(10, Duration.ofMinutes(1)))
            .build();
    }
}
```

**Endpoints Críticos para Rate Limiting:**
- `POST /api/auth/login` - 5 tentativas/minuto
- `POST /api/auth/register` - 3 tentativas/hora
- `POST /api/friends/add` - 10 convites/hora
- `POST /api/transactions` - 50 transações/minuto

---

### 6. Ausência de Validação de Ownership em Alguns Endpoints

**Severidade:** 🟠 **ALTA**
**Arquivos:** Controllers diversos

**Risco:**
- Usuário pode acessar/modificar recursos de outros usuários se descobrir IDs

**Exemplo do Problema:**
```java
// TransactionController.java:28
@GetMapping("/{id}")
public ResponseEntity<TransactionResponse> getTransaction(
    @CurrentUser String userId,
    @PathVariable String id
) {
    return ResponseEntity.ok(transactionService.getTransaction(userId, id));
}
```

**Verificação Necessária:** O serviço DEVE validar que a transação pertence ao userId:
```java
public TransactionResponse getTransaction(String userId, String transactionId) {
    Transaction transaction = transactionRepository.findById(transactionId)
        .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

    // ✅ VALIDAÇÃO CRÍTICA
    if (!transaction.getUser().getId().equals(userId)) {
        throw new UnauthorizedException("Acesso negado a este recurso");
    }

    return mapToResponse(transaction);
}
```

**Status Atual:** ✅ Verificado - os serviços estão validando ownership corretamente.

---

## 🟡 VULNERABILIDADES MÉDIAS (Melhorias recomendadas)

### 7. Ausência de Refresh Tokens

**Severidade:** 🟡 **MÉDIA**

**Problema:**
- Token JWT expira em 24 horas
- Usuário precisa fazer login novamente
- Má experiência de usuário

**Recomendação:** Implementar refresh tokens:
```java
public class TokenPair {
    private String accessToken;  // Expira em 15 minutos
    private String refreshToken; // Expira em 7 dias
}
```

---

### 8. Falta de Auditoria (Logging de Ações Críticas)

**Severidade:** 🟡 **MÉDIA**

**Ações que DEVEM ser logadas:**
- ✅ Login/Logout (usuário, IP, timestamp)
- ✅ Criação de conta
- ✅ Alteração de senha
- ✅ Exclusão de conta
- ✅ Criação/edição de transações grandes (> R$ 10.000)
- ✅ Exclusão de metas
- ✅ Adição/remoção de amigos

**Implementação:**
```java
@Aspect
@Component
public class AuditAspect {

    @AfterReturning("@annotation(Auditable)")
    public void logAudit(JoinPoint joinPoint) {
        // Salvar em tabela de auditoria
        log.info("AUDIT: {} - User: {} - IP: {}",
            joinPoint.getSignature().getName(),
            SecurityContextHolder.getContext().getAuthentication().getName(),
            getCurrentIP()
        );
    }
}
```

---

### 9. Validação de Email Não Implementada

**Severidade:** 🟡 **MÉDIA**

**Problema:**
- Usuários podem se registrar com emails falsos
- Recuperação de senha não é possível
- Spam/contas fake

**Recomendação:**
```java
@PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    User user = authService.register(request);

    // Enviar email de verificação
    emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());

    return ResponseEntity.ok(new MessageResponse("Verifique seu email para ativar a conta"));
}
```

---

### 10. Ausência de Proteção CSRF para Cookies

**Severidade:** 🟡 **MÉDIA**

**Status Atual:** ✅ OK para JWT stateless
**Observação:** Como o sistema usa JWT em header (não em cookies), CSRF está disabled corretamente:
```java
.csrf(csrf -> csrf.disable())
```

**⚠️ ATENÇÃO:** Se futuramente usar cookies, HABILITAR CSRF protection!

---

## ✅ PONTOS FORTES DO SISTEMA

### 1. Autenticação JWT Robusta ✅
- Uso correto de HS512
- Token expiration implementado
- Bearer token no header

### 2. Proteção contra SQL Injection ✅
- Uso de JPA/Hibernate
- Queries parametrizadas
- Sem concatenação de strings em queries

### 3. Validação de Entrada ✅
- Jakarta Validation implementada
- DTOs com @Valid
- Constraints declarativas (@NotBlank, @Email, etc.)

### 4. CORS Configurado ✅
```properties
cors.allowed-origins=http://localhost:3000,http://localhost:3001
```

### 5. Passwords Hash com BCrypt ✅
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### 6. Tratamento Global de Exceções ✅
- GlobalExceptionHandler implementado
- Respostas padronizadas de erro
- Não expõe stack traces

---

## 🎯 CHECKLIST DE SEGURANÇA PARA PRODUÇÃO

### Pré-Deploy (OBRIGATÓRIO)
- [ ] **Mover JWT secret para variável de ambiente**
- [ ] **Mover credenciais do banco para variáveis de ambiente**
- [ ] **Trocar jwt.secret atual (gerar novo)**
- [ ] **Configurar ddl-auto=validate em produção**
- [ ] **Desabilitar logs de SQL em produção**
- [ ] **Configurar logging level=INFO/WARN**
- [ ] **Implementar rate limiting nos endpoints críticos**
- [ ] **Configurar HTTPS (TLS 1.3)**
- [ ] **Adicionar health check endpoint**

### Pós-Deploy (RECOMENDADO)
- [ ] Implementar refresh tokens
- [ ] Adicionar verificação de email
- [ ] Implementar auditoria de ações
- [ ] Configurar monitoramento (Prometheus/Grafana)
- [ ] Implementar backup automático do banco
- [ ] Adicionar 2FA (Two-Factor Authentication)
- [ ] Configurar WAF (Web Application Firewall)
- [ ] Implementar migrations com Flyway

---

## 🔧 CONFIGURAÇÃO RECOMENDADA PARA PRODUÇÃO

### application-prod.properties
```properties
# Application
spring.application.name=inteliwallet-api
server.port=${PORT:8080}

# Database (NUNCA hardcode em produção)
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}

# JPA (NUNCA use update/create em produção)
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# JWT (VARIÁVEIS DE AMBIENTE)
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:900000}

# CORS (Apenas domínios específicos)
cors.allowed-origins=${ALLOWED_ORIGINS}

# Logging (Mínimo em produção)
logging.level.com.inteliwallet=INFO
logging.level.org.springframework.security=WARN

# Security Headers
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true
```

### docker-compose.prod.yml
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: ${DATABASE_NAME}
      POSTGRES_USER: ${DATABASE_USER}
      POSTGRES_PASSWORD: ${DATABASE_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - backend
    restart: always

  api:
    build: .
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DATABASE_URL: jdbc:postgresql://postgres:5432/${DATABASE_NAME}
      DATABASE_USERNAME: ${DATABASE_USER}
      DATABASE_PASSWORD: ${DATABASE_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      ALLOWED_ORIGINS: ${ALLOWED_ORIGINS}
    depends_on:
      - postgres
    networks:
      - backend
    restart: always

volumes:
  postgres_data:

networks:
  backend:
    driver: bridge
```

### .env.example (ADICIONAR AO PROJETO)
```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/inteliwallet
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_secure_password_here

# JWT
JWT_SECRET=generate_a_256_bit_secret_key_here
JWT_EXPIRATION=86400000

# CORS
ALLOWED_ORIGINS=https://inteliwallet.com,https://app.inteliwallet.com

# Email (futuro)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your_email
SMTP_PASSWORD=your_password
```

---

## 📊 MATRIZ DE RISCO

| Vulnerabilidade | Impacto | Probabilidade | Risco Final |
|-----------------|---------|---------------|-------------|
| JWT Secret exposto | 🔴 Crítico | 🟢 Baixa (se repo privado) | 🔴 ALTO |
| DB Password exposto | 🔴 Crítico | 🟠 Média | 🔴 ALTO |
| Sem Rate Limiting | 🟠 Alto | 🔴 Alta | 🟠 ALTO |
| DDL Auto = update | 🟠 Alto | 🟡 Média | 🟠 MÉDIO |
| Logs em produção | 🟡 Médio | 🟡 Média | 🟡 MÉDIO |
| Sem refresh token | 🟢 Baixo | 🟢 Baixa | 🟢 BAIXO |

---

## 🚀 PRÓXIMOS PASSOS

### Semana 1 (PRÉ-PRODUÇÃO)
1. Mover secrets para variáveis de ambiente
2. Gerar novo JWT secret
3. Configurar application-prod.properties
4. Implementar rate limiting básico
5. Ajustar logs para produção

### Semana 2 (PÓS-LANÇAMENTO)
1. Implementar refresh tokens
2. Adicionar auditoria de ações
3. Configurar monitoramento
4. Implementar verificação de email

### Mês 1 (MELHORIAS)
1. Adicionar 2FA
2. Implementar Flyway migrations
3. Configurar backup automático
4. Adicionar testes de segurança (OWASP ZAP)

---

## 📞 CONTATO E SUPORTE

**Equipe de Segurança:** security@inteliwallet.com
**Relatar Vulnerabilidade:** [security-report@inteliwallet.com](mailto:security-report@inteliwallet.com)

---

**Documento gerado em:** 24/10/2025
**Próxima revisão:** Antes do deploy em produção
**Responsável:** DevOps/Security Team
