package br.com.educonecta.service;

import br.com.educonecta.model.Aluno;
import br.com.educonecta.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Camada de regras de negocio do programa de reforco escolar.
 */
@Service
public class AlunoService {

    public static final int IDADE_MINIMA_PROGRAMA = 6;

    private final AlunoRepository repository;

    public AlunoService(AlunoRepository repository) {
        this.repository = repository;
    }

    public Aluno cadastrar(Aluno aluno) {
        if (aluno.getIdade() < IDADE_MINIMA_PROGRAMA) {
            throw new IllegalArgumentException(
                    "Aluno deve ter no minimo " + IDADE_MINIMA_PROGRAMA + " anos para ingressar no programa.");
        }
        return repository.inserir(aluno);
    }

    public Aluno buscarPorId(String id) {
        Aluno aluno = repository.buscarPorId(id);
        if (aluno == null) {
            throw new AlunoNaoEncontradoException(id);
        }
        return aluno;
    }

    public List<Aluno> listarTodos() {
        return repository.listarTodos();
    }

    public Aluno atualizar(Aluno aluno) {
        buscarPorId(aluno.getId());
        repository.atualizar(aluno);
        return aluno;
    }

    public void remover(String id) {
        buscarPorId(id);
        repository.remover(id);
    }

    public static class AlunoNaoEncontradoException extends RuntimeException {
        public AlunoNaoEncontradoException(String id) {
            super("Aluno com id " + id + " nao encontrado.");
        }
    }
}
