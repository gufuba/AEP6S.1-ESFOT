# EduConecta — Quadro de Tarefas (Kanban)

Metodologia de trabalho: **Kanban simplificado**, com o trabalho dividido em cartões pequenos e
verificáveis que percorrem as colunas **A fazer → Em andamento → Concluído**. Cada cartão concluído
aponta para o commit que o entregou, de modo que o quadro e o histórico do Git se confirmam.

**Definição de pronto** (um cartão só vai para *Concluído* quando):

1. o código está implementado em camadas e com as validações do domínio;
2. existem testes automatizados para o comportamento novo;
3. `mvn clean verify` está verde, com cobertura acima de 70% (regra do build);
4. a mudança está em um commit com mensagem descritiva;
5. a documentação foi atualizada quando o comportamento ou o modelo de dados mudou.

## A fazer

| Cartão | Descrição |
|---|---|
| T-16 | Publicar a versão final: enviar os commits ao GitHub e criar a tag `entrega-2` |
| T-17 | Gravar o vídeo de demonstração (3 a 5 minutos): evolução, arquitetura final, execução, tecnologias, testes e resultados |
| T-18 | Publicar o vídeo no YouTube e preencher o modelo de entrega (links do vídeo e do GitHub) |

## Em andamento

| Cartão | Descrição |
|---|---|
| — | Nenhum cartão em andamento |

## Concluído — 1ª entrega

| Cartão | Descrição | Evidência |
|---|---|---|
| T-01 | Definir o problema e o alinhamento com o ODS 4 (metas 4.1 e 4.5) | `README.md` (`9a610f1`) |
| T-02 | Modelar a coleção `alunos` e implementar o CRUD em camadas (model, repository, service, controller) | `b1559bb` |
| T-03 | Expor a API REST documentada com Swagger | `b1559bb` |
| T-04 | Testes automatizados e cobertura mínima de 70% com JaCoCo | `6446bde` |
| T-05 | README inicial e tag `entrega-1` | `9a610f1`, tag `entrega-1` |

## Concluído — 2ª entrega

| Cartão | Descrição | Evidência |
|---|---|---|
| T-06 | Refatorar a base (entidade base, repositório genérico, exceções genéricas) e corrigir falhas: idade mínima burlável no `PUT` e mensagem de `id` inválido expondo texto do driver | `24c1821` |
| T-07 | Aluno com endereço (subdocumento) e responsáveis (lista de subdocumentos), com a regra de menor de idade | `99a4f6b` |
| T-08 | Nova coleção `voluntarios`: CRUD, disciplinas em array e e-mail único com índice | `26845e1` |
| T-09 | Nova coleção `turmas`, relacionamento entre coleções (turma → voluntário, aluno → turma), matrículas, integridade referencial, índices e Swagger organizado | `d4e229d` |
| T-10 | Fechar lacunas de cobertura (igualdade das entidades e DTOs) | `753550c` |
| T-11 | Medir a cobertura sobre todo o código e testar configuração e contexto Spring (teste de integração) | `380516a` |
| T-12 | Validar o fluxo completo contra um MongoDB real (45 verificações reproduzíveis) | `375185a`, `scripts/teste-e2e.ps1` |
| T-13 | Documentação técnica completa | `e291f03`, `docs/DOCUMENTACAO_TECNICA.md` |
| T-14 | Quadro de tarefas | este documento |
| T-15 | Atualizar o README para a 2ª entrega | commit de atualização do `README.md` |

## Resultado ao final da 2ª entrega

| Indicador | Valor |
|---|---|
| Coleções no MongoDB | 3 (`alunos`, `voluntarios`, `turmas`) |
| Relacionamentos entre coleções | 2 |
| Endpoints REST | 18 |
| Testes automatizados | 195 |
| Cobertura (instruções, todo o código) | 99,8% (mínimo exigido: 70%) |
