package br.com.educonecta.model;

/**
 * Turma de reforco de uma disciplina, conduzida por um voluntario (referencia por id).
 */
public class Turma extends Entidade {

    public static final int VAGAS_MAXIMAS = 100;

    private String nome;
    private String disciplina;
    private String turno;
    private int vagas;
    private String voluntarioId;
    private boolean ativa;

    public Turma(String nome, String disciplina, String turno, int vagas, String voluntarioId) {
        this(null, nome, disciplina, turno, vagas, voluntarioId, true);
    }

    public Turma(String id, String nome, String disciplina, String turno, int vagas, String voluntarioId,
                 boolean ativa) {
        setNome(nome);
        setDisciplina(disciplina);
        setTurno(turno);
        setVagas(vagas);
        setVoluntarioId(voluntarioId);
        setId(id);
        this.ativa = ativa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Validacoes.textoObrigatorio(nome, "Nome da turma nao pode ser vazio.");
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = Validacoes.textoObrigatorio(disciplina, "Disciplina da turma nao pode ser vazia.");
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = Validacoes.textoObrigatorio(turno, "Turno da turma nao pode ser vazio.");
    }

    public int getVagas() {
        return vagas;
    }

    public void setVagas(int vagas) {
        if (vagas < 1 || vagas > VAGAS_MAXIMAS) {
            throw new IllegalArgumentException("Numero de vagas deve estar entre 1 e " + VAGAS_MAXIMAS + ".");
        }
        this.vagas = vagas;
    }

    public String getVoluntarioId() {
        return voluntarioId;
    }

    public void setVoluntarioId(String voluntarioId) {
        this.voluntarioId = Validacoes.textoObrigatorio(voluntarioId,
                "Voluntario responsavel pela turma e obrigatorio.");
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public void atualizarDados(Turma novosDados) {
        setNome(novosDados.nome);
        setDisciplina(novosDados.disciplina);
        setTurno(novosDados.turno);
        setVagas(novosDados.vagas);
        setVoluntarioId(novosDados.voluntarioId);
        setAtiva(novosDados.ativa);
    }
}
