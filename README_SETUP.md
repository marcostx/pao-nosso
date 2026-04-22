# Pão Nosso — Guia de Configuração e Execução

Guia detalhado. Para uma versão curta veja [`QUICK_START.md`](QUICK_START.md).

## Pré-requisitos

- **Backend:** Python 3.10+, pip, venv.
- **Android:** Android Studio Hedgehog (2023.1.1) ou superior, JDK 17,
  Android SDK 34, Compose-compatible compiler (Kotlin 1.9.x já incluso no
  projeto).

## 1. Backend

```bash
cd backend
python3 -m venv venv
source venv/bin/activate                # Windows: venv\Scripts\activate
pip install -r requirements.txt
python init_db.py                       # cria SQLite em instance/paonosso.db
python scripts/seed_dev.py              # opcional, popula com Maria Silva + 3 instituições
python app.py                           # http://localhost:5000
```

Health check:

```bash
curl http://localhost:5000/health
```

Testes:

```bash
pytest
```

## 2. Android

1. Abra `android/` no Android Studio.
1. Aguarde o Gradle sincronizar (Compose BOM, Material3, Retrofit, OkHttp,
   DataStore, Coil, Navigation Compose).
1. Selecione um AVD (API 24+, recomendado API 34) e clique **Run**. O
   debug build aponta para `http://10.0.2.2:5000`.

### Apontando para outro endereço

Edite `android/app/build.gradle.kts`:

```kotlin
buildTypes {
    debug {
        buildConfigField("String", "API_BASE_URL", "\"http://192.168.0.10:5000/\"")
    }
    release {
        buildConfigField("String", "API_BASE_URL", "\"https://api.paonosso.app/\"")
    }
}
```

Depois faça `Build > Rebuild Project`.

## 3. Smoke test integrado

Crie usuários via cURL (ou use os do seed):

```bash
# Doador
curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nome":"Joao","email":"joao@test.com","senha":"senha123","telefone":"11999999999","tipo":"DOADOR"}'

# Login
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@test.com","senha":"senha123"}'
```

Use as credenciais para entrar no app.

## 4. Comandos úteis

### Backend

```bash
# Resetar banco
rm instance/paonosso.db
python init_db.py
python scripts/seed_dev.py

# Rodar testes específicos
pytest tests/test_doacoes.py -v
```

### Android

```bash
cd android
./gradlew clean
./gradlew assembleDebug
./gradlew installDebug
```

## 5. Problemas comuns

**`ModuleNotFoundError`** — ative o venv: `source venv/bin/activate`.

**App não conecta** — confirme `API_BASE_URL`, e se for dispositivo físico
verifique se ele está na mesma rede Wi-Fi do backend.

**Gradle Sync falhou** —
`cd android && ./gradlew clean --refresh-dependencies`.

**Banco com schema antigo** — `rm instance/paonosso.db && python init_db.py`.

## 6. Status do MVP

### Implementado (v2)

- Backend: auth, doações, solicitações, instituições, stats, seed e testes.
- Android Compose: auth, shell do doador (Home, Agenda, Mapa placeholder,
  Perfil), wizard de Nova Doação em 3 passos, shell mínimo da instituição
  (lista de doações + caixa de pedidos).
- `AuthInterceptor` + `TokenStore` (DataStore) cuidam do JWT por baixo do
  pano.

### Fora do MVP (Fase 2)

- Mapa real e proximidade (hoje só placeholder).
- Push notifications (FCM).
- Upload de fotos para doações.
- Painel administrativo web.

## Suporte

Consulte:

- [`backend/README.md`](backend/README.md)
- [`android/README.md`](android/README.md)
- Logs do `python app.py` e do Logcat (Android Studio).
