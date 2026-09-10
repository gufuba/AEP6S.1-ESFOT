package br.com.educonecta.exception;

import br.com.educonecta.service.AlunoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converte as excecoes de dominio/negocio em respostas HTTP apropriadas,
 * mantendo os controllers livres de tratamento de erro repetitivo.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AlunoService.AlunoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleAlunoNaoEncontrado(AlunoService.AlunoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErroResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }
}
