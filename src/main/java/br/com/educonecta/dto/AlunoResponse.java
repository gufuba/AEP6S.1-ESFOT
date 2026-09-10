package br.com.educonecta.dto;

import br.com.educonecta.model.Aluno;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Dados de saida representando um aluno cadastrado.
 */
@Schema(description = "Aluno cadastrado no programa de reforco escolar")
public class AlunoResponse {

    @Schema(description = "Identificador gerado pelo MongoDB", example = "6512f1a2b3c4d5e6f7a8b9c0")
    private String id;

    @Schema(description = "Nome completo do aluno", example = "Maria Silva")
    private String nome;

    @Schema(description = "Idade do aluno em anos", example = "10")
    private int idade;

    @Schema(description = "Disciplina de reforco escolar", example = "Matematica")
    private String curso;

    @Schema(description = "Turno das aulas de reforco", example = "Tarde")
    private String turno;

    @Schema(description = "Indica se a matricula do aluno esta ativa", example = "true")
    private boolean ativo;

    public AlunoResponse() {
    }

    public AlunoResponse(String id, String nome, int idade, String curso, String turno, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.curso = curso;
        this.turno = turno;
        this.ativo = ativo;
    }

    public static AlunoResponse fromEntity(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getNome(),
                aluno.getIdade(),
                aluno.getCurso(),
                aluno.getTurno(),
                aluno.isAtivo()
        );
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getIdade() {
        return idade;
    }

    public String getCurso() {
        return curso;
    }

    public String getTurno() {
        return turno;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
