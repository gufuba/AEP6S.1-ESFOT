package br.com.educonecta.dto;

import br.com.educonecta.model.Turma;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de entrada para cadastro ou atualizacao (substituicao completa) de uma turma")
public record TurmaRequest(
        @Schema(description = "Nome da turma", example = "Matematica - Tarde A")
        String nome,
        @Schema(description = "Disciplina (deve ser lecionada pelo voluntario responsavel)", example = "Matematica")
        String disciplina,
        @Schema(description = "Turno das aulas", example = "Tarde")
        String turno,
        @Schema(description = "Numero de vagas (1 a 100)", example = "15")
        Integer vagas,
        @Schema(description = "Id do voluntario responsavel (referencia para a colecao voluntarios)",
                example = "6512f1a2b3c4d5e6f7a8b9c1")
        String voluntarioId,
        @Schema(description = "Turma ativa (opcional, padrao true)", example = "true")
        Boolean ativa) {

    public Turma toEntity() {
        if (vagas == null) {
            throw new IllegalArgumentException("Numero de vagas da turma e obrigatorio.");
        }
        Turma turma = new Turma(nome, disciplina, turno, vagas, voluntarioId);
        turma.setAtiva(ativa == null || ativa);
        return turma;
    }
}
