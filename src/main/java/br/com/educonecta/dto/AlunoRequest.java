package br.com.educonecta.dto;

import br.com.educonecta.model.Aluno;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Dados de entrada para cadastro ou atualizacao (substituicao completa) de um aluno")
public record AlunoRequest(
        @Schema(description = "Nome completo do aluno", example = "Maria Silva")
        String nome,
        @Schema(description = "Idade do aluno em anos (minimo 6)", example = "10")
        Integer idade,
        @Schema(description = "Disciplina de reforco escolar", example = "Matematica")
        String curso,
        @Schema(description = "Turno das aulas de reforco", example = "Tarde")
        String turno,
        @Schema(description = "Matricula ativa (opcional, padrao true)", example = "true")
        Boolean ativo,
        @Schema(description = "Endereco do aluno (opcional)")
        EnderecoDto endereco,
        @Schema(description = "Responsaveis pelo aluno (obrigatorio ao menos um para menores de 18 anos)")
        List<ResponsavelDto> responsaveis) {

    public Aluno toEntity() {
        if (idade == null) {
            throw new IllegalArgumentException("Idade do aluno e obrigatoria.");
        }
        Aluno aluno = new Aluno(nome, idade, curso, turno);
        aluno.setAtivo(ativo == null || ativo);
        if (endereco != null) {
            aluno.setEndereco(endereco.toEntity());
        }
        aluno.setResponsaveis(ResponsavelDto.toEntities(responsaveis));
        return aluno;
    }
}
