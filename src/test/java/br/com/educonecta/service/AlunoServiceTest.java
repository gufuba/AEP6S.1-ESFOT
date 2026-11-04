package br.com.educonecta.service;

import br.com.educonecta.exception.RecursoNaoEncontradoException;
import br.com.educonecta.model.Aluno;
import br.com.educonecta.model.Responsavel;
import br.com.educonecta.repository.AlunoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepository repository;

    private AlunoService service;

    @BeforeEach
    void setUp() {
        service = new AlunoService(repository);
    }

    private static Aluno alunoComResponsavel(String nome, int idade, String curso, String turno) {
        Aluno aluno = new Aluno(nome, idade, curso, turno);
        aluno.setResponsaveis(List.of(new Responsavel("Ana Souza", "Mae", "44999991234")));
        return aluno;
    }

    @Test
    void deveCadastrarAlunoComIdadeValida() {
        Aluno aluno = alunoComResponsavel("Maria", 10, "Matematica", "Tarde");
        when(repository.inserir(aluno)).thenReturn(aluno);

        assertSame(aluno, service.cadastrar(aluno));
        verify(repository).inserir(aluno);
    }

    @Test
    void naoDeveCadastrarAlunoAbaixoDaIdadeMinima() {
        Aluno aluno = alunoComResponsavel("Bebe", 3, "Matematica", "Tarde");

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(aluno));
        verify(repository, never()).inserir(any());
    }

    @Test
    void naoDeveCadastrarMenorDeIdadeSemResponsavel() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class, () -> service.cadastrar(aluno));
        assertTrue(erro.getMessage().contains("responsavel"));
        verify(repository, never()).inserir(any());
    }

    @Test
    void deveCadastrarMaiorDeIdadeSemResponsavel() {
        Aluno aluno = new Aluno("Carlos", 20, "Matematica", "Noite");
        when(repository.inserir(aluno)).thenReturn(aluno);

        assertSame(aluno, service.cadastrar(aluno));
    }

    @Test
    void deveBuscarAlunoPorIdQuandoExiste() {
        Aluno aluno = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        when(repository.buscarPorId("id1")).thenReturn(aluno);

        assertSame(aluno, service.buscarPorId("id1"));
    }

    @Test
    void deveLancarExcecaoAoBuscarIdInexistente() {
        when(repository.buscarPorId("naoExiste")).thenReturn(null);

        RecursoNaoEncontradoException erro = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.buscarPorId("naoExiste"));
        assertTrue(erro.getMessage().contains("Aluno"));
    }

    @Test
    void deveListarTodosOsAlunos() {
        when(repository.listarTodos()).thenReturn(List.of(
                new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true),
                new Aluno("id2", "Joao", 12, "Portugues", "Manha", true)));

        assertEquals(2, service.listarTodos().size());
    }

    @Test
    void deveAtualizarAlunoExistenteBuscandoUmaUnicaVez() {
        Aluno existente = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        Aluno novosDados = alunoComResponsavel("Maria Silva", 11, "Portugues", "Manha");
        when(repository.buscarPorId("id1")).thenReturn(existente);

        Aluno resultado = service.atualizar("id1", novosDados);

        assertEquals("id1", resultado.getId());
        assertEquals("Maria Silva", resultado.getNome());
        assertEquals("Portugues", resultado.getCurso());
        assertEquals(1, resultado.getResponsaveis().size());
        verify(repository, times(1)).buscarPorId("id1");
        verify(repository).atualizar(existente);
    }

    @Test
    void naoDeveAtualizarAlunoInexistente() {
        when(repository.buscarPorId("idInexistente")).thenReturn(null);
        Aluno novosDados = alunoComResponsavel("Maria", 10, "Matematica", "Tarde");

        assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizar("idInexistente", novosDados));
        verify(repository, never()).atualizar(any());
    }

    @Test
    void naoDevePermitirReduzirIdadeAbaixoDoMinimoNaAtualizacao() {
        Aluno existente = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        Aluno novosDados = alunoComResponsavel("Maria", 3, "Matematica", "Tarde");
        when(repository.buscarPorId("id1")).thenReturn(existente);

        assertThrows(IllegalArgumentException.class, () -> service.atualizar("id1", novosDados));
        assertEquals(10, existente.getIdade());
        verify(repository, never()).atualizar(any());
    }

    @Test
    void naoDevePermitirRemoverTodosOsResponsaveisDeUmMenorNaAtualizacao() {
        Aluno existente = alunoComResponsavel("Maria", 10, "Matematica", "Tarde");
        existente.setId("id1");
        Aluno novosDados = new Aluno("Maria", 10, "Matematica", "Tarde");
        when(repository.buscarPorId("id1")).thenReturn(existente);

        assertThrows(IllegalArgumentException.class, () -> service.atualizar("id1", novosDados));
        assertEquals(1, existente.getResponsaveis().size());
        verify(repository, never()).atualizar(any());
    }

    @Test
    void deveRemoverAlunoExistente() {
        when(repository.remover("id1")).thenReturn(true);

        assertDoesNotThrow(() -> service.remover("id1"));
        verify(repository).remover("id1");
    }

    @Test
    void naoDeveRemoverAlunoInexistente() {
        when(repository.remover("idInexistente")).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> service.remover("idInexistente"));
    }
}
