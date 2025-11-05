# Pão Nosso

App para combater a fome e desperdício de comida

## Resumo

O app Pão Nosso tem o intuito de conectar cozinhas solidárias, abrigos e instituições que distribuem alimentos para quem não tem o que comer, com pessoas que têm alimentos para doar.

## Motivação

Milhões de pessoas sofrem de fome em pleno 2025. O Pão Nosso facilita a conexão entre quem pode doar e quem precisa receber, combatendo simultaneamente a fome e o desperdício de alimentos.

## Como Funciona

- As instituições cadastram seu perfil no app
- No app do usuário, utilizamos a localização para listar as instituições cadastradas mais próximas
- O usuário cadastra quais itens vai doar, quantidade e horário de retirada
- A instituição recebe uma notificação das pessoas que estão querendo doar
- A instituição seleciona uma data/horário para retirada

## Objetivos

- Acabar com a fome
- Promoção da solidariedade
- Reduzir desperdício de alimentos

## Sistema Operacional

- Foco em Android (API 24+)

---

## Status da Implementação

### Fase 1: Fundações - **COMPLETO**

**Backend (Python/Flask):**
- API REST completa com autenticação
- Banco de dados SQLite configurado
- 5 modelos de dados (Usuario, Instituicao, Doacao, Solicitacao, DispositivoFCM)
- Endpoints de health check e autenticação (registro, login)
- Segurança com JWT e bcrypt
- Testes automatizados funcionando

**Android App (Kotlin):**
- Estrutura completa do projeto
- Comunicação com backend via Retrofit
- Tela inicial com verificação de conexão
- Material Design 3
- Suporte a emulador e dispositivo físico

## Como Executar

### Opção 1: Quick Start

```bash
# Backend
cd backend
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python init_db.py
python app.py

# Android
# Abrir pasta android/ no Android Studio
# Clicar em Run
```

### Opção 2: Guia Completo

Veja o guia detalhado em [`README_SETUP.md`](README_SETUP.md)

## Documentação

- [**SPEC.md**](https://github.com/marcostx/pao-nosso/blob/main/SPEC.md) - Especificação técnica completa
- [**README_SETUP.md**](README_SETUP.md) - Guia de configuração passo a passo
- [**backend/README.md**](backend/README.md) - Documentação do backend
- [**android/README.md**](android/README.md) - Documentação do Android

## Testando

### Backend (Terminal)

```bash
cd backend
./test_api.sh
```

### Android

1. Certifique-se de que o backend está rodando
2. Abra o projeto Android no Android Studio
3. Execute no emulador ou dispositivo
4. Você deve ver: "Conectado ao servidor"

## Arquitetura

```
┌─────────────────┐
│  App Android    │
│   (Kotlin)      │
└────────┬────────┘
         │ REST API
         │
┌────────▼────────┐
│   Backend API   │
│  (Python/Flask) │
└────────┬────────┘
         │
┌────────▼────────┐
│   Database      │
│    (SQLite)     │
└─────────────────┘
```

## Tecnologias

**Backend:**
- Python 3.9+
- Flask 3.0
- SQLAlchemy 2.0
- JWT para autenticação
- bcrypt para segurança

**Android:**
- Kotlin
- Material Design 3
- Retrofit 2.9
- Coroutines
- ViewBinding

## Próximos Passos

### Fase 2: Autenticação no Android
- [ ] Telas de login e registro
- [ ] Persistência de token
- [ ] Navegação entre telas

### Fase 3: Funcionalidades de Doações
- [ ] CRUD de doações
- [ ] Listagem e busca
- [ ] Filtros por categoria

### Fase 4: Localização
- [ ] Google Maps
- [ ] Busca por proximidade
- [ ] Visualização no mapa

### Fase 5: Notificações
- [ ] Firebase Cloud Messaging
- [ ] Push notifications

## Contribuindo

Este é um projeto em desenvolvimento ativo. Contribuições são bem-vindas!

## Licença

---

**Desenvolvido para combater a fome no Brasil**

## contribua
todas as contribuições são bem-vindas

## contato
marcostx1994@gmail.com