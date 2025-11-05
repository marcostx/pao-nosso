# Pão Nosso - Guia de Configuração e Execução

Guia completo para configurar e executar o projeto Pão Nosso localmente.

## Visão Geral

O Pão Nosso é composto por dois componentes principais:

1. **Backend (Python/Flask)**: API REST que gerencia usuários, doações e solicitações
2. **Android App (Kotlin)**: Aplicativo mobile que consome a API

## Pré-requisitos

### Para o Backend:
- Python 3.9+ 
- pip
- Ambiente virtual Python (venv)

### Para o Android:
- Android Studio Arctic Fox ou superior
- JDK 8+
- Android SDK (API 24-34)

## Configuração Rápida (Quick Start)

### Passo 1: Backend

```bash
# 1. Navegar para a pasta do backend
cd backend

# 2. Criar e ativar ambiente virtual
python3 -m venv venv
source venv/bin/activate  # No Windows: venv\Scripts\activate

# 3. Instalar dependências
pip install -r requirements.txt

# 4. Inicializar banco de dados
python init_db.py

# 5. Iniciar servidor
python app.py
```

**Servidor rodando em:** `http://localhost:5000`

#### Testar Backend:

```bash
# Health check
curl http://localhost:5000/health

# Ou executar script de testes completo
./test_api.sh
```

### Passo 2: Android App

```bash
# 1. Abrir Android Studio

# 2. File > Open > Selecionar pasta 'android/'

# 3. Aguardar sincronização do Gradle

# 4. Criar/iniciar emulador Android ou conectar dispositivo físico

# 5. Clicar em Run
```

**Importante:** Certifique-se de que o backend está rodando antes de executar o app!

## Configuração para Dispositivo Físico

Se estiver usando um dispositivo físico Android:

1. Descubra o IP da sua máquina:
   ```bash
   # macOS/Linux
   ifconfig | grep inet
   
   # Windows
   ipconfig
   ```

2. Edite `android/app/src/main/res/values/strings.xml`:
   ```xml
   <!-- Substitua 10.0.2.2 pelo IP da sua máquina -->
   <string name="api_base_url">http://192.168.1.100:5000</string>
   ```

3. Certifique-se de que o dispositivo está na mesma rede Wi-Fi

## Testando a Integração Completa

### Teste 1: Health Check

No app Android, você deve ver:
```
Conectado ao servidor
Backend: Pão Nosso API está funcionando!
Versão: 1.0.0
```

### Teste 2: Via cURL (Terminal)

```bash
# 1. Registrar um doador
curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "senha": "senha123",
    "telefone": "11999999999",
    "tipo": "DOADOR"
  }'

# 2. Fazer login
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "senha": "senha123"
  }'
```

## Estrutura do Projeto

```
pao-nosso/
├── README.md                  # Documentação principal
├── SPEC.md                    # Especificação técnica
├── README_SETUP.md           # Este arquivo
├── backend/                   # API Flask
│   ├── app.py                # Aplicação principal
│   ├── models/               # Modelos de dados
│   ├── routes/               # Endpoints da API
│   ├── utils/                # Utilitários
│   ├── requirements.txt      # Dependências Python
│   ├── init_db.py           # Script de inicialização do DB
│   ├── test_api.sh          # Script de testes
│   └── README.md            # Documentação do backend
└── android/                   # App Android
    ├── app/                  # Código do app
    │   ├── src/main/
    │   │   ├── java/         # Código Kotlin
    │   │   └── res/          # Recursos (layouts, strings, etc)
    │   └── build.gradle.kts  # Dependências do app
    └── README.md            # Documentação do Android
```

## Comandos Úteis

### Backend

```bash
# Recriar banco de dados (apaga dados existentes!)
python init_db.py

# Executar testes da API
./test_api.sh

# Ver logs do servidor
# Os logs aparecem no terminal onde você executou python app.py
```

### Android

```bash
# Limpar build
cd android
./gradlew clean

# Build debug
./gradlew assembleDebug

# Instalar no dispositivo
./gradlew installDebug
```

## Problemas Comuns

### 1. Backend não inicia

**Erro:** `ModuleNotFoundError: No module named 'flask'`

**Solução:**
```bash
# Certifique-se de estar no ambiente virtual
source venv/bin/activate
pip install -r requirements.txt
```

### 2. App não conecta ao backend

**Sintomas:** "Erro de conexão"

**Soluções:**
- Backend está rodando? Teste: `curl http://localhost:5000/health`
- Usando emulador? URL deve ser `http://10.0.2.2:5000`
- Usando dispositivo físico? Atualize o IP em `strings.xml`
- Dispositivo está na mesma rede Wi-Fi?

### 3. Erro de permissão no banco de dados

**Solução:**
```bash
cd backend
rm paonosso.db  # Remove banco antigo
python init_db.py  # Recria
```

### 4. Gradle Sync Failed no Android

**Solução:**
```bash
cd android
./gradlew clean --refresh-dependencies
```

### 5. App not showing in the simulator env (or device)
- missing minimap images

## Status do MVP

### Implementado
- [x] Backend API com Flask
- [x] Banco de dados SQLite
- [x] Modelos de dados (Usuario, Instituicao, Doacao, Solicitacao)
- [x] Endpoints de autenticação (registro, login)
- [x] Health check endpoint
- [x] App Android básico
- [x] Comunicação Android ↔ Backend
- [x] Verificação de conexão

### Em Desenvolvimento
- [ ] Telas de login/registro no Android
- [ ] Endpoints de doações e instituições
- [ ] Navegação entre telas
- [ ] Persistência de token JWT
- [ ] Busca de doações por localização

### Próximas Fases
- [ ] Google Maps integração
- [ ] Notificações push (FCM)
- [ ] Upload de fotos
- [ ] Chat entre usuários
- [ ] Deploy em produção

## Suporte

Em caso de dúvidas ou problemas:

2. Consulte os READMEs específicos:
   - `backend/README.md`
   - `android/README.md`
3. Verifique os logs do servidor e do Logcat
