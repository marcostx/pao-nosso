# Pão Nosso - Android App

Aplicativo Android nativo para o Pão Nosso.

## 📋 Requisitos

- Android Studio Arctic Fox ou superior
- JDK 8 ou superior
- Android SDK (mínimo API 24, target API 34)
- Emulador Android ou dispositivo físico

## 🚀 Como Executar

### 1. Abrir o Projeto

```bash
# Abrir Android Studio e selecionar:
# File > Open > Selecionar a pasta android/
```

### 2. Configurar URL do Backend

O app está configurado para se conectar ao backend local. A URL está em:

**Arquivo:** `app/src/main/res/values/strings.xml`

```xml
<!-- Para emulador Android -->
<string name="api_base_url">http://10.0.2.2:5000</string>

<!-- Para dispositivo físico, usar IP da máquina -->
<!-- Exemplo: <string name="api_base_url">http://192.168.1.100:5000</string> -->
```

**Importante:** 
- `10.0.2.2` é o endereço especial do emulador Android para acessar `localhost` da máquina host
- Para dispositivo físico conectado via USB, use o IP da sua máquina na rede local

### 3. Sincronizar Dependências

No Android Studio:
- Clique em **File > Sync Project with Gradle Files**
- Aguarde o download das dependências

### 4. Executar o Servidor Backend

**IMPORTANTE:** O backend deve estar rodando antes de executar o app!

```bash
cd ../backend
source venv/bin/activate
python app.py
```

O servidor deve estar rodando em `http://localhost:5000`

### 5. Executar o App

#### Opção A: Usando Emulador

1. No Android Studio, clique em **Tools > AVD Manager**
2. Crie um novo dispositivo virtual (se ainda não tiver):
   - Device: Pixel 5
   - System Image: Android 12 (API 31) ou superior
3. Inicie o emulador
4. Clique no botão **Run ▶️** no Android Studio

#### Opção B: Usando Dispositivo Físico

1. Ative o **Modo Desenvolvedor** no seu dispositivo Android:
   - Vá em **Configurações > Sobre o telefone**
   - Toque 7 vezes em **Número da versão**
2. Ative **Depuração USB** em **Configurações > Opções do desenvolvedor**
3. Conecte o dispositivo via USB
4. Altere a URL no `strings.xml` para o IP da sua máquina
5. Clique no botão **Run ▶️** no Android Studio

### 6. Verificar Conexão

Ao abrir o app, você deve ver:

```
✅ Conectado ao servidor
Backend: Pão Nosso API está funcionando!
Versão: 1.0.0
```

Se houver erro de conexão, verifique:
- ✅ O servidor backend está rodando?
- ✅ A URL no `strings.xml` está correta?
- ✅ O emulador/dispositivo tem acesso à rede?

## 📱 Funcionalidades Implementadas (MVP)

- ✅ Verificação de conexão com backend
- ✅ Tela inicial (splash/welcome)
- ⏳ Autenticação (registro e login) - em desenvolvimento
- ⏳ Navegação entre telas - em desenvolvimento

## 🏗️ Arquitetura

```
app/
├── src/main/
│   ├── java/com/paonosso/app/
│   │   ├── MainActivity.kt           # Activity principal
│   │   ├── PaoNossoApplication.kt    # Application class
│   │   └── data/
│   │       ├── api/
│   │       │   ├── ApiService.kt     # Interface Retrofit
│   │       │   └── ApiClient.kt      # Cliente HTTP
│   │       └── model/
│   │           └── Models.kt         # Data classes
│   └── res/
│       ├── layout/                   # Layouts XML
│       ├── values/                   # Strings, cores, temas
│       └── xml/                      # Configurações
└── build.gradle.kts                  # Dependências do app
```

## 📦 Dependências Principais

- **Retrofit**: Cliente HTTP para consumir a API REST
- **OkHttp**: Cliente HTTP de baixo nível
- **Gson**: Serialização/deserialização JSON
- **Material Components**: Design system do Google
- **Coroutines**: Programação assíncrona
- **DataStore**: Armazenamento de preferências

## 🔧 Build Manual (via Terminal)

```bash
# Navegar até a pasta android
cd android

# Limpar build anterior
./gradlew clean

# Build de debug
./gradlew assembleDebug

# Build de release
./gradlew assembleRelease

# Instalar no dispositivo conectado
./gradlew installDebug
```

O APK será gerado em: `app/build/outputs/apk/debug/app-debug.apk`

## 🐛 Troubleshooting

### Erro: "Unable to connect to backend"

1. Verifique se o backend está rodando:
   ```bash
   curl http://localhost:5000/health
   ```

2. Para emulador, teste:
   ```bash
   curl http://10.0.2.2:5000/health
   ```

3. Verifique os logs no Logcat do Android Studio

### Erro: "CLEARTEXT communication not permitted"

O app já está configurado com `usesCleartextTraffic="true"` no `AndroidManifest.xml` para permitir HTTP em desenvolvimento.

### Gradle Sync Failed

1. Verifique sua conexão com a internet
2. Limpe o cache do Gradle:
   ```bash
   ./gradlew clean --refresh-dependencies
   ```

## 📝 Próximos Passos

- [ ] Implementar telas de login e registro
- [ ] Implementar navegação entre telas
- [ ] Adicionar DataStore para persistir token JWT
- [ ] Criar telas para doadores
- [ ] Criar telas para instituições
- [ ] Adicionar Google Maps para localização
- [ ] Implementar notificações push (FCM)

