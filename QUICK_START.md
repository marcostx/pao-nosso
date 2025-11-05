# Quick Start - Pão Nosso

Guia visual rápido para executar o projeto em 5 minutos.

---

## Execução em 3 Passos

### Passo 1: Backend (2 minutos)

```bash
cd backend
python3 -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
pip install -r requirements.txt
python init_db.py
python app.py
```

**Deve aparecer:**
```
Banco de dados inicializado!
Servidor rodando em http://0.0.0.0:5000
```

**Teste rápido:**
```bash
# Em outro terminal
curl http://localhost:5000/health
```

**Deve retornar:**
```json
{
  "status": "ok",
  "message": "Pão Nosso API está funcionando!",
  "version": "1.0.0"
}
```

---

### Passo 2: Verificar que funciona (30 segundos)

```bash
cd backend
./test_api.sh
```

**Deve aparecer:**
```
======================================
Testando API Pão Nosso
======================================

Testando Health Check... ✓
Testando Endpoint Raiz... ✓
Registrando Doador... ✓
Fazendo Login do Doador... ✓
Registrando Instituição... ✓
Buscando informações do usuário autenticado... ✓

======================================
Testes concluídos!
======================================
```

---

### Passo 3: Android App (2 minutos)

#### Opção A: Emulador Android

1. **Abrir Android Studio**
   ```
   File > Open > Selecionar pasta 'android/'
   ```

2. **Aguardar sincronização do Gradle**
   - Primeira vez pode demorar 1-2 minutos
   - Status aparece na barra inferior

3. **Criar/Iniciar Emulador**
   ```
   Tools > AVD Manager > Create Virtual Device
   Device: Pixel 5
   System Image: Android 12 (API 31) ou superior
   ```

4. **Executar**
   ```
   Clicar no botão Run (ou Shift+F10)
   ```

#### Opção B: Dispositivo Físico

1. **No dispositivo Android:**
   - Configurações > Sobre o telefone
   - Tocar 7x em "Número da versão"
   - Configurações > Opções do desenvolvedor
   - Ativar "Depuração USB"

2. **Conectar via USB**

3. **Descobrir IP da sua máquina:**
   ```bash
   # macOS/Linux
   ifconfig | grep inet
   
   # Exemplo de resultado: 192.168.1.100
   ```

4. **Atualizar URL no Android:**
   - Editar: `android/app/src/main/res/values/strings.xml`
   - Mudar de: `http://10.0.2.2:5000`
   - Para: `http://192.168.1.100:5000` (seu IP)

5. **Executar no Android Studio**
   ```
   Clicar no botão Run
   ```

---

## Verificando se está tudo OK

### No Terminal (Backend):

```
Banco de dados inicializado!
Servidor rodando em http://0.0.0.0:5000
 * Serving Flask app 'app'
 * Debug mode: on
```

### No App Android:

Você deve ver esta tela:

```
┌─────────────────────────────┐
│                             │
│                             │
│        Pão Nosso           │
│  Conectando doadores e     │
│      instituições          │
│                             │
│  Conectado ao servidor     │
│                             │
│  Backend: Pão Nosso API    │
│  está funcionando!         │
│  Versão: 1.0.0             │
│                             │
│  ┌───────────────────┐     │
│  │   Sou Doador      │     │
│  └───────────────────┘     │
│                             │
│  ┌───────────────────┐     │
│  │ Sou Instituição   │     │
│  └───────────────────┘     │
│                             │
└─────────────────────────────┘
```

---

## Problemas?

### Backend não inicia

**Erro:** `ModuleNotFoundError`

**Solução:**
```bash
source venv/bin/activate  # Ativar ambiente virtual
pip install -r requirements.txt
```

---

### App mostra "Erro de conexão"

**Checklist:**
- [ ] Backend está rodando? (`curl http://localhost:5000/health`)
- [ ] URL correta no `strings.xml`?
  - Emulador: `http://10.0.2.2:5000`
  - Físico: `http://SEU_IP:5000`
- [ ] Dispositivo na mesma rede Wi-Fi?

**Teste rápido:**
```bash
# No terminal do seu computador
curl http://localhost:5000/health

# Se funcionar, backend está OK
# Se não, verifique se app.py está rodando
```

---

### Gradle sync failed

**Solução:**
```bash
cd android
gradle wrapper  # Gera o gradlew
./gradlew clean --refresh-dependencies
```

---

## Resumo Visual

``` 
┌─────────────────────────────────────────────────────────┐
│                                                         │
│  TERMINAL 1                    ANDROID STUDIO          │
│  ┌──────────────┐             ┌──────────────┐        │
│  │              │             │              │        │
│  │  cd backend  │             │  File > Open │        │
│  │  python      │             │   android/   │        │
│  │  app.py      │             │              │        │
│  │              │             │  Sync Gradle │        │
│  │  Running     │  ◄────────► │              │        │
│  │              │   REST API  │  Run         │        │
│  │ localhost:   │             │              │        │
│  │    5000      │             │  Conectado   │        │
│  │              │             │              │        │
│  └──────────────┘             └──────────────┘        │
│        ▲                                               │
│        │                                               │
│        ▼                                               │
│  ┌──────────────┐                                     │
│  │  SQLite DB   │                                     │
│  │ paonosso.db  │                                     │
│  └──────────────┘                                     │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## Proximo Passo

Agora que está tudo funcionando, você pode:

1. **Explorar o código:**
   - Backend: `backend/app.py`, `backend/routes/auth.py`
   - Android: `android/app/src/main/java/com/paonosso/app/MainActivity.kt`

2. **Ler a documentação:**
   - [`SPEC.md`](https://github.com/marcostx/pao-nosso/blob/main/SPEC.md) - Especificação completa
   - [`README_SETUP.md`](README_SETUP.md) - Guia detalhado

3. **Começar a desenvolver:**
   - Fase 2: Telas de login/registro no Android
   - Fase 3: Endpoints de doações
   - Fase 4: Integração com Google Maps

---

## Precisa de Ajuda?

1. Verifique os logs:
   - Backend: Terminal onde executou `python app.py`
   - Android: Logcat no Android Studio

2. Consulte a documentação:
   - `backend/README.md` - Detalhes do backend
   - `android/README.md` - Detalhes do Android

3. Teste os endpoints manualmente:
   ```bash
   cd backend
   ./test_api.sh
   ```

---

**Tempo total:** ~5 minutos
**Status esperado:** Backend rodando + App conectado

**Boa codificação!**
