#!/bin/bash

# Script de teste da API Pão Nosso
# Uso: ./test_api.sh

BASE_URL="http://localhost:5000"

echo "======================================"
echo "🧪 Testando API Pão Nosso"
echo "======================================"
echo ""

# 1. Health Check
echo "1️⃣  Testando Health Check..."
curl -s $BASE_URL/health | python3 -m json.tool
echo -e "\n"

# 2. Endpoint Raiz
echo "2️⃣  Testando Endpoint Raiz..."
curl -s $BASE_URL/ | python3 -m json.tool
echo -e "\n"

# 3. Registro de Doador
echo "3️⃣  Registrando Doador..."
RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "email": "maria@email.com",
    "senha": "senha123",
    "telefone": "11987654321",
    "tipo": "DOADOR"
  }')
echo $RESPONSE | python3 -m json.tool
TOKEN_DOADOR=$(echo $RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['access_token'])" 2>/dev/null || echo "")
echo -e "\n"

# 4. Login do Doador
echo "4️⃣  Fazendo Login do Doador..."
LOGIN_RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "maria@email.com",
    "senha": "senha123"
  }')
echo $LOGIN_RESPONSE | python3 -m json.tool
echo -e "\n"

# 5. Registro de Instituição
echo "5️⃣  Registrando Instituição..."
INST_RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Abrigo Esperança",
    "email": "abrigo@esperanca.org",
    "senha": "senha123",
    "telefone": "11976543210",
    "tipo": "INSTITUICAO"
  }')
echo $INST_RESPONSE | python3 -m json.tool
echo -e "\n"

# 6. Buscar informações do usuário autenticado
if [ ! -z "$TOKEN_DOADOR" ]; then
  echo "6️⃣  Buscando informações do usuário autenticado..."
  curl -s -X GET $BASE_URL/api/auth/me \
    -H "Authorization: Bearer $TOKEN_DOADOR" | python3 -m json.tool
  echo -e "\n"
fi

echo "======================================"
echo "✅ Testes concluídos!"
echo "======================================"

