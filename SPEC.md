# Especificação Técnica - Pão Nosso App

## 1. Visão Geral

App mobile Android para conectar doadores de alimentos com instituições de caridade (cozinhas solidárias, abrigos) que distribuem comida para pessoas em situação de vulnerabilidade.

## 2. Arquitetura do Sistema

### 2.1 Componentes Principais

```
┌─────────────────┐
│  App Android    │
│   (Kotlin)      │
└────────┬────────┘
         │
         │ REST API
         │
┌────────▼────────┐
│   Backend API   │
│  (Python/Flask) │
└────────┬────────┘
         │
┌────────▼────────┐
│   Database      │
│  (SQLite/Postgres)
└─────────────────┘
```

### 2.2 Stack Tecnológico

**Backend:**
- Python 3.10+ com Flask
- SQLAlchemy (ORM)
- SQLite (desenvolvimento local) / PostgreSQL (produção)
- JWT para autenticação
- Flask-CORS para comunicação com app

**Mobile:**
- Android nativo (Kotlin) com **Jetpack Compose** + Material 3
- MinSDK: 24 (Android 7.0)
- TargetSDK: 34 (Android 14)
- Retrofit + OkHttp + AuthInterceptor para HTTP
- Coil para imagens
- DataStore (Preferences) para armazenar token + tipo de usuário
- Navigation Compose para navegação
- Sem Google Maps SDK (a UI é list-first)

**Infraestrutura Local (Testes):**
- Backend rodando em localhost:5000
- Emulador Android (10.0.2.2:5000) ou dispositivo físico
- SQLite para banco de dados

**Fora do MVP (Fase 2):** Firebase Cloud Messaging, mapa real, fotos das doações.

## 3. Modelo de Dados

### 3.1 Entidades

#### Usuario
```
- id: UUID (PK)
- nome: String (max 100)
- email: String (unique)
- senha_hash: String
- telefone: String
- tipo: Enum ('DOADOR', 'INSTITUICAO')
- created_at: Timestamp
```

#### Instituicao
```
- id: UUID (PK)
- usuario_id: UUID (FK -> Usuario)
- nome_instituicao: String (max 200)
- cnpj: String (unique, nullable)
- tipo: Enum ('COZINHA_SOLIDARIA', 'ABRIGO', 'ONG', 'IGREJA', 'GOVERNO', 'OUTRO')
- descricao: Text
- endereco_completo: String
- bairro: String (max 100, nullable, indexed)
- horario_funcionamento: String
- telefone_contato: String
- aprovado: Boolean (default True em desenvolvimento, False em produção)
- created_at: Timestamp
```

#### Doacao
```
- id: UUID (PK)
- doador_id: UUID (FK -> Usuario)
- titulo: String (max 100)
- descricao: Text
- quantidade: String (ex: "2 kg", "5 unidades")
- categoria: Enum ('PERECIVEL', 'NAO_PERECIVEL', 'REFEICAO_PRONTA',
                   'HORTIFRUTI', + legados: 'FRUTAS', 'LEGUMES', 'GRAOS',
                   'LATICINIOS', 'OUTROS')
- janela: Enum ('HOJE', 'AMANHA', nullable)
- horario: Time (nullable)
- metodo_entrega: Enum ('EU_ENTREGO', 'SOLICITAR_COLETA')
- endereco_retirada: String (nullable)
- bairro: String (max 100, nullable, indexed)
- instituicao_id: UUID (FK -> Instituicao, nullable)
- status: Enum ('DISPONIVEL', 'RESERVADA', 'COLETADA', 'CANCELADA')
- created_at: Timestamp
- updated_at: Timestamp
```

Notas:
- `EU_ENTREGO` exige `instituicao_id` (cria automaticamente uma `Solicitacao` PENDENTE).
- `SOLICITAR_COLETA` deixa `instituicao_id` nulo até alguma instituição se candidatar.
- `latitude`/`longitude` foram removidos. A localização é trabalhada como string (`bairro`).

#### Solicitacao
```
- id: UUID (PK)
- doacao_id: UUID (FK -> Doacao)
- instituicao_id: UUID (FK -> Instituicao)
- data_coleta_proposta: Date (nullable)
- hora_coleta_proposta: Time (nullable)
- observacoes: Text (nullable)
- status: Enum ('PENDENTE', 'ACEITA', 'RECUSADA', 'CANCELADA', 'CONCLUIDA')
- created_at: Timestamp
- updated_at: Timestamp
```

#### DispositivoFCM
```
- id: UUID (PK)
- usuario_id: UUID (FK -> Usuario)
- token_fcm: String
- created_at: Timestamp
```

### 3.2 Relacionamentos

