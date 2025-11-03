# Pão Nosso - Backend API

API REST para o aplicativo Pão Nosso.

## Setup Local

### 1. Criar ambiente virtual Python

```bash
cd backend
python3 -m venv venv
source venv/bin/activate  # No Windows: venv\Scripts\activate
```

### 2. Instalar dependências

```bash
pip install -r requirements.txt
```

### 3. Configurar variáveis de ambiente

```bash
# O arquivo .env já está criado para desenvolvimento local
# Se necessário, copie do exemplo:
cp .env.example .env
```

### 4. Inicializar banco de dados

```bash
python init_db.py
```

### 5. Rodar servidor

```bash
python app.py
```

O servidor estará disponível em `http://localhost:5000`

## Testando a API

### Health Check

```bash
curl http://localhost:5000/health
```

### Registro de usuário

```bash
curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "senha": "senha123",
    "telefone": "11999999999",
    "tipo": "DOADOR"
  }'
```

### Login

```bash
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "senha": "senha123"
  }'
```

## Estrutura de Pastas

```
backend/
├── app.py                 # Aplicação Flask principal
├── config.py             # Configurações
├── extensions.py         # Extensões Flask
├── init_db.py           # Script de inicialização do DB
├── requirements.txt      # Dependências Python
├── models/              # Modelos de dados
│   ├── usuario.py
│   ├── instituicao.py
│   ├── doacao.py
│   ├── solicitacao.py
│   └── dispositivo_fcm.py
├── routes/              # Rotas da API
│   ├── auth.py
│   └── health.py
├── services/            # Serviços (futuro)
└── utils/              # Utilitários
    └── validators.py
```

