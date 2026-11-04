package br.com.educonecta.model;

/**
 * Objeto de valor armazenado em uma lista de subdocumentos dentro do aluno.
 * O telefone e guardado apenas com digitos (DDD + numero).
 */
public record Responsavel(String nome, String parentesco, String telefone) {

    public Responsavel {
        nome = Validacoes.textoObrigatorio(nome, "Nome do responsavel nao pode ser vazio.");
        parentesco = Validacoes.textoObrigatorio(parentesco, "Parentesco do responsavel nao pode ser vazio.");
        telefone = Validacoes.textoObrigatorio(telefone, "Telefone do responsavel nao pode ser vazio.")
                .replaceAll("\\D", "");
        if (telefone.length() < 10 || telefone.length() > 11) {
            throw new IllegalArgumentException("Telefone do responsavel deve ter 10 ou 11 digitos (DDD + numero).");
        }
    }
}