- Usuario 1:1 Instituicao (somente se tipo = 'INSTITUICAO')
- Usuario 1:N Doacao (um doador pode ter várias doações)
- Doacao 1:N Solicitacao (uma doação pode receber várias solicitações)
- Instituicao 1:N Solicitacao (uma instituição pode fazer várias solicitações)
- Usuario 1:N DispositivoFCM (um usuário pode ter múltiplos dispositivos)

## 4. API REST - Endpoints

### 4.1 Autenticação

```
POST /api/auth/register
Body: { nome, email, senha, telefone, tipo }
Response: { user_id, access_token }

POST /api/auth/login
Body: { email, senha }
Response: { user_id, tipo, access_token }

POST /api/auth/logout
Headers: { Authorization: Bearer <token> }
Response: { message }
```

> Todos os endpoints (exceto `/health`, `/ping`, `/api/auth/register` e
> `/api/auth/login`) exigem o header `Authorization: Bearer <token>`. O cliente
> Android injeta esse header automaticamente via `AuthInterceptor`.

### 4.2 Instituições

```
POST /api/instituicoes
Body: { nome_instituicao, cnpj?, tipo, descricao?, endereco_completo, bairro?,
        horario_funcionamento?, telefone_contato }
Response: { instituicao }

GET /api/instituicoes?bairro=<opcional>
Response: [ { instituicao }, ... ]   (apenas aprovadas, ordem alfabética)

GET /api/instituicoes/me
Response: { instituicao }            (instituição do usuário logado)

GET /api/instituicoes/{id}
Response: { instituicao }

PUT /api/instituicoes/{id}
Body: { campos_a_atualizar }
Response: { instituicao }
```

### 4.3 Doações

```
POST /api/doacoes
Body: { titulo, categoria, metodo_entrega, janela?, horario?, descricao?,
        quantidade?, instituicao_id?, endereco_retirada?, bairro? }
Response: { doacao }
- metodo_entrega = EU_ENTREGO  -> instituicao_id obrigatório
- metodo_entrega = SOLICITAR_COLETA -> endereco_retirada obrigatório

GET /api/doacoes/disponiveis?bairro=<opcional>&categoria=<opcional>&janela=<opcional>
Response: [ { doacao }, ... ]   (status = DISPONIVEL)

GET /api/doacoes/minhas
Response: [ { doacao }, ... ]

GET /api/doacoes/{id}
Response: { doacao }

PUT /api/doacoes/{id}      (somente o doador, status DISPONIVEL ou CANCELADA)
DELETE /api/doacoes/{id}   (somente o doador, exceto se já COLETADA)
```

### 4.4 Solicitações

```
POST /api/solicitacoes               (apenas instituições)
Body: { doacao_id, observacoes? }
Response: { solicitacao }

GET /api/solicitacoes/recebidas
Response: [ { solicitacao }, ... ]
- Doador: solicitações feitas em suas doações.
- Instituição: solicitações em doações onde ela é o ponto de coleta.

GET /api/solicitacoes/enviadas       (instituições)
Response: [ { solicitacao }, ... ]

GET /api/solicitacoes/agendamentos?status=<opcional>
Response: [ { agendamento }, ... ]
- Lista achatada usada pela aba "Agenda" do app, com os campos do mock
  (item, instituicao_nome, janela, horario, status…).

PUT /api/solicitacoes/{id}/aceitar   (doador) — auto-recusa solicitações irmãs
PUT /api/solicitacoes/{id}/recusar   (doador)
PUT /api/solicitacoes/{id}/cancelar  (doador ou instituição envolvida)
PUT /api/solicitacoes/{id}/concluir  (doador ou instituição envolvida)
```

### 4.5 Estatísticas

```
GET /api/stats/me
Response: {
  doacoes_total, doacoes_concluidas, peso_total_kg, refeicoes_salvas,
  instituicoes_ajudadas (doador) | doadores_atendidos (instituição)
}
```

### 4.6 Notificações (Fase 2)

FCM e push notifications saíram do MVP — os endpoints `POST/DELETE
/api/dispositivos/fcm` continuarão na spec original como referência, mas não
estão implementados nesta versão.

## 5. Fluxos Principais

### 5.1 Fluxo de Cadastro - Instituição

1. Usuário baixa app e seleciona "Sou uma Instituição"
2. Preenche formulário: nome, email, senha, dados da instituição
3. App envia localização baseada no endereço (geocoding)
4. Backend cria usuário tipo INSTITUICAO (status: aguardando aprovação)
5. Instituição recebe email/notificação quando aprovada (manual por admin)

### 5.2 Fluxo de Cadastro - Doador

1. Usuário baixa app e seleciona "Quero Doar"
2. Preenche formulário: nome, email, senha, telefone
3. Backend cria usuário tipo DOADOR
4. Login automático após cadastro

### 5.3 Fluxo de Doação

