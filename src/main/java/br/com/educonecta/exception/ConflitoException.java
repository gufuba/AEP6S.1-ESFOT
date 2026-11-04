package br.com.educonecta.exception;

/**
 * Lancada quando a operacao viola uma regra de negocio em relacao ao estado atual
 * dos dados (ex.: turma lotada, registro com vinculos, e-mail ja cadastrado).
 */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
