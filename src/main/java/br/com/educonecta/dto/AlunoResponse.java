package br.com.educonecta.dto;

import br.com.educonecta.model.Aluno;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Aluno cadastrado no programa de reforco escolar")
public record AlunoResponse(
        @Schema(description = "Identificador gerado pelo MongoDB", example = "6512f1a2b3c4d5e6f7a8b9c0")
        String id,
        @Schema(description = "Nome completo do aluno", example = "Maria Silva")
        String nome,
        @Schema(description = "Idade do aluno em anos", example = "10")
        int idade,
        @Schema(description = "Disciplina de reforco escolar", example = "Matematica")
        String curso,
        @Schema(description = "Turno das aulas de reforco", example = "Tarde")
        String turno,
        @Schema(description = "Indica se a matricula do aluno esta ativa", example = "true")
        boolean ativo,
        @Schema(description = "Id da turma em que o aluno esta matriculado (nulo se nao matriculado)",
                example = "6512f1a2b3c4d5e6f7a8b9c2")
        String turmaId,
        @Schema(description = "Endereco do aluno (nulo se nao informado)")
        EnderecoDto endereco,
        @Schema(description = "Responsaveis pelo aluno")
        List<ResponsavelDto> responsaveis) {

    public static AlunoResponse fromEntity(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getIdade(),
                aluno.getCurso(),
                aluno.getTurno(),
                aluno.isAtivo(),
                aluno.getTurmaId(),
                EnderecoDto.fromEntity(aluno.getEndereco()),
                aluno.getResponsaveis().stream().map(ResponsavelDto::fromEntity).toList());
    }
}
