package br.com.educonecta.model;

/**
 * Regras de validacao reutilizadas por todas as entidades do dominio.
 */
final class Validacoes {

    private Validacoes() {
    }

    static String textoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor.trim();
    }
}
