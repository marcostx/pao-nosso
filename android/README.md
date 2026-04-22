# Pão Nosso — Android App

App nativo Android escrito em Kotlin + **Jetpack Compose** que consome a
API Flask em `../backend`.

## Requisitos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK (minSdk 24, targetSdk 34)
- Emulador AVD ou dispositivo físico

## Como rodar

1. Abra `android/` no Android Studio.
2. Aguarde o Gradle sincronizar (Compose BOM, Material3, Retrofit, OkHttp,
   DataStore, Coil, Navigation Compose).
3. Garanta que o backend esteja rodando (`cd ../backend && python app.py`).
4. Selecione um AVD e clique **Run**.

O build de debug aponta para `http://10.0.2.2:5000` (endereço do host
visto pelo emulador). Para usar outro endereço (dispositivo físico,
servidor remoto), edite `app/build.gradle.kts`:

```kotlin
buildTypes {
    debug {
        buildConfigField("String", "API_BASE_URL", "\"http://192.168.0.10:5000/\"")
    }
}
```

## Arquitetura

```
app/src/main/java/com/paonosso/app/
├── PaoNossoApplication.kt          # Inicializa AppContainer
├── MainActivity.kt                 # ComponentActivity → AppNavHost
│
├── data/
│   ├── AppContainer.kt             # Service locator (DI manual)
│   ├── api/
│   │   ├── ApiService.kt           # Retrofit interface
│   │   ├── ApiClient.kt            # OkHttp + Retrofit + BuildConfig.API_BASE_URL
│   │   └── AuthInterceptor.kt      # Injeta Authorization: Bearer <token>
│   ├── local/
│   │   └── TokenStore.kt           # DataStore Preferences
│   ├── model/
│   │   └── Models.kt               # Donation, Appointment, Institution, Stats…
│   └── repository/
│       ├── AuthRepository.kt
│       ├── DonationRepository.kt
│       ├── InstitutionRepository.kt
│       ├── AppointmentRepository.kt
│       └── StatsRepository.kt
│
├── ui/
│   ├── theme/                      # Color, Type, Shape, Theme
│   ├── components/                 # AppScaffold (BottomBar + FAB), StatusPill, EmptyState
│   ├── nav/                        # Routes.kt, AppNavHost.kt
│   └── screens/
│       ├── auth/                   # LoginScreen, RegisterScreen
│       ├── home/                   # HomeScreen (doador)
│       ├── agenda/                 # AgendaScreen
│       ├── donate/                 # DonateFlowScreen (3 steps)
│       ├── map/                    # MapPlaceholderScreen
│       ├── profile/                # ProfileScreen
│       └── institution/            # InstitutionHomeScreen, InstitutionRequestsScreen
│
└── viewmodel/
    ├── AuthViewModel.kt
    ├── HomeViewModel.kt
    ├── AgendaViewModel.kt
    ├── DonateViewModel.kt
    ├── ProfileViewModel.kt
    └── InstitutionViewModel.kt     # InstitutionHomeViewModel + InstitutionRequestsViewModel
```

Padrão MVVM com `StateFlow`. Cada tela tem um `*UiState` e o seu
ViewModel expõe um `Factory` que pega as repos do `AppContainer`.

## Telas (espelham `v2-visual-mock.js`)

- **Auth:** Login + Registro com seleção de tipo (DOADOR / INSTITUICAO).
- **Doador shell** (`AppScaffold`): bottom bar com FAB central que vai
  para o wizard "Nova Doação".
  - Home: header com nome + refeições salvas, atalhos, "Próximas Coletas".
  - Agenda: lista única com `StatusPill` e ações por status.
  - Mapa: placeholder estático.
  - Perfil: avatar, estatísticas, logout.
  - Donate: wizard 3 passos com animações entre steps.
- **Institution shell:** lista de doações disponíveis e caixa de pedidos
  recebidos com Aceitar/Recusar/Concluir/Cancelar.

## Dependências principais

- Jetpack Compose (BOM 2024.02.x), Material3, Material Icons Extended
- Navigation Compose
- Lifecycle ViewModel Compose
- Retrofit + OkHttp (com `AuthInterceptor` e `HttpLoggingInterceptor`)
- Gson
- Kotlin Coroutines
- DataStore Preferences (token + tipo de usuário)
- Coil (avatares)

## Build via terminal

```bash
cd android
./gradlew clean
./gradlew assembleDebug
./gradlew installDebug
```

APK em `app/build/outputs/apk/debug/app-debug.apk`.

## Troubleshooting

**"Unable to connect to backend"** — confirme que o backend está rodando e
que `API_BASE_URL` aponta para o host correto. No emulador use
`10.0.2.2:5000`; em dispositivo físico use o IP da máquina (mesma rede
Wi-Fi).

**"CLEARTEXT communication not permitted"** — `usesCleartextTraffic="true"`
já está habilitado no `AndroidManifest.xml` para o ambiente de
desenvolvimento; produção deve usar HTTPS.

**Gradle Sync falhou** — `./gradlew clean --refresh-dependencies`.

## Fora do MVP

- Mapa real (atualmente é só placeholder).
- Push notifications (FCM).
- Upload de fotos.
- Painel administrativo web.
