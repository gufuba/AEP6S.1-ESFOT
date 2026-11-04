package br.com.educonecta.model;

import java.util.List;
import java.util.Objects;

/**
 * Representa um aluno inscrito no programa de reforco escolar gratuito.
 * Possui um endereco (subdocumento) e uma lista de responsaveis (subdocumentos).
 */
public class Aluno extends Entidade {

    public static final int IDADE_MAXIMA = 120;
    public static final int MAIORIDADE = 18;

    private String nome;
    private int idade;
    private String curso;
    private String turno;
    private boolean ativo;
    private Endereco endereco;
    private List<Responsavel> responsaveis = List.of();

    public Aluno(String nome, int idade, String curso, String turno) {
        this(null, nome, idade, curso, turno, true);
    }

    public Aluno(String id, String nome, int idade, String curso, String turno, boolean ativo) {
        setNome(nome);
        setIdade(idade);
        setCurso(curso);
        setTurno(turno);
        setId(id);
        this.ativo = ativo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Validacoes.textoObrigatorio(nome, "Nome do aluno nao pode ser vazio.");
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        if (idade < 0 || idade > IDADE_MAXIMA) {
            throw new IllegalArgumentException("Idade informada e invalida.");
        }
        this.idade = idade;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = Validacoes.textoObrigatorio(curso, "Curso/disciplina de reforco nao pode ser vazio.");
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = Validacoes.textoObrigatorio(turno, "Turno nao pode ser vazio.");
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public List<Responsavel> getResponsaveis() {
        return responsaveis;
    }

    public void setResponsaveis(List<Responsavel> responsaveis) {
        if (responsaveis == null) {
            this.responsaveis = List.of();
            return;
        }
        if (responsaveis.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Responsavel nao pode ser nulo.");
        }
        this.responsaveis = List.copyOf(responsaveis);
    }

    public boolean isMenorDeIdade() {
        return idade < MAIORIDADE;
    }

    public void atualizarDados(Aluno novosDados) {
        setNome(novosDados.nome);
        setIdade(novosDados.idade);
        setCurso(novosDados.curso);
        setTurno(novosDados.turno);
        setAtivo(novosDados.ativo);
        setEndereco(novosDados.endereco);
        setResponsaveis(novosDados.responsaveis);
    }

    @Override
    public String toString() {
        return "Aluno{" +
                "id='" + getId() + '\'' +
                ", nome='" + nome + '\'' +
                ", idade=" + idade +
                ", curso='" + curso + '\'' +
                ", turno='" + turno + '\'' +
                ", ativo=" + ativo +
                ", endereco=" + endereco +
                ", responsaveis=" + responsaveis +
                '}';
    }
}
