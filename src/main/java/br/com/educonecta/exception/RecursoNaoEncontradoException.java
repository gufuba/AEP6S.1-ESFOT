package br.com.educonecta.exception;

/**
 * Lancada quando um registro referenciado por id nao existe na colecao correspondente.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String recurso, String id) {
        super(recurso + " com id " + id + " nao encontrado.");
    }
}
