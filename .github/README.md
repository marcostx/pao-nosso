# GitHub Workflows - Pão Nosso

Documentação dos workflows de CI/CD e automações do projeto.

## Workflows Implementados

### 1. Backend CI (`backend-ci.yml`)

**Trigger:** Push/PR em `main` ou `develop` que modifica `backend/**`

**Jobs:**
- **Lint**: Verifica formatação (Black, isort, flake8)
- **Test**: Executa testes com cobertura (pytest)
- **Security**: Análise de segurança (Bandit, Safety)
- **Build**: Testa inicialização do servidor

**Tecnologias:**
- Black (formatação)
- isort (organização de imports)
- flake8 (linting)
- pytest + coverage
- Bandit (segurança)
- Safety (vulnerabilidades)

---

### 2. Android CI (`android-ci.yml`)

**Trigger:** Push/PR em `main` ou `develop` que modifica `android/**`

**Jobs:**
- **Lint**: ktlint + Android Lint
- **Build**: Build de APK Debug e Release
- **Test**: Testes unitários
- **Code Quality**: Detekt + Spotless

**Artefatos Gerados:**
- APK Debug
- APK Release (unsigned)
- Relatórios de lint
- Resultados de testes

---

### 3. CodeQL Security Analysis (`codeql-analysis.yml`)

**Trigger:**
- Push/PR em `main` ou `develop`
- Agendado: Toda segunda-feira

**Análises:**
- Python (backend)
- Java/Kotlin (Android)

**Detecta:**
- Vulnerabilidades de segurança
- Code smells
- Problemas de qualidade

---

### 4. Dependency Review (`dependency-review.yml`)

**Trigger:** Pull Requests

**Funcionalidade:**
- Analisa mudanças em dependências
- Alerta sobre vulnerabilidades
- Comenta no PR com resumo

**Severidade:** Falha em vulnerabilidades `moderate` ou superior

---

### 5. Documentation Check (`docs-check.yml`)

**Trigger:** Modificações em arquivos `.md`

**Jobs:**
- **Markdown Lint**: Verifica formatação
- **Link Check**: Detecta links quebrados
- **Spelling**: Verifica ortografia (PT-BR)

---

### 6. PR Auto Labeler (`pr-labeler.yml`)

**Trigger:** Abertura/atualização de PR

**Labels Automáticas:**
- `backend` - mudanças em backend/
- `android` - mudanças em android/
- `documentation` - mudanças em .md
- `dependencies` - mudanças em requirements.txt ou build.gradle
- `tests` - mudanças em testes
- `security` - mudanças em auth/security
- `models` - mudanças em modelos
- `api` - mudanças em rotas/endpoints
- `ui` - mudanças em layouts

---

### 7. Release Build (`release.yml`)

**Trigger:** Push de tag `v*` (ex: v1.0.0)

**Processos:**
1. Cria GitHub Release
2. Build do backend package
3. Build do Android APK
4. Anexa artefatos ao release

**Artefatos:**
- `paonosso-backend.tar.gz`
- `paonosso-android.apk`

---

## Configurações de Qualidade

### Backend (`backend/pyproject.toml`)

**Black:**
- Line length: 100
- Target: Python 3.9

**isort:**
- Profile: black
- Line length: 100

**pytest:**
- Cobertura mínima: 80%
- Formatos: terminal, HTML, XML

**Bandit:**
- Exclusões: tests, venv

---

### Android

**ktlint:**
- Versão: 1.0.1
- Android mode: true

**Detekt:**
- Complexidade máxima: 15
- Parâmetros máximos: 6
- Line length: 120

**Gradle Plugins:**
- detekt: 1.23.1
- ktlint: 11.6.1

---

## Templates

### Pull Request Template

Localização: `.github/PULL_REQUEST_TEMPLATE.md`

**Seções:**
- Descrição
- Tipo de mudança
- Componente afetado
- Como testar
- Checklist
- Screenshots
- Issues relacionadas

### Issue Templates

**Bug Report** (`.github/ISSUE_TEMPLATE/bug_report.md`)
- Descrição
- Componente
- Passos para reproduzir
- Ambiente
- Logs

**Feature Request** (`.github/ISSUE_TEMPLATE/feature_request.md`)
- Descrição
- Problema que resolve
- Solução proposta
- Prioridade
- Benefícios

---

## Como Usar

### Executar Checks Localmente

**Backend:**
```bash
cd backend

# Formatação
black .
isort .

# Linting
flake8 .

# Testes
pytest --cov=.

# Segurança
bandit -r .
safety check
```

**Android:**
```bash
cd android

# Formatação
./gradlew ktlintFormat

# Linting
./gradlew ktlintCheck
./gradlew lint

# Análise estática
./gradlew detekt

# Testes
./gradlew test

# Build
./gradlew assembleDebug
```

---

## Status Badges

Adicione ao README.md:

```markdown
![Backend CI](https://github.com/seu-usuario/pao-nosso/workflows/Backend%20CI/badge.svg)
![Android CI](https://github.com/seu-usuario/pao-nosso/workflows/Android%20CI/badge.svg)
![CodeQL](https://github.com/seu-usuario/pao-nosso/workflows/CodeQL/badge.svg)
```

---

## Configurações Necessárias

### Secrets do GitHub

Para builds de release, configure:

```
ANDROID_KEYSTORE_BASE64
ANDROID_KEYSTORE_PASSWORD
ANDROID_KEY_ALIAS
ANDROID_KEY_PASSWORD
```

### Branch Protection Rules

Recomendado para `main` e `develop`:

- ✅ Require pull request reviews (1 approval)
- ✅ Require status checks to pass
  - Backend CI
  - Android CI
  - CodeQL
- ✅ Require branches to be up to date
- ✅ Include administrators
- ✅ Restrict force pushes

---

## Troubleshooting

### Workflow falha no Backend CI

1. Verifique se `requirements.txt` está atualizado
2. Execute testes localmente
3. Verifique formatação com Black/isort

### Workflow falha no Android CI

1. Verifique compatibilidade do Gradle
2. Execute `./gradlew clean`
3. Verifique ktlint localmente

### CodeQL timeout

1. Reduza complexidade do código
2. Adicione exclusões se necessário
3. Verifique se build está otimizado

---

## Manutenção

### Atualizar Versões

Verifique periodicamente:
- Actions versions (@v4, @v3, etc)
- Python version
- JDK version
- Gradle version
- Dependências de linting

### Monitoramento

- GitHub Actions usage
- Build times
- Failure rates
- Coverage trends

---

## Contribuindo

Para adicionar novos workflows:

1. Crie arquivo em `.github/workflows/`
2. Teste localmente com [act](https://github.com/nektos/act)
3. Documente aqui
4. Abra PR com descrição detalhada

---

Última atualização: Novembro 2025

