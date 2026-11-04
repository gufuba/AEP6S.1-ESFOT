package br.com.educonecta.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Professor voluntario do programa. Guarda a lista de disciplinas que leciona.
 */
public class Voluntario extends Entidade {

    private static final Pattern PADRAO_EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private String nome;
    private String email;
    private List<String> disciplinas;
    private boolean ativo;

    public Voluntario(String nome, String email, List<String> disciplinas) {
        this(null, nome, email, disciplinas, true);
    }

    public Voluntario(String id, String nome, String email, List<String> disciplinas, boolean ativo) {
        setNome(nome);
        setEmail(email);
        setDisciplinas(disciplinas);
        setId(id);
        this.ativo = ativo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Validacoes.textoObrigatorio(nome, "Nome do voluntario nao pode ser vazio.");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String normalizado = Validacoes.textoObrigatorio(email, "E-mail do voluntario nao pode ser vazio.")
                .toLowerCase(Locale.ROOT);
        if (!PADRAO_EMAIL.matcher(normalizado).matches()) {
            throw new IllegalArgumentException("E-mail do voluntario invalido.");
        }
        this.email = normalizado;
    }

    public List<String> getDisciplinas() {
        return disciplinas;
    }

    public void setDisciplinas(List<String> disciplinas) {
        if (disciplinas == null || disciplinas.isEmpty()) {
            throw new IllegalArgumentException("Voluntario deve lecionar ao menos uma disciplina.");
        }
        List<String> normalizadas = new ArrayList<>();
        for (String item : disciplinas) {
            String disciplina = Validacoes.textoObrigatorio(item, "Disciplina do voluntario nao pode ser vazia.");
            if (normalizadas.stream().noneMatch(disciplina::equalsIgnoreCase)) {
                normalizadas.add(disciplina);
            }
        }
        this.disciplinas = List.copyOf(normalizadas);
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean leciona(String disciplina) {
        return disciplinas.stream().anyMatch(d -> d.equalsIgnoreCase(disciplina));
    }

    public void atualizarDados(Voluntario novosDados) {
        setNome(novosDados.nome);
        setEmail(novosDados.email);
        setDisciplinas(novosDados.disciplinas);
        setAtivo(novosDados.ativo);
    }
}
