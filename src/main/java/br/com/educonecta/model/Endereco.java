package br.com.educonecta.model;

import java.util.Locale;

/**
 * Objeto de valor armazenado como subdocumento dentro do aluno.
 */
public record Endereco(String rua, String bairro, String cidade, String estado) {

    public Endereco {
        rua = Validacoes.textoObrigatorio(rua, "Rua do endereco nao pode ser vazia.");
        bairro = Validacoes.textoObrigatorio(bairro, "Bairro do endereco nao pode ser vazio.");
        cidade = Validacoes.textoObrigatorio(cidade, "Cidade do endereco nao pode ser vazia.");
        estado = Validacoes.textoObrigatorio(estado, "Estado (UF) do endereco nao pode ser vazio.")
                .toUpperCase(Locale.ROOT);
        if (!estado.matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("Estado deve ser a sigla (UF) com 2 letras.");
        }
    }
}
