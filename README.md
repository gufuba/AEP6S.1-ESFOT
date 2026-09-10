# EduConecta — Cadastro de Alunos para Reforço Escolar Gratuito

Prova de Conceito (PoC) desenvolvida para a Atividade de Estudo Programada (AEP) de
Engenharia de Software — UniCesumar, 6º semestre (2026.2). API REST documentada e
testável via **Swagger UI**, sem front-end nesta entrega.

## 1. Problema

Crianças e adolescentes em situação de vulnerabilidade socioeconômica frequentemente
não têm acesso a apoio pedagógico fora do horário regular de aula. Para suprir essa
lacuna, ONGs e coletivos comunitários organizam programas de reforço escolar
gratuito, mas em geral controlam as inscrições em planilhas soltas ou de memória —
o que causa perda de histórico quando voluntários trocam, dificulta saber quantos
alunos estão ativos por turma/turno, e prejudica a prestação de contas a financiadores.

O **EduConecta** ataca esse problema operacional com uma API para cadastrar,
consultar, atualizar e remover alunos inscritos em programas de reforço escolar
gratuito de forma centralizada e persistente.

## 2. ODS Atendido

**ODS 4 — Educação de Qualidade**, em especial as metas **4.1** (garantir ensino
primário/secundário gratuito e de qualidade) e **4.5** (eliminar disparidades de
acesso à educação para os mais vulneráveis). O EduConecta não substitui a ação
pedagógica dos voluntários — remove a barreira operacional que limita o alcance e a
continuidade desses programas.

## 3. Tecnologias Utilizadas

| Camada              | Tecnologia                                        |
|---------------------|-----------------------------------------------------|
| Linguagem           | Java 21 (LTS) — Programação Orientada a Objetos     |
| Framework REST      | Spring Boot 3.5                                     |
| Documentação da API | Swagger / OpenAPI (springdoc-openapi)               |
| Banco de dados      | MongoDB (Community Edition, local)                  |
| Driver MongoDB      | mongodb-driver-sync 5.5.1                           |
| Build               | Maven                                               |
| Testes              | JUnit 5 + Mockito + Spring MockMvc                  |
| Cobertura de testes | JaCoCo (mínimo de 70% configurado no `pom.xml`)     |

## 4. Escopo desta Entrega (1ª Entrega)

Conforme o escopo da primeira entrega: uma única coleção NoSQL (`alunos`,
no banco `educonecta`), objetos homogêneos e de estrutura simples, com CRUD básico
exposto via API REST.

```json
{
  "_id": "ObjectId(...)",
  "nome": "Maria Silva",
  "idade": 10,
  "curso": "Matemática",
  "turno": "Tarde",
  "ativo": true
}
```

## 5. Como Executar

**Pré-requisitos:** Java 21, Maven 3.9+ e MongoDB Community Edition rodando em
`localhost:27017` (opcionalmente visualizável pelo MongoDB Compass).

```bash
mvn clean package
java -jar target/educonecta-poc.jar
```

Ou direto pela IDE: rode a classe `EduConectaApplication`. Com a API no ar, acesse:

```
http://localhost:8080/swagger-ui.html
```

Endpoints disponíveis:

| Método | Endpoint            | O que faz                    |
|--------|----------------------|-------------------------------|
| POST   | `/api/alunos`        | Cadastra um novo aluno        |
| GET    | `/api/alunos`        | Lista todos os alunos         |
| GET    | `/api/alunos/{id}`   | Busca um aluno pelo id        |
| PUT    | `/api/alunos/{id}`   | Atualiza os dados de um aluno |
| DELETE | `/api/alunos/{id}`   | Remove um aluno               |

Basta abrir cada endpoint no Swagger, clicar em **"Try it out"** e **"Execute"** —
os campos já vêm com exemplo preenchido.

## 6. Como Rodar os Testes e Gerar o Relatório de Cobertura

Os testes são automatizados e **não exigem** MongoDB em execução (banco mockado com
Mockito; API testada com MockMvc).

```bash
mvn clean verify
```

Executa os testes, gera o relatório em `target/site/jacoco/index.html` e **falha o
build automaticamente** se a cobertura ficar abaixo de 70% (regra no `pom.xml`).

> As classes `EduConectaApplication` e `config/MongoConfig` são excluídas da métrica
> por serem apenas bootstrap/infraestrutura, sem regra de negócio própria.

## 7. Versão desta Entrega

Esta versão corresponde à **Primeira Entrega** da AEP (tag `entrega-1`).
