package br.com.educonecta.service;

import br.com.educonecta.model.Aluno;
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

    @Test
    void deveCadastrarAlunoComIdadeValida() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");
        when(repository.inserir(aluno)).thenReturn(aluno);

        Aluno resultado = service.cadastrar(aluno);

        assertEquals(aluno, resultado);
        verify(repository).inserir(aluno);
    }

    @Test
    void naoDeveCadastrarAlunoAbaixoDaIdadeMinima() {
        Aluno aluno = new Aluno("Bebe", 3, "Matematica", "Tarde");

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(aluno));
        verify(repository, never()).inserir(any());
    }

    @Test
    void deveBuscarAlunoPorIdQuandoExiste() {
        Aluno aluno = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        when(repository.buscarPorId("id1")).thenReturn(aluno);

        Aluno resultado = service.buscarPorId("id1");

        assertEquals(aluno, resultado);
    }

    @Test
    void deveLancarExcecaoAoBuscarIdInexistente() {
        when(repository.buscarPorId("naoExiste")).thenReturn(null);

        assertThrows(AlunoService.AlunoNaoEncontradoException.class,
                () -> service.buscarPorId("naoExiste"));
    }

    @Test
    void deveListarTodosOsAlunos() {
        List<Aluno> alunos = List.of(
                new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true),
                new Aluno("id2", "Joao", 12, "Portugues", "Manha", true)
        );
        when(repository.listarTodos()).thenReturn(alunos);

        List<Aluno> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
    }

    @Test
    void deveAtualizarAlunoExistente() {
        Aluno aluno = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        when(repository.buscarPorId("id1")).thenReturn(aluno);

        Aluno resultado = service.atualizar(aluno);

        assertEquals(aluno, resultado);
        verify(repository).atualizar(aluno);
    }

    @Test
    void naoDeveAtualizarAlunoInexistente() {
        Aluno aluno = new Aluno("idInexistente", "Maria", 10, "Matematica", "Tarde", true);
        when(repository.buscarPorId("idInexistente")).thenReturn(null);

        assertThrows(AlunoService.AlunoNaoEncontradoException.class,
                () -> service.atualizar(aluno));
        verify(repository, never()).atualizar(any());
    }

    @Test
    void deveRemoverAlunoExistente() {
        Aluno aluno = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        when(repository.buscarPorId("id1")).thenReturn(aluno);

        service.remover("id1");

        verify(repository).remover("id1");
    }

    @Test
    void naoDeveRemoverAlunoInexistente() {
        when(repository.buscarPorId("idInexistente")).thenReturn(null);

        assertThrows(AlunoService.AlunoNaoEncontradoException.class,
                () -> service.remover("idInexistente"));
        verify(repository, never()).remover(any());
    }
}