1. Doador clica em "Nova Doação"
2. Preenche: o que vai doar, quantidade, categoria, data/horário disponível
3. Informa endereço de retirada (pode usar localização atual)
4. Backend cria doação com status DISPONIVEL
5. Backend envia notificação push para instituições num raio de 10km
6. Instituições visualizam doação disponível

### 5.4 Fluxo de Solicitação

1. Instituição vê lista de doações disponíveis próximas
2. Seleciona uma doação de interesse
3. Propõe data/hora de coleta
4. Backend cria solicitação com status PENDENTE
5. Doador recebe notificação
6. Doador aceita ou recusa a solicitação
7. Se aceita: status da doação muda para RESERVADA
8. Outras solicitações para mesma doação são automaticamente recusadas
9. Após coleta, instituição ou doador marca como CONCLUIDA

## 6. Interface Android - Telas Principais

A UI v2 é totalmente baseada em listas e está implementada em **Jetpack
Compose** (`com.paonosso.app.ui`). O `AppNavHost` decide entre o shell do
doador e o da instituição com base no `tipo` persistido no `TokenStore`.

### 6.1 Telas Comuns

- **Login** (`ui/screens/auth/LoginScreen.kt`)
- **Registro** (`ui/screens/auth/RegisterScreen.kt`) — escolha entre Doador e
  Instituição

### 6.2 Telas do Doador (shell com bottom bar + FAB central)

- **Home** (`ui/screens/home/HomeScreen.kt`)
  - Header em gradiente esmeralda com nome + total de refeições salvas
  - 2 atalhos rápidos: "Nova Doação" e "Ver Coletas"
  - Lista "Próximas Coletas" (`/api/solicitacoes/agendamentos?status=ACEITA`)
- **Agenda** (`ui/screens/agenda/AgendaScreen.kt`)
  - Lista única vinda de `/api/solicitacoes/agendamentos`
  - `StatusPill` colore por status (ACEITA→esmeralda, PENDENTE→laranja,
    RECUSADA/CANCELADA→vermelho)
  - Ações: Aceitar/Recusar (PENDENTE) ou Concluir/Cancelar (ACEITA)
- **Mapa** (`ui/screens/map/MapPlaceholderScreen.kt`)
  - Placeholder "Mapa em desenvolvimento" — sem dependências
- **Perfil** (`ui/screens/profile/ProfileScreen.kt`)
  - Avatar com inicial, dois cards de estatística (Doações + Peso Total),
    Logout
- **Nova Doação** (`ui/screens/donate/DonateFlowScreen.kt`)
  - Wizard 3 passos:
    1. Item, quantidade, observação, categoria (chips)
    2. Método de entrega (Eu Entrego ↔ lista de instituições | Solicitar
       Coleta ↔ campo de endereço), janela (Hoje/Amanhã) e slot de horário
    3. Resumo + Confirmar
  - Animações `fadeIn() + slideInVertically()` entre steps

### 6.3 Telas da Instituição (shell com bottom bar)

- **Doações** (`ui/screens/institution/InstitutionHomeScreen.kt`)
  - Lista `/api/doacoes/disponiveis`
  - Botão "Solicitar coleta" → cria `Solicitacao` PENDENTE; após criada o card
    mostra "Pedido enviado"
- **Pedidos** (`ui/screens/institution/InstitutionRequestsScreen.kt`)
  - Lista `/api/solicitacoes/recebidas`, com `StatusPill` e ação "Marcar como
    retirado" / "Cancelar" para ACEITA
- **Perfil** — reaproveita `ProfileScreen`

## 7. Configuração Local para Testes

### 7.1 Backend

```bash
# Clonar repositório
git clone <repo-url>
cd pao-nosso

# Criar ambiente virtual Python
python3 -m venv venv
source venv/bin/activate  # No Windows: venv\Scripts\activate

# Instalar dependências
pip install -r backend/requirements.txt

# Configurar variáveis de ambiente
cp backend/.env.example backend/.env
# Editar .env para desenvolvimento local

# Inicializar banco de dados
cd backend
python init_db.py

# Rodar servidor
python app.py
# Servidor rodando em http://localhost:5000
```

### 7.2 Android App

```bash
# Abrir projeto no Android Studio
# Arquivo: android/build.gradle

# Configurar URL do backend em:
# android/app/src/main/res/values/strings.xml
# <string name="api_base_url">http://10.0.2.2:5000</string>
# (10.0.2.2 é o localhost do emulador Android)

# Para dispositivo físico, usar IP da máquina:
# <string name="api_base_url">http://192.168.x.x:5000</string>

# Build e Run no emulador ou dispositivo
```

### 7.3 Testes via Terminal (cURL)

