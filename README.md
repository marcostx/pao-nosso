# Pão Nosso

App para combater a fome e o desperdício de comida.

## Resumo

O Pão Nosso conecta cozinhas solidárias, abrigos e ONGs que distribuem
alimentos a pessoas em situação de vulnerabilidade com pessoas que têm
alimentos para doar — sem a burocracia de mapas, geocoding ou cadastros longos.

## Como funciona

- O doador abre o app, escolhe **Nova Doação**, descreve o item, indica se
  vai entregar pessoalmente em uma instituição ou se quer que alguém venha
  buscar, e confirma um horário (Hoje/Amanhã + slot).
- A instituição vê a doação na lista **Doações Disponíveis** e envia um
  pedido de coleta com um toque.
- O doador recebe o pedido na aba **Agenda**, aceita ou recusa. Aceitar
  marca a doação como reservada e recusa automaticamente os outros pedidos
  para a mesma doação.
- Depois da retirada, qualquer um dos lados marca como **Concluída**, e a
  estatística do doador é atualizada (`/api/stats/me`).

A Mapa fica como placeholder no MVP — o fluxo inteiro é list-first.

## Stack

**Backend** (`backend/`): Python 3.10+, Flask, SQLAlchemy, SQLite (dev) ou
Postgres (prod), JWT, Flask-CORS, pytest.

**Mobile** (`android/`): Kotlin + **Jetpack Compose** (Material 3), Retrofit
+ OkHttp + `AuthInterceptor`, DataStore, Navigation Compose, Coil. minSdk 24,
targetSdk 34. Sem Google Maps SDK.

**Fora do MVP (Fase 2):** FCM/push, mapa real, upload de fotos, painel
administrativo.

## Quick start

```bash
# Backend
cd backend
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python init_db.py
python scripts/seed_dev.py     # opcional, cria Maria Silva + 3 instituições
python app.py                  # http://localhost:5000
```

```bash
# Android
# Abra android/ no Android Studio (Hedgehog ou superior)
# Run > Selecione um emulador (ele aponta para http://10.0.2.2:5000)
```

Guia detalhado: [`QUICK_START.md`](QUICK_START.md).

## Documentação

- [`SPEC.md`](SPEC.md) — especificação técnica (modelo, API, telas)
- [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md) — layout das pastas
- [`backend/README.md`](backend/README.md) — endpoints e como rodar testes
- [`android/README.md`](android/README.md) — arquitetura do app Compose

## Testando

```bash
# Backend
cd backend && pytest

# Android
# Run > Run 'app' no Android Studio
```

## Arquitetura

```
┌───────────────────────────┐
│  Android (Compose + MVVM) │
│  ui ↔ viewmodel ↔ repo    │
└──────────────┬────────────┘
               │ HTTPS / JSON
┌──────────────▼────────────┐
│  Backend Flask            │
│  routes ↔ services ↔ ORM  │
└──────────────┬────────────┘
               │
        ┌──────▼──────┐
        │  SQLite/PG  │
        └─────────────┘
```

## Contribuindo

Contribuições são bem-vindas! Veja [`CONTRIBUTING.md`](CONTRIBUTING.md).

## Contato

marcostx1994@gmail.com
