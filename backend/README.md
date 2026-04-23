# Pão Nosso — Backend

API REST em Flask que sustenta o app Pão Nosso v2 (lista, sem mapas).

## Setup

```bash
cd backend
python3 -m venv venv
source venv/bin/activate            # Windows: venv\Scripts\activate
pip install -r requirements.txt
cp .env.example .env                # se quiser sobrescrever JWT_SECRET / DB
python init_db.py                   # cria SQLite em instance/paonosso.db
python scripts/seed_dev.py          # opcional: popula dados de exemplo
python app.py                       # http://0.0.0.0:5000
```

## Endpoints

Todas as rotas (exceto `/health`, `/ping`, `POST /api/auth/register` e
`POST /api/auth/login`) exigem `Authorization: Bearer <token>`.
Detalhes completos em [`SPEC.md`](../SPEC.md) §4.

**Autenticação**

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

**Doações**

- `POST /api/doacoes`
- `GET /api/doacoes/disponiveis`
- `GET /api/doacoes/minhas`
- `GET /api/doacoes/{id}`
- `PUT /api/doacoes/{id}`
- `DELETE /api/doacoes/{id}`

**Solicitações**

- `POST /api/solicitacoes`
- `GET /api/solicitacoes/recebidas`
- `GET /api/solicitacoes/enviadas`
- `GET /api/solicitacoes/agendamentos`
- `PUT /api/solicitacoes/{id}/aceitar`
- `PUT /api/solicitacoes/{id}/recusar`
- `PUT /api/solicitacoes/{id}/cancelar`
- `PUT /api/solicitacoes/{id}/concluir`

**Instituições**

- `POST /api/instituicoes`
- `GET /api/instituicoes`
- `GET /api/instituicoes/me`
- `GET /api/instituicoes/{id}`
- `PUT /api/instituicoes/{id}`

**Estatísticas**

- `GET /api/stats/me`

**Saúde**

- `GET /health`
- `GET /ping`

### Smoke test

```bash
curl http://localhost:5000/health
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"maria@paonosso.dev","senha":"123456"}'
```

(`maria@paonosso.dev / 123456` existe se você rodou o seed.)

## Testes

```bash
pytest               # roda toda a suíte
pytest tests/test_doacoes.py -v
```

As fixtures em `tests/conftest.py` criam um doador e uma instituição
autenticados, então os testes podem chamar a API com `client` direto.

## Estrutura de pastas

```
backend/
├── app.py
├── config.py
├── extensions.py
├── init_db.py
├── requirements.txt
├── models/
│   ├── usuario.py
│   ├── instituicao.py        # bairro, sem lat/lng, aprovado em dev
│   ├── doacao.py             # janela, horario, metodo_entrega, instituicao_id
│   ├── solicitacao.py        # PENDENTE/ACEITA/RECUSADA/CANCELADA/CONCLUIDA
│   └── dispositivo_fcm.py
├── routes/
│   ├── auth.py
│   ├── doacoes.py
│   ├── solicitacoes.py
│   ├── instituicoes.py
│   ├── stats.py
│   └── health.py
├── services/
│   ├── donation_service.py   # transições de status, auto-recusa de irmãs
│   └── stats_service.py
├── scripts/
│   └── seed_dev.py
├── utils/validators.py
└── tests/                    # conftest + suites por blueprint
```

## Notas

- O modelo `Instituicao` autoaprova em `FLASK_ENV=development` para o
  fluxo do app não depender de um painel administrativo.
- `donation_service.aceitar_solicitacao` recusa automaticamente as
  solicitações irmãs da mesma doação — esse comportamento está coberto por
  testes em `tests/test_solicitacoes.py`.
- Localização foi simplificada para um campo `bairro` em `Doacao` e
  `Instituicao`. Não há mais lat/lng nem geocoding.
