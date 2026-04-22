# Guia de Contribuição - Pão Nosso

Obrigado por considerar contribuir com o Pão Nosso!
Este documento reúne as diretrizes para colaborar com o projeto.

## Código de Conduta

Este projeto adere a um código de conduta.
Ao participar, você concorda em manter um ambiente respeitoso e inclusivo.

## Como Contribuir

### Reportando Bugs

1. Verifique se o bug já foi reportado nas [Issues](https://github.com/marcostx/pao-nosso/issues)
1. Se não, crie uma nova issue usando o template de Bug Report
1. Inclua o máximo de detalhes possível
1. Adicione labels apropriadas

### Sugerindo Features

1. Verifique se a feature já foi sugerida nas Issues
1. Crie uma nova issue usando o template de Feature Request
1. Descreva claramente o problema que a feature resolve
1. Explique como a feature funcionaria

### Pull Requests

1. **Fork o repositório**
1. **Crie uma branch** a partir de `develop`:

   ```bash
   git checkout -b feature/nome-da-feature
   # ou
   git checkout -b fix/nome-do-bug
   ```

1. **Faça suas alterações** seguindo os padrões do projeto
1. **Teste suas mudanças**:

   ```bash
   # Backend
   cd backend
   pytest
   ./test_api.sh

   # Android
   cd android
   ./gradlew test
   ./gradlew lint
   ```

1. **Commit suas mudanças** com mensagens descritivas:

   ```bash
   git commit -m "feat: adiciona validação de CNPJ"
   # ou
   git commit -m "fix: corrige erro no login de instituições"
   ```

1. **Push para seu fork**:

   ```bash
   git push origin feature/nome-da-feature
   ```

1. **Abra um Pull Request** para a branch `develop`

## Padrões de Código

### Backend (Python)

- Siga a [PEP 8](https://pep8.org/)
- Use **Black** para formatação:

  ```bash
  black .
  ```

- Use **isort** para organizar imports:

  ```bash
  isort .
  ```

- Execute **flake8** para linting:

  ```bash
  flake8 .
  ```

- Adicione docstrings para funções e classes
- Mínimo de 80% de cobertura de testes

### Android (Kotlin)

- Siga as [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use **ktlint** para formatação:

  ```bash
  ./gradlew ktlintFormat
  ```

- Execute **detekt** para análise estática:

  ```bash
  ./gradlew detekt
  ```

- Use nomes descritivos em português para variáveis de UI
- Mínimo de 70% de cobertura de testes

## Convenção de Commits

Usamos [Conventional Commits](https://www.conventionalcommits.org/):

```text
<tipo>(<escopo>): <descrição>

[corpo opcional]

[rodapé opcional]
```

**Tipos:**

- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Mudanças na documentação
- `style`: Formatação, ponto e vírgula faltando, etc
- `refactor`: Refatoração de código
- `test`: Adição de testes
- `chore`: Atualizações de build, configs, etc

**Exemplos:**

```text
feat(backend): adiciona endpoint de busca de doações
fix(android): corrige crash ao fazer login
docs: atualiza guia de instalação
test(backend): adiciona testes para modelo de instituição
```

## Estrutura de Branches

- `main`: Código em produção
- `develop`: Branch de desenvolvimento principal
- `feature/*`: Novas funcionalidades
- `fix/*`: Correções de bugs
- `hotfix/*`: Correções urgentes para produção
- `release/*`: Preparação para release

## Processo de Review

1. Pelo menos 1 aprovação é necessária
1. Todos os checks de CI devem passar
1. Código deve seguir os padrões estabelecidos
1. Testes devem ser incluídos
1. Documentação deve ser atualizada se necessário

## Configurando o Ambiente de Desenvolvimento

### Ambiente Backend

```bash
cd backend
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
pip install black isort flake8 pytest
python init_db.py
```

### Ambiente Android

```bash
cd android
./gradlew build
./gradlew ktlintFormat
```

## Executando Testes

### Testes Backend

```bash
cd backend
pytest --cov=. --cov-report=html
./test_api.sh
```

### Testes Android

```bash
cd android
./gradlew test
./gradlew connectedAndroidTest  # Testes instrumentados
```

## Checklist antes de Submeter PR

- [ ] Código segue os padrões do projeto
- [ ] Testes foram adicionados/atualizados
- [ ] Todos os testes passam
- [ ] Documentação foi atualizada
- [ ] Commits seguem a convenção
- [ ] Branch está atualizada com `develop`
- [ ] PR descreve claramente as mudanças

## Dúvidas

Se tiver dúvidas sobre como contribuir:

1. Verifique a [documentação](README.md)
1. Consulte issues existentes
1. Abra uma issue com sua dúvida
1. Entre em contato com os mantenedores

## Agradecimentos

Toda contribuição é valiosa!
Obrigado por ajudar a tornar o Pão Nosso melhor.
