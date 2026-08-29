# FinControl - Sistema de Controle de Finanças Pessoais

Sistema de gestão de finanças pessoais com metas e módulo fiscal, desenvolvido com Quarkus (Java) e Angular.

## Visão Geral

FinControl é uma aplicação web full stack que permite aos usuários:
- Cadastrar receitas e despesas por categoria
- Definir e acompanhar metas mensais
- Visualizar dashboard com gráficos e projeções
- Gerenciar obrigações fiscais conforme seu perfil (PF, MEI, ME)

## Stack

### Back-end
- **Linguagem:** Java 25
- **Framework:** Quarkus 3.38.3
- **ORM:** Hibernate + Panache
- **Banco:** PostgreSQL
- **Autenticação:** JWT (SmallRye)
- **Agendador:** Quarkus Scheduler

### Front-end
- **Framework:** Angular 18+
- **Linguagem:** TypeScript
- **Componentes:** Standalone Components
- **Formulários:** Reactive Forms

## Estrutura do Projeto

```
src/main/
  java/br/unitins/tp2/fincontrol/
    model/              # Entidades JPA
    dto/                # Data Transfer Objects
    resource/           # Endpoints REST
    service/            # Lógica de negócio
    repository/         # Acesso a dados
    exception/          # Exceções customizadas
    security/           # Autenticação
    scheduler/          # Agendador de tarefas
  resources/
    application.properties
    import.sql
```

## Como Executar

### Modo desenvolvimento
```bash
./mvnw quarkus:dev
```

Acesse em `http://localhost:8080`

## Configração do Banco

PostgreSQL em `localhost:5432`
