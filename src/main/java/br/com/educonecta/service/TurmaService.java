package br.com.educonecta.service;

import br.com.educonecta.exception.ConflitoException;
import br.com.educonecta.exception.RecursoNaoEncontradoException;
import br.com.educonecta.model.Turma;
import br.com.educonecta.model.Voluntario;
import br.com.educonecta.repository.TurmaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Regras de negocio das turmas: o voluntario responsavel deve existir, estar ativo
 * e lecionar a disciplina da turma, e a turma nao pode ficar com menos vagas que alunos.
 */
@Service
public class TurmaService {

    private final TurmaRepository repository;
    private final VoluntarioService voluntarioService;
    private final AlunoService alunoService;

    public TurmaService(TurmaRepository repository, VoluntarioService voluntarioService,
                        AlunoService alunoService) {
        this.repository = repository;
        this.voluntarioService = voluntarioService;
        this.alunoService = alunoService;
    }

    public Turma cadastrar(Turma turma) {
        validarVoluntario(turma);
        return repository.inserir(turma);
    }

    public Turma buscarPorId(String id) {
        Turma turma = repository.buscarPorId(id);
        if (turma == null) {
            throw new RecursoNaoEncontradoException("Turma", id);
        }
        return turma;
    }

    public List<Turma> listarTodos() {
        return repository.listarTodos();
    }

    public Turma atualizar(String id, Turma novosDados) {
        Turma existente = buscarPorId(id);
        validarVoluntario(novosDados);
        validarContraAlunosMatriculados(existente, novosDados);
        existente.atualizarDados(novosDados);
        repository.atualizar(existente);
        return existente;
    }

    public void remover(String id) {
        buscarPorId(id);
        if (alunoService.contarPorTurma(id) > 0) {
            throw new ConflitoException("Turma possui alunos matriculados e nao pode ser removida.");
        }
        repository.remover(id);
    }

    private void validarVoluntario(Turma turma) {
        Voluntario voluntario = voluntarioService.buscarPorId(turma.getVoluntarioId());
        if (!voluntario.isAtivo()) {
            throw new ConflitoException("Voluntario inativo nao pode ser responsavel por uma turma.");
        }
        if (!voluntario.leciona(turma.getDisciplina())) {
            throw new ConflitoException("Voluntario " + voluntario.getNome()
                    + " nao leciona a disciplina " + turma.getDisciplina() + ".");
        }
    }

    private void validarContraAlunosMatriculados(Turma existente, Turma novosDados) {
        long matriculados = alunoService.contarPorTurma(existente.getId());
        if (novosDados.getVagas() < matriculados) {
            throw new ConflitoException("Turma possui " + matriculados
                    + " alunos matriculados; o numero de vagas nao pode ser menor que isso.");
        }
        if (matriculados > 0 && !existente.getDisciplina().equalsIgnoreCase(novosDados.getDisciplina())) {
            throw new ConflitoException("Nao e possivel alterar a disciplina de uma turma com alunos matriculados.");
        }
    }
}
