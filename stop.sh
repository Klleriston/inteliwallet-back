#!/bin/bash

# Script para parar o backend InteliWallet

echo "🛑 Parando InteliWallet Backend..."

# Para o PostgreSQL
echo "📦 Parando PostgreSQL..."
docker-compose down

echo "✅ Backend parado com sucesso!"