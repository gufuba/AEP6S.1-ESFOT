package br.com.educonecta.dto;

import br.com.educonecta.model.Aluno;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de entrada para cadastro ou atualizacao de um aluno")
public record AlunoRequest(
        @Schema(description = "Nome completo do aluno", example = "Maria Silva")
        String nome,
        @Schema(description = "Idade do aluno em anos (minimo 6)", example = "10")
        Integer idade,
        @Schema(description = "Disciplina de reforco escolar", example = "Matematica")
        String curso,
        @Schema(description = "Turno das aulas de reforco", example = "Tarde")
        String turno) {

    public Aluno toEntity() {
        if (idade == null) {
            throw new IllegalArgumentException("Idade do aluno e obrigatoria.");
        }
        return new Aluno(nome, idade, curso, turno);
    }
}
