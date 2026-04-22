# Estrutura do Projeto Pão Nosso

## Visão geral

```
pao-nosso/
├── README.md                  # Visão geral do projeto
├── SPEC.md                    # Especificação técnica (modelo, API, telas)
├── QUICK_START.md             # Como subir o projeto em poucos minutos
├── PROJECT_STRUCTURE.md       # Este arquivo
├── CONTRIBUTING.md
│
├── backend/                   # API REST (Python / Flask)
│   ├── app.py                 # Aplicação Flask + registro de blueprints
│   ├── config.py              # Configuração (SQLALCHEMY_*, JWT_*, CORS_*)
│   ├── extensions.py          # db, jwt, cors
│   ├── init_db.py             # Recria as tabelas no SQLite
│   ├── requirements.txt
│   ├── README.md              # Documentação do backend
│   │
│   ├── models/
│   │   ├── usuario.py
│   │   ├── instituicao.py     # bairro, sem lat/lng, aprovado em dev
│   │   ├── doacao.py          # janela, horario, metodo_entrega, bairro
│   │   ├── solicitacao.py     # status PENDENTE/ACEITA/RECUSADA/CANCELADA/CONCLUIDA
│   │   └── dispositivo_fcm.py
│   │
│   ├── routes/
│   │   ├── auth.py
│   │   ├── doacoes.py         # CRUD + /disponiveis + /minhas
│   │   ├── solicitacoes.py    # CRUD + /agendamentos + accept/refuse/cancel/conclude
│   │   ├── instituicoes.py    # CRUD + listagem aprovada
│   │   ├── stats.py           # GET /api/stats/me
│   │   └── health.py
│   │
│   ├── services/
│   │   ├── donation_service.py  # Transições de status, auto-recusa de irmãs
│   │   └── stats_service.py     # Agregação de estatísticas
│   │
│   ├── scripts/
│   │   └── seed_dev.py        # Maria Silva + 3 instituições + doações de exemplo
│   │
│   ├── utils/validators.py
│   │
│   └── tests/
│       ├── conftest.py        # Fixtures (app, client, doador, instituicao)
│       ├── test_doacoes.py
│       ├── test_solicitacoes.py
│       ├── test_instituicoes.py
│       └── test_stats.py
│
└── android/                   # App Android (Kotlin + Jetpack Compose)
    ├── settings.gradle.kts
    ├── build.gradle.kts
    ├── gradle.properties
    ├── README.md
    │
    └── app/
        ├── build.gradle.kts   # Compose BOM, Material3, Coil, DataStore
        └── src/main/
            ├── AndroidManifest.xml   # Sem permissões de localização
            │
            ├── java/com/paonosso/app/
            │   ├── PaoNossoApplication.kt   # Inicializa AppContainer
            │   ├── MainActivity.kt          # ComponentActivity → AppNavHost
            │   │
            │   ├── data/
            │   │   ├── AppContainer.kt          # Service locator (DI manual)
            │   │   ├── api/
            │   │   │   ├── ApiService.kt        # Retrofit interface (todas as rotas)
            │   │   │   ├── ApiClient.kt         # OkHttp + Retrofit + BuildConfig.API_BASE_URL
            │   │   │   └── AuthInterceptor.kt   # Injeta Bearer token
            │   │   ├── local/
            │   │   │   └── TokenStore.kt        # DataStore preferences
            │   │   ├── model/
            │   │   │   └── Models.kt            # Donation, Appointment, Stats…
            │   │   └── repository/
            │   │       ├── AuthRepository.kt
            │   │       ├── DonationRepository.kt
            │   │       ├── InstitutionRepository.kt
            │   │       ├── AppointmentRepository.kt
            │   │       └── StatsRepository.kt
            │   │
            │   ├── ui/
            │   │   ├── theme/        # Color, Type, Shape, Theme (paleta esmeralda)
            │   │   ├── components/   # AppScaffold, StatusPill, EmptyState
            │   │   ├── nav/          # Routes.kt, AppNavHost.kt
            │   │   └── screens/
            │   │       ├── auth/         # LoginScreen, RegisterScreen
            │   │       ├── home/         # HomeScreen (doador)
            │   │       ├── agenda/       # AgendaScreen (doador)
            │   │       ├── donate/       # DonateFlowScreen (3 steps)
            │   │       ├── map/          # MapPlaceholderScreen
            │   │       ├── profile/      # ProfileScreen
            │   │       └── institution/  # InstitutionHomeScreen, InstitutionRequestsScreen
            │   │
            │   └── viewmodel/        # AuthVM, HomeVM, AgendaVM, DonateVM, ProfileVM,
            │                          # InstitutionHomeVM, InstitutionRequestsVM
            │
            └── res/values/
                ├── strings.xml
                ├── colors.xml
                └── themes.xml        # Theme.PaoNosso (sem ActionBar, status emerald)
```

## Onde começar

### Backend

1. `backend/app.py` — registra todos os blueprints.
1. `backend/models/doacao.py` e `solicitacao.py` — modelo central da v2.
1. `backend/services/donation_service.py` — regras de negócio (auto-recusa,
   transições de status).
1. `backend/tests/` — exemplos completos de uso da API.

### Android

1. `MainActivity.kt` → `ui/nav/AppNavHost.kt` — ponto de entrada Compose.
1. `ui/components/AppScaffold.kt` — bottom bar + FAB central que sustenta o
   shell do doador.
1. `ui/screens/donate/DonateFlowScreen.kt` — wizard de Nova Doação (mocks
   fielmente reproduzidos).
1. `data/AppContainer.kt` — DI leve (service locator) usado pelos
   `ViewModels`.

## Convenções

- Backend usa Flask + SQLAlchemy 2.0 (declarative). Toda regra de negócio
  fora de transições triviais mora em `services/`.
- Android segue MVVM com `StateFlow`. Cada tela tem um `*UiState` data
  class e um `Factory` no ViewModel para receber as repos do `AppContainer`.
- Sem injeção de dependência por anotação (Hilt) no MVP — manter o setup
  enxuto. O `AppContainer` é criado em `PaoNossoApplication`.
- Toda chamada autenticada passa por `AuthInterceptor`; o token vive em
  DataStore (`TokenStore`).

## Fora do MVP

- Google Maps SDK e geocoding — substituídos pelo placeholder de mapa.
- FCM / push notifications.
- Upload de fotos.
- Painel administrativo web.
