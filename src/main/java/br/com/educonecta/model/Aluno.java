package br.com.educonecta.model;

import java.util.Objects;

/**
 * Representa um aluno inscrito no programa de reforco escolar gratuito.
 * Estrutura simples e homogenea, conforme escopo da primeira entrega da AEP.
 */
public class Aluno {

    private String id;
    private String nome;
    private int idade;
    private String curso;
    private String turno;
    private boolean ativo;

    public Aluno(String nome, int idade, String curso, String turno) {
        this(null, nome, idade, curso, turno, true);
    }

    public Aluno(String id, String nome, int idade, String curso, String turno, boolean ativo) {
        setNome(nome);
        setIdade(idade);
        setCurso(curso);
        setTurno(turno);
        this.id = id;
        this.ativo = ativo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do aluno nao pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        if (idade < 0 || idade > 120) {
            throw new IllegalArgumentException("Idade informada e invalida.");
        }
        this.idade = idade;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        if (curso == null || curso.isBlank()) {
            throw new IllegalArgumentException("Curso/disciplina de reforco nao pode ser vazio.");
        }
        this.curso = curso.trim();
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        if (turno == null || turno.isBlank()) {
            throw new IllegalArgumentException("Turno nao pode ser vazio.");
        }
        this.turno = turno.trim();
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aluno)) return false;
        Aluno aluno = (Aluno) o;
        return Objects.equals(id, aluno.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Aluno{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", idade=" + idade +
                ", curso='" + curso + '\'' +
                ", turno='" + turno + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
