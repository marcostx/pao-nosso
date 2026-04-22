# Quick Start — Pão Nosso

Guia para subir o app + backend em poucos minutos.

---

## 1. Backend (Flask)

```bash
cd backend
python3 -m venv venv
source venv/bin/activate          # Windows: venv\Scripts\activate
pip install -r requirements.txt
python init_db.py                 # cria/recria o SQLite
python scripts/seed_dev.py        # opcional: popula com Maria Silva + 3 instituições
python app.py                     # servidor em http://0.0.0.0:5000
```

Verifique:

```bash
curl http://localhost:5000/health
```

Resposta esperada:

```json
{ "status": "ok", "message": "Pão Nosso API está funcionando!", "version": "1.0.0" }
```

Rodando os testes:

```bash
cd backend
pytest
```

---

## 2. Android (Jetpack Compose)

1. Abra a pasta `android/` no Android Studio Hedgehog (ou superior).
2. Aguarde o Gradle sincronizar (na primeira vez baixa Compose BOM,
   Retrofit, DataStore, Coil, etc).
3. Selecione um emulador AVD com API 24+ (recomendado API 34) e clique
   **Run**. O app aponta para `http://10.0.2.2:5000` no build de debug.

Para rodar em dispositivo físico, edite o `buildConfigField` `API_BASE_URL`
no bloco `debug` de `android/app/build.gradle.kts`:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://192.168.0.10:5000/\"")
```

(use o IP da máquina onde o Flask está rodando, na mesma Wi-Fi).

---

## 3. Caminho feliz no app

Use as credenciais do seed:

| tipo        | email                    | senha    |
|-------------|--------------------------|----------|
| Doador      | maria@example.com        | senha123 |
| Instituição | sopa@solidaria.org       | senha123 |

Como doador você verá a Home com "Próximas Coletas", pode tocar no FAB para
abrir o wizard de Nova Doação (3 steps), ver suas doações na Agenda e suas
estatísticas no Perfil.

Como instituição, você verá a lista de doações disponíveis e pode enviar
pedidos de coleta — eles aparecem na Agenda do doador.

---

## 4. Problemas comuns

**Backend não inicia / `ModuleNotFoundError`:**

```bash
source venv/bin/activate
pip install -r requirements.txt
```

**App mostra erro de rede:**

- O backend está rodando? `curl http://localhost:5000/health`
- A `API_BASE_URL` aponta pro endereço correto? (`10.0.2.2:5000` no
  emulador, IP da máquina no dispositivo físico)
- Mesma rede Wi-Fi se for físico?

**Reset do banco:**

```bash
cd backend
rm instance/paonosso.db
python init_db.py
python scripts/seed_dev.py
```

---

## 5. Próximos passos

- Ler [`SPEC.md`](SPEC.md) para ver a API completa e o modelo de dados.
- Ler [`PROJECT_STRUCTURE.md`](PROJECT_STRUCTURE.md) para entender o layout.
- Ver as telas em `android/app/src/main/java/com/paonosso/app/ui/screens/`.
