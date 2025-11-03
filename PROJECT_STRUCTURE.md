# Estrutura do Projeto Pão Nosso

Visualização completa da estrutura de arquivos e pastas do projeto.

## Árvore de Diretórios

```
pao-nosso/
│
├── README.md                      # Documentação principal do projeto
├── SPEC.md                        # Especificação técnica completa
├── README_SETUP.md               # Guia de configuração detalhado
├── QUICK_START.md                # Guia rápido de execução
├── IMPLEMENTATION_SUMMARY.md     # Resumo da implementação
├── PROJECT_STRUCTURE.md          # Este arquivo
├── .gitignore                    # Arquivos ignorados pelo Git
│
├── backend/                       # API REST (Python/Flask)
│   │
│   ├── app.py                    # Aplicação Flask principal
│   ├── config.py                 # Configurações do app
│   ├── extensions.py             # Extensões (DB, JWT, CORS)
│   ├── init_db.py                # Script de inicialização do DB
│   ├── requirements.txt          # Dependências Python
│   ├── test_api.sh               # Script de testes
│   ├── README.md                 # Documentação do backend
│   ├── .env                      # Variáveis de ambiente (não versionado)
│   ├── .env.example              # Exemplo de configuração
│   ├── .gitignore                # Ignores específicos do backend
│   │
│   ├── models/                   # Modelos de dados (SQLAlchemy)
│   │   ├── __init__.py           # Exporta todos os modelos
│   │   ├── usuario.py            # Modelo Usuario (doador/instituição)
│   │   ├── instituicao.py        # Modelo Instituicao
│   │   ├── doacao.py             # Modelo Doacao
│   │   ├── solicitacao.py        # Modelo Solicitacao
│   │   └── dispositivo_fcm.py    # Modelo DispositivoFCM
│   │
│   ├── routes/                   # Rotas da API (endpoints)
│   │   ├── __init__.py           # Exporta blueprints
│   │   ├── auth.py               # Autenticação (register, login)
│   │   └── health.py             # Health check
│   │
│   ├── services/                 # Serviços (lógica de negócio)
│   │   └── __init__.py           # Preparado para expansão
│   │
│   ├── utils/                    # Utilitários
│   │   ├── __init__.py           #
│   │   └── validators.py         # Validações (email, senha, etc)
│   │
│   ├── instance/                 # Instância do banco (criado automaticamente)
│   │   └── paonosso.db           # SQLite database
│   │
│   └── venv/                     # Ambiente virtual Python (não versionado)
│       └── ...                   # Dependências instaladas
│
└── android/                      # App Android (Kotlin)
    │
    ├── settings.gradle.kts       # Configuração do projeto
    ├── build.gradle.kts          # Build do projeto
    ├── gradle.properties         # Propriedades do Gradle
    ├── README.md                 # Documentação do Android
    ├── .gitignore                # Ignores do Android
    │
    ├── gradle/wrapper/           # Gradle wrapper
    │   └── gradle-wrapper.properties #
    │
    └── app/                      # Módulo do aplicativo
        │
        ├── build.gradle.kts      # Dependências do app
        ├── proguard-rules.pro    # Regras de ofuscação
        │
        └── src/main/
            │
            ├── AndroidManifest.xml  # Configuração do app
            │
            ├── java/com/paonosso/app/
            │   │
            │   ├── PaoNossoApplication.kt  # Application class
            │   ├── MainActivity.kt         # Activity principal
            │   │
            │   └── data/
            │       │
            │       ├── api/
            │       │   ├── ApiService.kt      # Interface Retrofit
            │       │   └── ApiClient.kt       # Cliente HTTP
            │       │
            │       └── model/
            │           └── Models.kt          # Data classes
            │
            └── res/                  # Recursos do app
                │
                ├── layout/
                │   └── activity_main.xml      # Layout da tela principal
                │
                ├── values/
                │   ├── strings.xml            # Textos (PT-BR)
                │   ├── colors.xml             # Paleta de cores
                │   └── themes.xml             # Tema Material
                │
                └── xml/
                    ├── data_extraction_rules.xml
                    └── backup_rules.xml
```

## Estatísticas

### Backend (Python)

| Categoria | Quantidade |
|-----------|------------|
| Arquivos Python | 14 |
| Modelos de dados | 5 |
| Rotas (blueprints) | 2 |
| Endpoints | 6 |
| Tabelas no DB | 5 |
| Dependências | 17 |

### Android (Kotlin)

| Categoria | Quantidade |
|-----------|------------|
| Arquivos Kotlin | 5 |
| Activities | 1 |
| Layouts XML | 1 |
| Resources | 6 |
| Dependências | 15+ |

### Documentação

| Arquivo | Descrição | Linhas |
|---------|-----------|--------|
| `README.md` | Visão geral do projeto | 165 |
| `SPEC.md` | Especificação técnica | 547 |
| `README_SETUP.md` | Guia de configuração | 300+ |
| `QUICK_START.md` | Início rápido | 300+ |
| `IMPLEMENTATION_SUMMARY.md` | Resumo da implementação | 400+ |
| `backend/README.md` | Docs do backend | 150+ |
| `android/README.md` | Docs do Android | 200+ |

**Total de documentação:** ~2.000+ linhas

## Arquivos-Chave

### Backend

```python
# Ponto de entrada principal
backend/app.py

# Configuração
backend/config.py
backend/extensions.py

# Modelos principais
backend/models/usuario.py
backend/models/doacao.py

# Autenticação
backend/routes/auth.py

# Testes
backend/test_api.sh
```

### Android

```kotlin
// Ponto de entrada principal
android/app/src/main/java/com/paonosso/app/MainActivity.kt

// Networking
android/app/src/main/java/com/paonosso/app/data/api/ApiClient.kt
android/app/src/main/java/com/paonosso/app/data/api/ApiService.kt

// Modelos
android/app/src/main/java/com/paonosso/app/data/model/Models.kt

// Layout
android/app/src/main/res/layout/activity_main.xml

// Configuração
android/app/src/main/res/values/strings.xml
```

## Como Navegar

### Para Backend:

1. **Começar por:** `backend/app.py`
2. **Entender modelos:** `backend/models/`
3. **Ver endpoints:** `backend/routes/`
4. **Testar:** `backend/test_api.sh`

### Para Android:

1. **Começar por:** `MainActivity.kt`
2. **Ver networking:** `data/api/`
3. **Entender dados:** `data/model/Models.kt`
4. **Ver UI:** `res/layout/activity_main.xml`

## Tamanho do Projeto

```
Backend (sem venv):     ~50 KB
Android (sem build):    ~30 KB
Documentação:           ~100 KB
Total (fonte):          ~180 KB

Com dependências:
Backend (com venv):     ~50 MB
Android (com build):    ~200 MB
```

## Checklist de Arquivos

### Essenciais

- [x] README.md principal
- [x] Especificação técnica (SPEC.md)
- [x] Guias de setup
- [x] .gitignore (raiz, backend, android)

### Backend

- [x] app.py (servidor Flask)
- [x] requirements.txt
- [x] 5 modelos de dados
- [x] Rotas de autenticação
- [x] Script de testes

### Android

- [x] MainActivity
- [x] API Client (Retrofit)
- [x] Layouts XML
- [x] Strings em PT-BR
- [x] Gradle configs

## Arquivos Executáveis

```bash
# Backend
backend/app.py              # Inicia servidor
backend/init_db.py          # Inicializa DB
backend/test_api.sh         # Testa API

# Android
# Executar via Android Studio ou:
android/gradlew assembleDebug  # Build APK
```

