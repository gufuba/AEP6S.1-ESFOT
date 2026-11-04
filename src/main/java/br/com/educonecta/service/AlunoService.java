package br.com.educonecta.service;

import br.com.educonecta.exception.RecursoNaoEncontradoException;
import br.com.educonecta.model.Aluno;
import br.com.educonecta.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Regras de negocio do cadastro de alunos do programa de reforco escolar.
 */
@Service
public class AlunoService {

    public static final int IDADE_MINIMA_PROGRAMA = 6;

    private final AlunoRepository repository;

    public AlunoService(AlunoRepository repository) {
        this.repository = repository;
    }

    public Aluno cadastrar(Aluno aluno) {
        validarRegras(aluno);
        return repository.inserir(aluno);
    }

    public Aluno buscarPorId(String id) {
        Aluno aluno = repository.buscarPorId(id);
        if (aluno == null) {
            throw new RecursoNaoEncontradoException("Aluno", id);
        }
        return aluno;
    }

    public List<Aluno> listarTodos() {
        return repository.listarTodos();
    }

    public Aluno atualizar(String id, Aluno novosDados) {
        Aluno existente = buscarPorId(id);
        validarRegras(novosDados);
        existente.atualizarDados(novosDados);
        repository.atualizar(existente);
        return existente;
    }

    public void remover(String id) {
        if (!repository.remover(id)) {
            throw new RecursoNaoEncontradoException("Aluno", id);
        }
    }

    private void validarRegras(Aluno aluno) {
        if (aluno.getIdade() < IDADE_MINIMA_PROGRAMA) {
            throw new IllegalArgumentException(
                    "Aluno deve ter no minimo " + IDADE_MINIMA_PROGRAMA + " anos para ingressar no programa.");
        }
    }
}
