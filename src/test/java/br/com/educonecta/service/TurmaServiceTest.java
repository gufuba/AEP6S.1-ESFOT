package br.com.educonecta.service;

import br.com.educonecta.exception.ConflitoException;
import br.com.educonecta.exception.RecursoNaoEncontradoException;
import br.com.educonecta.model.Turma;
import br.com.educonecta.model.Voluntario;
import br.com.educonecta.repository.TurmaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurmaServiceTest {

    @Mock
    private TurmaRepository repository;

    @Mock
    private VoluntarioService voluntarioService;

    @Mock
    private AlunoService alunoService;

    private TurmaService service;

    @BeforeEach
    void setUp() {
        service = new TurmaService(repository, voluntarioService, alunoService);
    }

    private static Voluntario voluntarioDeMatematica(boolean ativo) {
        return new Voluntario("vol1", "Carlos", "carlos@email.com", List.of("Matematica"), ativo);
    }

    private static Turma novaTurma(String disciplina, int vagas) {
        return new Turma("Turma A", disciplina, "Tarde", vagas, "vol1");
    }

    private static Turma turmaExistente() {
        return new Turma("t1", "Turma A", "Matematica", "Tarde", 10, "vol1", true);
    }

    @Test
    void deveCadastrarTurmaComVoluntarioAtivoQueLecionaADisciplina() {
        Turma turma = novaTurma("matematica", 10);
        when(voluntarioService.buscarPorId("vol1")).thenReturn(voluntarioDeMatematica(true));
        when(repository.inserir(turma)).thenReturn(turma);

        assertSame(turma, service.cadastrar(turma));
    }

    @Test
    void naoDeveCadastrarTurmaComVoluntarioInexistente() {
        when(voluntarioService.buscarPorId("vol1")).thenThrow(new RecursoNaoEncontradoException("Voluntario", "vol1"));

        assertThrows(RecursoNaoEncontradoException.class, () -> service.cadastrar(novaTurma("Matematica", 10)));
        verify(repository, never()).inserir(any());
    }

    @Test
    void naoDeveCadastrarTurmaComVoluntarioInativo() {
        when(voluntarioService.buscarPorId("vol1")).thenReturn(voluntarioDeMatematica(false));

        ConflitoException erro = assertThrows(ConflitoException.class,
                () -> service.cadastrar(novaTurma("Matematica", 10)));
        assertTrue(erro.getMessage().contains("inativo"));
        verify(repository, never()).inserir(any());
    }

    @Test
    void naoDeveCadastrarTurmaDeDisciplinaQueOVoluntarioNaoLeciona() {
        when(voluntarioService.buscarPorId("vol1")).thenReturn(voluntarioDeMatematica(true));

        ConflitoException erro = assertThrows(ConflitoException.class,
                () -> service.cadastrar(novaTurma("Historia", 10)));
        assertTrue(erro.getMessage().contains("Historia"));
        verify(repository, never()).inserir(any());
    }

    @Test
    void deveBuscarTurmaPorId() {
        Turma turma = turmaExistente();
        when(repository.buscarPorId("t1")).thenReturn(turma);

        assertSame(turma, service.buscarPorId("t1"));
    }

    @Test
    void deveLancarExcecaoQuandoTurmaNaoExiste() {
        when(repository.buscarPorId("x")).thenReturn(null);

        RecursoNaoEncontradoException erro = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.buscarPorId("x"));
        assertTrue(erro.getMessage().contains("Turma"));
    }

    @Test
    void deveListarTurmas() {
        when(repository.listarTodos()).thenReturn(List.of(turmaExistente()));

        assertEquals(1, service.listarTodos().size());
    }

    @Test
    void deveAtualizarTurmaSemAlunos() {
        Turma existente = turmaExistente();
        when(repository.buscarPorId("t1")).thenReturn(existente);
        when(voluntarioService.buscarPorId("vol1")).thenReturn(voluntarioDeMatematica(true));
        when(alunoService.contarPorTurma("t1")).thenReturn(0L);

        Turma resultado = service.atualizar("t1", novaTurma("Matematica", 20));

        assertEquals(20, resultado.getVagas());
        verify(repository).atualizar(existente);
    }

    @Test
    void devePermitirReduzirVagasAteONumeroDeMatriculados() {
        Turma existente = turmaExistente();
        when(repository.buscarPorId("t1")).thenReturn(existente);
        when(voluntarioService.buscarPorId("vol1")).thenReturn(voluntarioDeMatematica(true));
        when(alunoService.contarPorTurma("t1")).thenReturn(5L);

        assertEquals(5, service.atualizar("t1", novaTurma("Matematica", 5)).getVagas());
    }

    @Test
    void naoDevePermitirVagasMenoresQueOsAlunosMatriculados() {
        Turma existente = turmaExistente();
        when(repository.buscarPorId("t1")).thenReturn(existente);
        when(voluntarioService.buscarPorId("vol1")).thenReturn(voluntarioDeMatematica(true));
        when(alunoService.contarPorTurma("t1")).thenReturn(8L);

        ConflitoException erro = assertThrows(ConflitoException.class,
                () -> service.atualizar("t1", novaTurma("Matematica", 5)));
        assertTrue(erro.getMessage().contains("8"));
        assertEquals(10, existente.getVagas());
        verify(repository, never()).atualizar(any());
    }

    @Test
    void naoDevePermitirTrocarDisciplinaDeTurmaComAlunos() {
        Turma existente = turmaExistente();
        Voluntario lecionaAmbas = new Voluntario("vol1", "Carlos", "carlos@email.com",
                List.of("Matematica", "Fisica"), true);
        when(repository.buscarPorId("t1")).thenReturn(existente);
        when(voluntarioService.buscarPorId("vol1")).thenReturn(lecionaAmbas);
        when(alunoService.contarPorTurma("t1")).thenReturn(2L);

        assertThrows(ConflitoException.class, () -> service.atualizar("t1", novaTurma("Fisica", 10)));
        assertEquals("Matematica", existente.getDisciplina());
    }

    @Test
    void devePermitirTrocarDisciplinaDeTurmaSemAlunos() {
        Turma existente = turmaExistente();
        Voluntario lecionaAmbas = new Voluntario("vol1", "Carlos", "carlos@email.com",
                List.of("Matematica", "Fisica"), true);
        when(repository.buscarPorId("t1")).thenReturn(existente);
        when(voluntarioService.buscarPorId("vol1")).thenReturn(lecionaAmbas);
        when(alunoService.contarPorTurma("t1")).thenReturn(0L);

        assertEquals("Fisica", service.atualizar("t1", novaTurma("Fisica", 10)).getDisciplina());
    }

    @Test
    void naoDeveAtualizarTurmaInexistente() {
        when(repository.buscarPorId("x")).thenReturn(null);

        assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizar("x", novaTurma("Matematica", 10)));
        verifyNoInteractions(voluntarioService, alunoService);
    }

    @Test
    void deveRemoverTurmaSemAlunos() {
        when(repository.buscarPorId("t1")).thenReturn(turmaExistente());
        when(alunoService.contarPorTurma("t1")).thenReturn(0L);

        assertDoesNotThrow(() -> service.remover("t1"));
        verify(repository).remover("t1");
    }

    @Test
    void naoDeveRemoverTurmaComAlunosMatriculados() {
        when(repository.buscarPorId("t1")).thenReturn(turmaExistente());
        when(alunoService.contarPorTurma("t1")).thenReturn(3L);

        assertThrows(ConflitoException.class, () -> service.remover("t1"));
        verify(repository, never()).remover(any());
    }

    @Test
    void naoDeveRemoverTurmaInexistente() {
        when(repository.buscarPorId("x")).thenReturn(null);

        assertThrows(RecursoNaoEncontradoException.class, () -> service.remover("x"));
    }
}
