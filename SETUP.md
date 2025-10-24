# Guia de Configuração - InteliWallet Backend

## 📋 Pré-requisitos

1. **Java 17 ou superior**
   ```bash
   java -version
   ```

2. **Maven 3.8+**
   ```bash
   mvn -version
   ```

3. **Docker e Docker Compose** (para o banco de dados)
   ```bash
   docker --version
   docker-compose --version
   ```

## 🚀 Inicialização Rápida

### Opção 1: Script Automático (Recomendado)

```bash
# Dar permissão de execução aos scripts (apenas primeira vez)
chmod +x start.sh stop.sh

# Iniciar o backend
./start.sh

# Para parar
./stop.sh
```

### Opção 2: Manual

#### 1. Iniciar o PostgreSQL

```bash
docker-compose up -d
```

#### 2. Compilar e executar

```bash
# Compilar
mvn clean install

# Executar
mvn spring-boot:run
```

## 🔧 Configuração

### Banco de Dados

O projeto está configurado para usar PostgreSQL via Docker. As configurações padrão são:

- **Host:** localhost
- **Porta:** 5432
- **Database:** inteliwallet
- **Usuário:** postgres
- **Senha:** postgres

Para alterar, edite `src/main/resources/application.properties`

### JWT Secret

**IMPORTANTE:** Para produção, altere o `jwt.secret` em `application.properties`

```properties
jwt.secret=seu-secret-key-super-seguro-com-minimo-256-bits
```

### CORS

Por padrão, o CORS está configurado para aceitar requisições de:
- http://localhost:3000 (frontend)
- http://localhost:3001 (backend)

Para adicionar outras origens, edite `application.properties`:

```properties
cors.allowed-origins=http://localhost:3000,https://seu-dominio.com
```

## 🧪 Testando a API

### 1. Registrar um usuário

```bash
curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "senha123"
  }'
```

### 2. Fazer login

```bash
curl -X POST http://localhost:3001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "senha123"
  }'
```

Isso retornará um token JWT. Use-o nos próximos requests:

```bash
curl -X GET http://localhost:3001/api/auth/me \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

## 📁 Estrutura do Projeto

```
backend/
├── src/
│   └── main/
│       ├── java/com/inteliwallet/
│       │   ├── config/              # Configurações (Security, CORS)
│       │   ├── controller/          # REST Controllers
│       │   ├── dto/                 # Data Transfer Objects
│       │   ├── entity/              # Entidades JPA
│       │   ├── exception/           # Tratamento de exceções
│       │   ├── repository/          # Repositórios JPA
│       │   ├── security/            # JWT e autenticação
│       │   ├── service/             # Lógica de negócio
│       │   └── InteliwalletApplication.java
│       └── resources/
│           └── application.properties
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── start.sh                         # Script de inicialização
└── stop.sh                          # Script para parar
```

## 🔍 Logs

Durante o desenvolvimento, os logs SQL serão exibidos no console.

Para ajustar o nível de logs, edite `application.properties`:

```properties
logging.level.com.inteliwallet=DEBUG
logging.level.org.springframework.security=DEBUG
```

## 🐛 Troubleshooting

### Erro: "Port 5432 already in use"

Você já tem PostgreSQL rodando localmente. Opções:
1. Pare o PostgreSQL local
2. Altere a porta no `docker-compose.yml`

### Erro: "Could not connect to database"

1. Verifique se o PostgreSQL está rodando: `docker ps`
2. Aguarde alguns segundos após iniciar o container
3. Verifique as credenciais em `application.properties`

### Erro de compilação

```bash
# Limpe o projeto e compile novamente
mvn clean install -DskipTests
```

## 📦 Build para Produção

```bash
# Compilar JAR
mvn clean package

# Executar JAR
java -jar target/inteliwallet-api-1.0.0.jar
```

## 🐳 Docker

### Build da imagem

```bash
docker build -t inteliwallet-api .
```

### Executar com Docker

```bash
docker run -p 3001:3001 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/inteliwallet \
  inteliwallet-api
```

## 📚 Documentação da API

Todos os endpoints disponíveis estão documentados no arquivo `README.md`

## 🆘 Ajuda

Se encontrar problemas, verifique:

1. Java 17+ está instalado
2. Maven está instalado
3. Docker está rodando
4. PostgreSQL está acessível
5. Portas 3001 e 5432 estão livres

---

**Desenvolvido para InteliWallet** 🚀