package br.com.educonecta.dto;

import br.com.educonecta.model.Turma;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Turma de reforco cadastrada")
public record TurmaResponse(
        @Schema(description = "Identificador gerado pelo MongoDB", example = "6512f1a2b3c4d5e6f7a8b9c2")
        String id,
        @Schema(description = "Nome da turma", example = "Matematica - Tarde A")
        String nome,
        @Schema(description = "Disciplina", example = "Matematica")
        String disciplina,
        @Schema(description = "Turno das aulas", example = "Tarde")
        String turno,
        @Schema(description = "Numero de vagas", example = "15")
        int vagas,
        @Schema(description = "Id do voluntario responsavel", example = "6512f1a2b3c4d5e6f7a8b9c1")
        String voluntarioId,
        @Schema(description = "Indica se a turma esta ativa", example = "true")
        boolean ativa) {

    public static TurmaResponse fromEntity(Turma turma) {
        return new TurmaResponse(
                turma.getId(),
                turma.getNome(),
                turma.getDisciplina(),
                turma.getTurno(),
                turma.getVagas(),
                turma.getVoluntarioId(),
                turma.isAtiva());
    }
}