```bash
# Registrar usuário doador
curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com",
    "senha": "senha123",
    "telefone": "11999999999",
    "tipo": "DOADOR"
  }'

# Login
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "senha": "senha123"
  }'

# Criar doação (usar token retornado no login)
curl -X POST http://localhost:5000/api/doacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "titulo": "Frutas frescas",
    "descricao": "5kg de laranjas",
    "quantidade": "5kg",
    "categoria": "FRUTAS",
    "data_disponivel": "2025-11-04",
    "hora_inicio": "14:00",
    "hora_fim": "18:00",
    "endereco_retirada": "Rua das Flores, 123, São Paulo",
    "latitude": -23.550520,
    "longitude": -46.633308
  }'

# Listar doações disponíveis próximas
curl -X GET "http://localhost:5000/api/doacoes/disponiveis?lat=-23.550520&lng=-46.633308&raio=10" \
  -H "Authorization: Bearer <token>"
```

## 8. Recursos Opcionais (Fase 2)

Para manter simplicidade inicial, estas features ficam para versões futuras:

- ✓ Chat entre doador e instituição
- ✓ Sistema de avaliação/feedback
- ✓ Fotos das doações
- ✓ Painel administrativo web
- ✓ Relatórios e analytics
- ✓ Integração com redes sociais
- ✓ Gamificação (badges, ranking de doadores)
- ✓ Suporte iOS

## 9. Considerações de Segurança

- Senhas armazenadas com hash bcrypt
- JWT com expiração de 7 dias
- HTTPS obrigatório em produção
- Rate limiting nos endpoints da API
- Validação de dados no backend
- Sanitização de inputs para prevenir SQL injection
- CORS configurado para aceitar apenas domínios autorizados

## 10. Considerações de Performance

- Índices no banco: email, latitude/longitude, status, created_at
- Cache de consultas frequentes (instituições próximas)
- Paginação em listas (20 itens por página)
- Compressão de respostas da API (gzip)
- Lazy loading de imagens (quando implementado)

## 11. Localização e Idioma

- Interface 100% em Português Brasileiro
- Formato de data: DD/MM/AAAA
- Formato de hora: HH:mm (24h)
- Moeda: Real (R$) - caso necessário no futuro
- Timezone: America/Sao_Paulo

## 12. Estrutura de Pastas

```
pao-nosso/
├── README.md
├── SPEC.md
├── backend/
│   ├── app.py
│   ├── requirements.txt
│   ├── init_db.py
│   ├── models/         (usuario, instituicao, doacao, solicitacao, dispositivo_fcm)
│   ├── routes/         (auth, doacoes, solicitacoes, instituicoes, stats, health)
│   ├── services/       (donation_service, stats_service)
│   ├── scripts/        (seed_dev.py)
│   ├── utils/          (validators)
│   └── tests/          (test_doacoes, test_solicitacoes, test_instituicoes, test_stats, ...)
└── android/
    └── app/src/main/java/com/paonosso/app/
        ├── MainActivity.kt          (single activity, hosts AppNavHost)
        ├── PaoNossoApplication.kt
        ├── data/
        │   ├── api/                 (ApiService, ApiClient, AuthInterceptor)
        │   ├── local/               (TokenStore - DataStore)
        │   ├── model/               (Donation, Appointment, Institution, Stats, Auth*)
        │   └── repository/          (Auth, Donation, Appointment, Institution, Stats)
        ├── ui/
        │   ├── theme/               (Color, Theme, Type, Shape — paleta esmeralda)
        │   ├── components/          (AppScaffold, BottomBar com FAB, StatusPill, EmptyState)
        │   ├── nav/                 (AppNavHost, Routes)
        │   └── screens/             (auth, home, agenda, donate, map, profile, institution)
        └── viewmodel/               (Auth, Home, Agenda, Donate, Profile, InstitutionHome,
                                       InstitutionRequests)
```

## 13. Métricas de Sucesso

- **Funcional**: App consegue completar fluxo completo de doação → solicitação → coleta
- **Performance**: APIs respondem em < 500ms
- **Usabilidade**: Cadastro e criação de doação em < 3 minutos
- **Confiabilidade**: 99% de uptime em produção

## 14. Timeline Estimado (MVP)

- **Semana 1**: Setup inicial + Backend API (modelos e autenticação)
- **Semana 2**: Backend API (endpoints de doações e solicitações)
- **Semana 3**: Android App (telas de autenticação e navegação)
- **Semana 4**: Android App (funcionalidades principais)
- **Semana 5**: Integração backend + frontend + testes
- **Semana 6**: Ajustes, correções e preparação para release

## 15. Próximos Passos

1. ✅ Revisar e aprovar esta especificação
2. ⏳ Implementar backend API
3. ⏳ Implementar app Android
4. ⏳ Testes locais integrados
5. ⏳ Deploy em ambiente de staging
6. ⏳ Testes com usuários beta
7. ⏳ Publicação na Google Play Store

---