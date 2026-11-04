package br.com.educonecta.service;

import br.com.educonecta.exception.ConflitoException;
import br.com.educonecta.exception.RecursoNaoEncontradoException;
import br.com.educonecta.model.Aluno;
import br.com.educonecta.model.Turma;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock
    private AlunoService alunoService;

    @Mock
    private TurmaService turmaService;

    private MatriculaService service;

    @BeforeEach
    void setUp() {
        service = new MatriculaService(alunoService, turmaService);
    }

    private static Turma turma(String disciplina, int vagas, boolean ativa) {
        return new Turma("t1", "Turma A", disciplina, "Tarde", vagas, "vol1", ativa);
    }

    private static Aluno aluno(String curso, boolean ativo) {
        return new Aluno("a1", "Maria", 10, curso, "Tarde", ativo);
    }

    private void prepararBuscas(Turma turma, Aluno aluno) {
        when(turmaService.buscarPorId("t1")).thenReturn(turma);
        when(alunoService.buscarPorId("a1")).thenReturn(aluno);
    }

    @Test
    void deveMatricularAlunoEmTurmaComVagaEMesmaDisciplina() {
        Aluno aluno = aluno("matematica", true);
        prepararBuscas(turma("Matematica", 2, true), aluno);
        when(alunoService.contarPorTurma("t1")).thenReturn(1L);
        when(alunoService.alterarTurma(aluno, "t1")).thenReturn(aluno);

        Aluno resultado = service.matricular("t1", "a1");

        assertSame(aluno, resultado);
        verify(alunoService).alterarTurma(aluno, "t1");
    }

    @Test
    void deveTransferirAlunoQueJaEstaEmOutraTurma() {
        Aluno aluno = aluno("Matematica", true);
        aluno.setTurmaId("outraTurma");
        prepararBuscas(turma("Matematica", 5, true), aluno);
        when(alunoService.contarPorTurma("t1")).thenReturn(0L);

        service.matricular("t1", "a1");

        verify(alunoService).alterarTurma(aluno, "t1");
    }

    @Test
    void naoDeveMatricularAlunoJaMatriculadoNaMesmaTurma() {
        Aluno aluno = aluno("Matematica", true);
        aluno.setTurmaId("t1");
        prepararBuscas(turma("Matematica", 5, true), aluno);

        ConflitoException erro = assertThrows(ConflitoException.class, () -> service.matricular("t1", "a1"));
        assertTrue(erro.getMessage().contains("ja esta matriculado"));
    }

    @Test
    void naoDeveMatricularEmTurmaInativa() {
        prepararBuscas(turma("Matematica", 5, false), aluno("Matematica", true));

        ConflitoException erro = assertThrows(ConflitoException.class, () -> service.matricular("t1", "a1"));
        assertTrue(erro.getMessage().contains("Turma inativa"));
    }

    @Test
    void naoDeveMatricularAlunoInativo() {
        prepararBuscas(turma("Matematica", 5, true), aluno("Matematica", false));

        ConflitoException erro = assertThrows(ConflitoException.class, () -> service.matricular("t1", "a1"));
        assertTrue(erro.getMessage().contains("Aluno inativo"));
    }

    @Test
    void naoDeveMatricularAlunoDeOutraDisciplina() {
        prepararBuscas(turma("Matematica", 5, true), aluno("Portugues", true));

        ConflitoException erro = assertThrows(ConflitoException.class, () -> service.matricular("t1", "a1"));
        assertTrue(erro.getMessage().contains("Matematica"));
        assertTrue(erro.getMessage().contains("Portugues"));
    }

    @Test
    void naoDeveMatricularEmTurmaLotada() {
        prepararBuscas(turma("Matematica", 2, true), aluno("Matematica", true));
        when(alunoService.contarPorTurma("t1")).thenReturn(2L);

        ConflitoException erro = assertThrows(ConflitoException.class, () -> service.matricular("t1", "a1"));
        assertTrue(erro.getMessage().contains("sem vagas"));
        verify(alunoService, never()).alterarTurma(any(), any());
    }

    @Test
    void deveRepassarErroQuandoTurmaOuAlunoNaoExistem() {
        when(turmaService.buscarPorId("t1")).thenThrow(new RecursoNaoEncontradoException("Turma", "t1"));

        assertThrows(RecursoNaoEncontradoException.class, () -> service.matricular("t1", "a1"));
        verifyNoInteractions(alunoService);
    }

    @Test
    void deveDesmatricularAlunoDaTurma() {
        Aluno aluno = aluno("Matematica", true);
        aluno.setTurmaId("t1");
        prepararBuscas(turma("Matematica", 5, true), aluno);

        service.desmatricular("t1", "a1");

        verify(alunoService).alterarTurma(aluno, null);
    }

    @Test
    void naoDeveDesmatricularAlunoQueNaoEstaNaTurma() {
        Aluno aluno = aluno("Matematica", true);
        aluno.setTurmaId("outraTurma");
        prepararBuscas(turma("Matematica", 5, true), aluno);

        assertThrows(ConflitoException.class, () -> service.desmatricular("t1", "a1"));
        verify(alunoService, never()).alterarTurma(any(), any());
    }

    @Test
    void naoDeveDesmatricularAlunoSemTurma() {
        prepararBuscas(turma("Matematica", 5, true), aluno("Matematica", true));

        assertThrows(ConflitoException.class, () -> service.desmatricular("t1", "a1"));
    }

    @Test
    void deveListarAlunosDaTurma() {
        Aluno aluno = aluno("Matematica", true);
        when(turmaService.buscarPorId("t1")).thenReturn(turma("Matematica", 5, true));
        when(alunoService.listarPorTurma("t1")).thenReturn(List.of(aluno));

        assertEquals(List.of(aluno), service.listarAlunosDaTurma("t1"));
    }

    @Test
    void naoDeveListarAlunosDeTurmaInexistente() {
        when(turmaService.buscarPorId("x")).thenThrow(new RecursoNaoEncontradoException("Turma", "x"));

        assertThrows(RecursoNaoEncontradoException.class, () -> service.listarAlunosDaTurma("x"));
        verifyNoInteractions(alunoService);
    }
}
