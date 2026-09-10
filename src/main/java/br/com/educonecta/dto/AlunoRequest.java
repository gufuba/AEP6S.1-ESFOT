package br.com.educonecta.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Dados de entrada para cadastro/atualizacao de um aluno.
 * A validacao efetiva ocorre no construtor de {@link br.com.educonecta.model.Aluno}.
 */
@Schema(description = "Dados de entrada para cadastro ou atualizacao de um aluno")
public class AlunoRequest {

    @Schema(description = "Nome completo do aluno", example = "Maria Silva")
    private String nome;

    @Schema(description = "Idade do aluno em anos", example = "10")
    private int idade;

    @Schema(description = "Disciplina de reforco escolar", example = "Matematica")
    private String curso;

    @Schema(description = "Turno das aulas de reforco", example = "Tarde")
    private String turno;

    public AlunoRequest() {
    }

    public AlunoRequest(String nome, int idade, String curso, String turno) {
        this.nome = nome;
        this.idade = idade;
        this.curso = curso;
        this.turno = turno;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }
}
