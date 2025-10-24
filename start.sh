#!/bin/bash

# Script de inicialização do backend InteliWallet

echo "🚀 Iniciando InteliWallet Backend..."
echo ""

# Verifica se o Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Por favor, inicie o Docker primeiro."
    exit 1
fi

# Inicia o PostgreSQL via Docker Compose
echo "📦 Iniciando PostgreSQL..."
docker-compose up -d

# Aguarda o PostgreSQL estar pronto
echo "⏳ Aguardando PostgreSQL ficar pronto..."
sleep 5

# Verifica se o Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não encontrado. Por favor, instale o Maven primeiro."
    exit 1
fi

# Compila e executa a aplicação
echo "🔨 Compilando aplicação..."
mvn clean install -DskipTests

echo ""
echo "▶️  Executando aplicação..."
echo ""
mvn spring-boot:run
