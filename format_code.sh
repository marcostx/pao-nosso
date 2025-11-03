#!/bin/bash

# Script para formatar todo o código Python do projeto Pão Nosso
# Uso: ./format_code.sh

echo "🔧 Formatando código Python com Black e isort..."
echo ""

cd backend

# Verificar se Black e isort estão instalados
if ! command -v black &> /dev/null; then
    echo "📦 Instalando Black..."
    pip install black isort flake8
fi

echo "✨ Aplicando Black..."
black .

echo "📑 Organizando imports com isort..."
isort .

echo ""
echo "✅ Formatação concluída!"
echo ""
echo "🔍 Verificando com flake8..."
flake8 . --count --select=E9,F63,F7,F82 --show-source --statistics || true

echo ""
echo "📊 Estatísticas de formatação:"
echo "   - Arquivos Python formatados: $(find . -name '*.py' -not -path './venv/*' | wc -l)"
echo ""
echo "💡 Próximos passos:"
echo "   1. Revisar as mudanças: git diff"
echo "   2. Testar: pytest"
echo "   3. Commit: git add backend/ && git commit -m 'style: apply Black formatting'"

