<p align="center">
  <img src="docs/logo.svg" alt="FinControl" width="96" />
</p>

<h1 align="center">FinControl — Backend (API)</h1>

<p align="center">
  API REST do FinControl, sistema de controle financeiro para pessoas, MEIs e microempresas.<br />
  Projeto da disciplina de Tópicos II.
</p>

<p align="center">
  <a href="https://github.com/ctamanda/topicos-2-front">Frontend (Angular)</a> ·
  <a href="https://ctamanda.github.io/topicos-2-front/">Frontend online</a>
</p>

---

## Sumário

- [Visão geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Como executar](#como-executar)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [Autenticação e perfis](#autenticação-e-perfis)
- [Endpoints](#endpoints)
- [Formato de erros](#formato-de-erros)
- [Regras de negócio](#regras-de-negócio)
- [Modelo de dados](#modelo-de-dados)
- [Migrations](#migrations)
- [Segurança e LGPD](#segurança-e-lgpd)
- [Testes automatizados](#testes-automatizados)
- [Docker](#docker)

---

## Visão geral

A API controla **usuários, transações (receitas e despesas), categorias** e **parâmetros fiscais**, com autenticação JWT e dois perfis de acesso:

| Perfil | Acesso |
|---|---|
| **USER** | Os próprios dados: transações, categorias e perfil |
| **ADMIN** | O mesmo que o USER (com os próprios dados) + estatísticas **agregadas** do sistema e CRUD de parâmetros fiscais |

Os dados de cada usuário ficam **isolados**: toda consulta é filtrada pelo dono, inclusive para o administrador.

## Tecnologias

| | |
|---|---|
| Linguagem | **Java 25** |
| Framework | **Quarkus 3.38.3** (Quarkus REST + Jackson) |
| Persistência | Hibernate ORM + Panache |
| Banco | **PostgreSQL 16** (H2 em memória no perfil de teste) |
| Migrations | Flyway |
| Segurança | SmallRye JWT (assinatura RSA) + BCrypt |
| Validação | Hibernate Validator (Bean Validation) |
| Documentação | SmallRye OpenAPI / Swagger UI |
| Build | Maven (wrapper incluso) |

## Arquitetura

Camadas clássicas, uma pasta para cada:

```
src/main/java/br/unitins/tp2/fincontrol/
├── resource/     # Endpoints REST (JAX-RS): recebem a requisição, pegam o usuário do JWT
├── service/      # Regras de negócio e transações (@Transactional)
├── repository/   # Consultas Panache (sempre filtrando por usuário quando o dado é privado)
├── model/        # Entidades JPA e enums
├── dto/          # Records de entrada (RequestDTO, com validação) e saída (ResponseDTO)
├── mapper/       # Conversão entidade → DTO de resposta
└── exception/    # Exceções e ExceptionMappers no formato RFC 7807
src/main/resources/
├── application.properties
├── publicKey.pem             # chave pública JWT (a privada NÃO é versionada)
└── db/migration/             # V4__ ... V10__
```

Fluxo de uma requisição autenticada:

```
HTTP + "Authorization: Bearer <jwt>"
  → SmallRye JWT valida assinatura, emissor e validade
  → @RolesAllowed confere o perfil (groups)
  → Resource lê o e-mail do token (upn) e busca o Usuario
  → Service aplica as regras usando esse usuário
  → Repository consulta sempre com "usuario.id = ?"
```

## Como executar

### Pré-requisitos

- **Java 25** (JDK)
- **Docker** (para o PostgreSQL) **ou** um PostgreSQL próprio

### 1. Subir o banco

```bash
docker compose up -d
```

Sobe o PostgreSQL 16 com banco `fincontroldb`, usuário `fincontrol` e senha `123456`, na porta **5432**.

> **Já tem um PostgreSQL instalado na 5432?** Suba o container em outra porta e aponte o backend para ela:
> ```powershell
> $env:DB_PORT="5433"; docker compose up -d
> $env:DATABASE_URL="jdbc:postgresql://localhost:5433/fincontroldb"
> ```

### 2. Configurar as chaves JWT (uma vez)

O token é assinado com uma chave **privada RSA**, que **não vai para o Git** (bloqueada no `.gitignore`). Cada desenvolvedor gera o seu par de chaves (no Git Bash, Linux ou Mac):

```bash
mkdir -p ~/.fincontrol && cd ~/.fincontrol
openssl genpkey -algorithm RSA -out privateKey.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in privateKey.pem -out publicKey.pem
```

Depois, aponte as variáveis de ambiente para os arquivos. No Windows (PowerShell), troque `SEU_USUARIO`:

```powershell
setx JWT_PRIVATE_KEY_LOCATION "file:///C:/Users/SEU_USUARIO/.fincontrol/privateKey.pem"
setx JWT_PUBLIC_KEY_LOCATION  "file:///C:/Users/SEU_USUARIO/.fincontrol/publicKey.pem"
```

Feche e abra o terminal para valer. No Linux ou Mac, use `export` com `file:///home/SEU_USUARIO/...`.

### 3. Iniciar

```bash
./mvnw quarkus:dev        # Linux/Mac
.\mvnw.cmd quarkus:dev    # Windows
```

Na primeira execução, o Flyway cria as tabelas e o **usuário administrador**.

| Endereço | O que é |
|---|---|
| http://localhost:8080 | API |
| http://localhost:8080/q/swagger-ui | Swagger UI (testar os endpoints) |
| http://localhost:8080/q/dev-ui | Dev UI do Quarkus |

### Usuário administrador inicial

| E-mail | Senha |
|---|---|
| `admin@fincontrol.com` | `admin123` |

Criado pela migration `V10__cria_admin.sql`. **Troque a senha antes de qualquer uso fora do ambiente acadêmico.**

## Variáveis de ambiente

| Variável | Padrão | Para quê |
|---|---|---|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/fincontroldb` | URL JDBC do banco |
| `DATABASE_USER` | `fincontrol` | Usuário do banco |
| `DATABASE_PASSWORD` | `123456` | Senha do banco |
| `JWT_PRIVATE_KEY_LOCATION` | `privateKey.pem` (classpath) | Chave privada que **assina** os tokens |
| `JWT_PUBLIC_KEY_LOCATION` | `publicKey.pem` (classpath) | Chave pública que **valida** os tokens |
| `PROBLEM_BASE_URL` | `https://api.fincontrol.com` | Base do campo `type` dos erros |
| `DB_PORT` | `5432` | Porta do PostgreSQL no host (só no `docker-compose.yml`) |

**CORS** liberado para `http://localhost:4200` (ng serve) e `https://ctamanda.github.io` (GitHub Pages).

## Autenticação e perfis

1. `POST /auth/login` com e-mail e senha.
2. A resposta traz `{ "token": "...", "expiresIn": 3600 }`. O token vale **1 hora**.
3. Envie o token em todas as rotas protegidas:
   ```
   Authorization: Bearer eyJ0eXAiOiJKV1Qi...
   ```

Conteúdo do token:

| Claim | Exemplo | Significado |
|---|---|---|
| `iss` | `fincontrol` | Emissor (validado pelo backend) |
| `upn` | `maria@email.com` | E-mail do usuário: é por ele que a API identifica quem chama |
| `groups` | `["USER"]` | Perfil (`USER` ou `ADMIN`), usado pelo `@RolesAllowed` |
| `exp` | `1792200352` | Validade (1 hora após o login) |

> Todo cadastro feito por `/auth/register` cria um **USER**. O ADMIN inicial vem da migration V10.

## Endpoints

Legenda de acesso: 🌐 público · 👤 USER ou ADMIN · 🛡️ só ADMIN

### Autenticação — `/auth`

| Método | Rota | Acesso | Corpo | Resposta |
|---|---|---|---|---|
| POST | `/auth/register` | 🌐 | `RegisterRequestDTO` | **201** `UsuarioResponseDTO` · **400** e-mail já cadastrado |
| POST | `/auth/login` | 🌐 | `LoginRequestDTO` | **200** `{ token, expiresIn }` · **401** credenciais inválidas |

```jsonc
// POST /auth/register
{
  "email": "maria@email.com",
  "nome": "Maria Souza",
  "password": "123456",          // 6 a 100 caracteres
  "perfilFiscal": "MEI",         // PF | MEI | ME
  "atividadeMei": "SERVICO"      // COMERCIO | SERVICO | AMBOS — obrigatório só para MEI
}

// POST /auth/login
{ "email": "maria@email.com", "password": "123456" }
```

### Perfil do usuário — `/usuarios/me`

| Método | Rota | Acesso | Corpo | Resposta |
|---|---|---|---|---|
| GET | `/usuarios/me` | 👤 | — | **200** `UsuarioResponseDTO` |
| PUT | `/usuarios/me` | 👤 | `UsuarioUpdateDTO` | **200** `UsuarioResponseDTO` |

```jsonc
// UsuarioResponseDTO
{ "id": 2, "email": "maria@email.com", "nome": "Maria Souza",
  "perfilFiscal": "MEI", "atividadeMei": "SERVICO", "role": "USER" }

// UsuarioUpdateDTO (e-mail e perfil de acesso não mudam por aqui)
{ "nome": "Maria S. Souza", "perfilFiscal": "MEI", "atividadeMei": "AMBOS" }
```

### Transações — `/transacoes`

| Método | Rota | Acesso | Corpo | Resposta |
|---|---|---|---|---|
| GET | `/transacoes?page=0&pageSize=10&descricao=` | 👤 | — | **200** `PageResponse<TransacaoResponseDTO>` |
| GET | `/transacoes/{id}` | 👤 | — | **200** · **404** se não existir ou não for do usuário |
| POST | `/transacoes` | 👤 | `TransacaoRequestDTO` | **201** `TransacaoResponseDTO` |
| PUT | `/transacoes/{id}` | 👤 | `TransacaoRequestDTO` | **204** |
| DELETE | `/transacoes/{id}` | 👤 | — | **204** |

- `page` começa em 0. `pageSize` vai de 1 a **100**.
- `descricao` é opcional e filtra por trecho, sem diferenciar maiúsculas.
- A lista vem ordenada por data, da mais recente para a mais antiga.

```jsonc
// TransacaoRequestDTO
{
  "descricao": "Mercado",        // 2 a 150 caracteres
  "valor": 152.37,               // > 0, até 2 casas
  "data": "2026-10-10",          // yyyy-MM-dd
  "tipo": "DESPESA",             // RECEITA | DESPESA
  "escopo": "PESSOAL",           // PESSOAL | EMPRESA
  "idCategoria": 3               // categoria do próprio usuário e do mesmo tipo (despesa/receita)
}

// PageResponse
{ "items": [ { "id": 1, "descricao": "Mercado", "valor": 152.37, "data": "2026-10-10",
               "tipo": "DESPESA", "escopo": "PESSOAL",
               "categoria": { "id": 3, "nome": "Alimentação", "tipo": "DESPESA", "cor": "#FF5733" } } ],
  "page": 0, "pageSize": 10, "totalItems": 1, "totalPages": 1 }
```

### Categorias — `/categorias`

| Método | Rota | Acesso | Corpo | Resposta |
|---|---|---|---|---|
| GET | `/categorias` | 👤 | — | **200** lista ordenada por nome (sem paginação) |
| GET | `/categorias/{id}` | 👤 | — | **200** · **404** |
| POST | `/categorias` | 👤 | `CategoriaRequestDTO` | **201** `CategoriaResponseDTO` |
| PUT | `/categorias/{id}` | 👤 | `CategoriaRequestDTO` | **200** · **400** se mudar o tipo de uma categoria com transações |
| DELETE | `/categorias/{id}` | 👤 | — | **204** · **400** se houver transações na categoria |

```jsonc
// CategoriaRequestDTO
{ "nome": "Saúde", "tipo": "DESPESA", "cor": "#8E24AA" }   // nome 2–60; cor hexadecimal opcional
```

### Administração — `/admin`

| Método | Rota | Acesso | Corpo | Resposta |
|---|---|---|---|---|
| GET | `/admin/estatisticas` | 🛡️ | — | **200** `EstatisticaResponseDTO` |
| GET | `/admin/parametros` | 🛡️ | — | **200** lista (por chave e vigência) |
| GET | `/admin/parametros/{id}` | 🛡️ | — | **200** · **404** |
| POST | `/admin/parametros` | 🛡️ | `ParametroFiscalRequestDTO` | **201** |
| PUT | `/admin/parametros/{id}` | 🛡️ | `ParametroFiscalRequestDTO` | **200** |
| DELETE | `/admin/parametros/{id}` | 🛡️ | — | **204** |

```jsonc
// EstatisticaResponseDTO — só números agregados, nenhum dado individual
{ "totalUsuarios": 5, "totalAdmins": 1, "totalUsers": 4,
  "totalTransacoes": 6, "totalCategorias": 31, "mediaTransacoesPorUsuario": 1.2 }

// ParametroFiscalRequestDTO
{ "chave": "LIMITE_FATURAMENTO_MEI", "valor": 81000.00,
  "vigenciaInicio": "2026-01-01", "vigenciaFim": "2026-12-31" }
```

## Formato de erros

Os erros da aplicação seguem o padrão **RFC 7807 / 9457** (`application/problem+json`):

```json
{
  "type": "http://localhost:8080/errors/validation-error",
  "title": "Erro de validação",
  "status": 400,
  "detail": "Um ou mais campos não passaram na validação.",
  "instance": "/transacoes",
  "timestamp": "2026-10-16T23:39:56-03:00",
  "errors": [
    { "field": "descricao", "message": "O campo deve conter entre 2 e 150 caracteres." }
  ]
}
```

| Status | Quando |
|---|---|
| 400 | Validação de campos (`errors`), regra de negócio (`detail`), corpo ausente ou JSON inválido (ex.: enum inexistente lista os valores aceitos; data fora de `aaaa-mm-dd`) |
| 401 | Sem token, token inválido ou expirado (**sem corpo**, vem da camada de segurança); senha errada no login (com corpo RFC 7807) |
| 403 | Perfil sem permissão, ex.: USER em `/admin/*` (**sem corpo**) |
| 404 | Recurso inexistente **ou de outro usuário** (não revela que existe) |
| 409 | Conflito no banco em requisições simultâneas (ex.: dois cadastros com o mesmo e-mail ao mesmo tempo) |
| 500 | Erro inesperado (detalhe só no log do servidor) |

## Regras de negócio

| Regra | Onde |
|---|---|
| E-mail único, **sem diferenciar maiúsculas** (gravado em minúsculas; login aceita qualquer caixa) | `UsuarioService` + índice `uk_usuario_email_lower` (V11) |
| Perfil **MEI** exige `atividadeMei`; nos outros perfis ela é descartada | `UsuarioService` (cadastro e edição) |
| Senha guardada com **BCrypt** | `UsuarioService.criar` |
| Todo usuário novo ganha 6 categorias: Alimentação, Moradia, Transporte, Lazer (despesas), Salário e Vendas (receitas) | `UsuarioService.criarCategoriasIniciais` |
| Nome de categoria único **por usuário**, sem diferenciar maiúsculas e ignorando espaços nas pontas | `CategoriaServiceImpl` + constraint `uk_categoria_nome_usuario` |
| Cor de categoria vazia vira `null`; cor informada é gravada em maiúsculas | `CategoriaServiceImpl` |
| Não dá para **mudar o tipo** de uma categoria que já tem transações | `CategoriaServiceImpl` |
| Categoria com transações vinculadas **não pode ser excluída** | `CategoriaServiceImpl.delete` |
| Transação só aceita categoria **do próprio usuário** e **do mesmo tipo** (despesa → categoria de despesa) | `TransacaoServiceImpl` |
| Paginação: `page ≥ 0` e `1 ≤ pageSize ≤ 100` | `TransacaoServiceImpl.validarPaginacao` |
| Parâmetro fiscal: `vigenciaFim ≥ vigenciaInicio` | DTO (`@AssertTrue`) + `ParametroFiscalServiceImpl` |
| Parâmetro fiscal: chave normalizada em `MAIUSCULAS_COM_UNDERLINE` (`"teto mei"` → `TETO_MEI`) | `ParametroFiscalServiceImpl.normalizarChave` |
| Parâmetro fiscal: **sem sobreposição** de vigência para a mesma chave | `ParametroFiscalServiceImpl.verificarSobreposicao` |
| Token de uma conta que não existe mais responde **401** | `UsuarioService.buscarLogado` |

## Modelo de dados

```
usuario 1 ───< categoria          (categoria.usuario_id)
usuario 1 ───< transacao          (transacao.usuario_id)
categoria 1 ─< transacao          (transacao.id_categoria)
parametro_fiscal                  (global, administrado pelo ADMIN)
```

| Tabela | Colunas principais |
|---|---|
| `usuario` | `email` (único, sem diferenciar maiúsculas), `nome`, `password` (BCrypt), `perfil_fiscal`, `atividade_mei`, `role` |
| `categoria` | `nome`, `tipo`, `cor`, `usuario_id` · único (`nome`, `usuario_id`) |
| `transacao` | `descricao`, `valor` NUMERIC(15,2), `data`, `tipo`, `escopo`, `id_categoria`, `usuario_id` |
| `parametro_fiscal` | `chave`, `valor` NUMERIC(15,2), `vigencia_inicio`, `vigencia_fim` · único (`chave`, `vigencia_inicio`) |

Todas as entidades herdam de `DefaultEntity`: `id` (IDENTITY), `dataCadastro` e `dataAlteracao`, preenchidas automaticamente.

**Enums** (gravados como texto):

| Enum | Valores |
|---|---|
| `Role` | `ADMIN`, `USER` |
| `PerfilFiscal` | `PF`, `MEI`, `ME` |
| `AtividadeMei` | `COMERCIO`, `SERVICO`, `AMBOS` |
| `TipoTransacao` / `TipoCategoria` | `RECEITA`, `DESPESA` |
| `Escopo` | `PESSOAL`, `EMPRESA` |

## Migrations

Quem cria e altera tabelas é o **Flyway**; o Hibernate só valida (`database.generation=validate`).

| Versão | O que faz |
|---|---|
| V4 | Cria `transacao` (e `categoria`, se ainda não existir) |
| V5 | Cria `usuario` |
| V6 | Adiciona o dono (`usuario_id`) em `transacao` |
| V7 | Renomeia `username` → `email`; adiciona `nome`, `perfil_fiscal` e `atividade_mei` |
| V8 | Adiciona o dono (`usuario_id`) em `categoria` + nome único por usuário |
| V9 | Cria `parametro_fiscal` |
| V10 | Cria o usuário **ADMIN** inicial e as 6 categorias dele |
| V11 | Padroniza e-mails em minúsculas e cria índice único que ignora maiúsculas |

> **Regra do time:** migration que já foi para a `main` **nunca é editada**. Para mudar algo, crie uma nova (`V12__...`).

## Segurança e LGPD

- **Senhas** com BCrypt; nunca saem nas respostas.
- **JWT RSA** com validade de 1 hora; a chave privada fica fora do repositório.
- **Isolamento por usuário:** transações e categorias são sempre buscadas com o id do dono, que vem do token. Um id de outro usuário responde **404**, como se não existisse.
- **Administrador sem acesso a dados individuais:** `/admin/estatisticas` devolve apenas totais e médias, e não existe endpoint de listagem de usuários.
- **Logs:** a exibição de SQL com parâmetros reais vale só no perfil `dev`.

## Testes automatizados

Testes de ponta a ponta em `src/test/java/.../ApiTest.java` (JUnit 5 + RestAssured), rodando com **H2 em memória**. Não precisam de Docker nem de PostgreSQL, e as chaves JWT são geradas na hora por `ChavesJwtTeste`.

```bash
./mvnw test        # Linux/Mac
.\mvnw.cmd test   # Windows
```

Cobrem 24 cenários:
- cadastro e login (e-mail sem diferenciar maiúsculas, senha errada, MEI sem atividade, categorias iniciais);
- erros de entrada (corpo ausente, JSON malformado, enum inválido, sem token);
- isolamento entre usuários (ler, editar e excluir dados de outra pessoa; usar categoria alheia);
- regras de transação, categoria, perfil e paginação;
- área administrativa (403 para USER, estatísticas, parâmetros fiscais com chave normalizada e sem sobreposição).

> Rodar `mvnw test` com o `quarkus:dev` aberto faz o modo dev reiniciar (as classes são recompiladas). Espere alguns segundos antes da próxima requisição.

## Docker

O `Dockerfile` gera a imagem da API em duas etapas (build com JDK 25 e execução com JRE 25):

```bash
docker build -t fincontrol-api .
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/fincontroldb \
  -e JWT_PRIVATE_KEY_LOCATION=file:///keys/privateKey.pem \
  -e JWT_PUBLIC_KEY_LOCATION=file:///keys/publicKey.pem \
  -v ~/.fincontrol:/keys \
  fincontrol-api
```

O `docker-compose.yml` sobe só o banco; a API roda com `quarkus:dev` durante o desenvolvimento.
