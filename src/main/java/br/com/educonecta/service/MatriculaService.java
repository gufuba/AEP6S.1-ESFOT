package br.com.educonecta.service;

import br.com.educonecta.exception.ConflitoException;
import br.com.educonecta.model.Aluno;
import br.com.educonecta.model.Turma;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Gerencia o relacionamento entre alunos e turmas. Matricular um aluno que ja esta
 * em outra turma transfere o aluno para a nova turma.
 */
@Service
public class MatriculaService {

    private final AlunoService alunoService;
    private final TurmaService turmaService;

    public MatriculaService(AlunoService alunoService, TurmaService turmaService) {
        this.alunoService = alunoService;
        this.turmaService = turmaService;
    }

    public Aluno matricular(String turmaId, String alunoId) {
        Turma turma = turmaService.buscarPorId(turmaId);
        Aluno aluno = alunoService.buscarPorId(alunoId);
        validarMatricula(turma, aluno);
        return alunoService.alterarTurma(aluno, turma.getId());
    }

    public void desmatricular(String turmaId, String alunoId) {
        Turma turma = turmaService.buscarPorId(turmaId);
        Aluno aluno = alunoService.buscarPorId(alunoId);
        if (!turma.getId().equals(aluno.getTurmaId())) {
            throw new ConflitoException("Aluno nao esta matriculado nesta turma.");
        }
        alunoService.alterarTurma(aluno, null);
    }

    public List<Aluno> listarAlunosDaTurma(String turmaId) {
        Turma turma = turmaService.buscarPorId(turmaId);
        return alunoService.listarPorTurma(turma.getId());
    }

    private void validarMatricula(Turma turma, Aluno aluno) {
        if (turma.getId().equals(aluno.getTurmaId())) {
            throw new ConflitoException("Aluno ja esta matriculado nesta turma.");
        }
        if (!turma.isAtiva()) {
            throw new ConflitoException("Turma inativa nao aceita matriculas.");
        }
        if (!aluno.isAtivo()) {
            throw new ConflitoException("Aluno inativo nao pode ser matriculado.");
        }
        if (!turma.getDisciplina().equalsIgnoreCase(aluno.getCurso())) {
            throw new ConflitoException("A disciplina da turma (" + turma.getDisciplina()
                    + ") e diferente da disciplina do aluno (" + aluno.getCurso() + ").");
        }
        if (alunoService.contarPorTurma(turma.getId()) >= turma.getVagas()) {
            throw new ConflitoException("Turma sem vagas disponiveis.");
        }
    }
}
