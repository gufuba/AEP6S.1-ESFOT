package br.com.educonecta.model;

/**
 * Representa um aluno inscrito no programa de reforco escolar gratuito.
 */
public class Aluno extends Entidade {

    public static final int IDADE_MAXIMA = 120;

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

    public void atualizarDados(Aluno novosDados) {
        setNome(novosDados.nome);
        setIdade(novosDados.idade);
        setCurso(novosDados.curso);
        setTurno(novosDados.turno);
        setAtivo(novosDados.ativo);
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
                '}';
    }
}
