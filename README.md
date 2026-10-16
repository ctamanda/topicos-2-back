# FinControl — Backend

API REST do sistema FinControl, desenvolvida com Quarkus (Java).

## Visão Geral

Backend responsável pelo cadastro e gerenciamento de **transações financeiras** e **categorias**, com autenticação JWT e controle de acesso por perfil (ADMIN).

## Stack

- **Linguagem:** Java 25
- **Framework:** Quarkus 3.38.3
- **ORM:** Hibernate + Panache
- **Banco:** PostgreSQL 16
- **Migrations:** Flyway
- **Autenticação:** JWT (SmallRye)
- **Build:** Maven (wrapper incluso)

## Estrutura do Projeto

```
src/main/
  java/br/unitins/tp2/fincontrol/
    dto/                # Data Transfer Objects
    exception/          # Exceções customizadas e mappers
    mapper/             # Mapeamento entidade <-> DTO
    model/              # Entidades JPA
    repository/         # Acesso a dados (Panache)
    resource/           # Endpoints REST
    service/            # Lógica de negócio
  resources/
    application.properties
    db/migration/       # Migrations Flyway (V1__, V2__, ...)
```

## Como Executar

### Pré-requisitos

- Java 25
- Docker (para o PostgreSQL) **ou** PostgreSQL local rodando em `localhost:5432`

### 1. Subir o banco

```bash
docker-compose up -d
```

Isso sobe um container PostgreSQL com as credenciais definidas em `application.properties`:
- Database: `fincontroldb`
- User: `fincontrol`
- Password: `123456`

### 2. Configurar as chaves JWT

A aplicação usa **RSA** para assinar e verificar tokens JWT. A chave **privada** nunca deve ser versionada. A chave **pública** está em `src/main/resources/publicKey.pem`.

Para rodar localmente, você precisa gerar um par de chaves **compatível** com a pública do repo, ou configurar suas próprias chaves via variáveis de ambiente.

**Opção A — Gerar chaves próprias:**

```bash
mkdir -p ~/.fincontrol
cd ~/.fincontrol
openssl genpkey -algorithm RSA -out privateKey.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in privateKey.pem -out publicKey.pem
```

Depois configure as variáveis de ambiente:

```
JWT_PRIVATE_KEY_LOCATION=file:///C:/Users/<seu-usuario>/.fincontrol/privateKey.pem
JWT_PUBLIC_KEY_LOCATION=file:///C:/Users/<seu-usuario>/.fincontrol/publicKey.pem
```

(no Linux/Mac: `file:///home/<seu-usuario>/.fincontrol/privateKey.pem`)

**Opção B — Usar a chave pública do repo:**

Se você tem acesso à chave privada correspondente ao `publicKey.pem` versionado, apenas configure `JWT_PRIVATE_KEY_LOCATION` apontando para ela.

### 3. Subir a aplicação

```bash
./mvnw quarkus:dev
```

Acesse em `http://localhost:8080`

- Swagger UI: `http://localhost:8080/q/swagger-ui`
- Dev UI: `http://localhost:8080/q/dev-ui`

## Segurança

- Endpoints sob `/admin/**` exigem token JWT com `role: ADMIN`.
- A chave privada JWT **não** é versionada — veja a seção "Configurar as chaves JWT".
- Chaves `.pem` (exceto `publicKey.pem`) estão bloqueadas no `.gitignore`.

## Migrations

As migrations ficam em `src/main/resources/db/migration/` e seguem o padrão Flyway:

- `V1__...sql`
- `V2__...sql`

**Migrations já mergeadas na `main` nunca são editadas** — cria-se uma nova por cima.

## Configuração do Banco (produção)

Em produção, o banco é configurado via variáveis de ambiente:

- `DATABASE_URL`
- `DATABASE_USER`
- `DATABASE_PASSWORD`