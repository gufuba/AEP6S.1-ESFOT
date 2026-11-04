package br.com.educonecta.service;

import br.com.educonecta.exception.ConflitoException;
import br.com.educonecta.exception.RecursoNaoEncontradoException;
import br.com.educonecta.model.Voluntario;
import br.com.educonecta.repository.VoluntarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoluntarioServiceTest {

    @Mock
    private VoluntarioRepository repository;

    private VoluntarioService service;

    @BeforeEach
    void setUp() {
        service = new VoluntarioService(repository);
    }

    private static Voluntario voluntario(String email) {
        return new Voluntario("Carlos", email, List.of("Matematica"));
    }

    @Test
    void deveCadastrarVoluntarioComEmailDisponivel() {
        Voluntario novo = voluntario("carlos@email.com");
        when(repository.existePorEmail("carlos@email.com")).thenReturn(false);
        when(repository.inserir(novo)).thenReturn(novo);

        assertSame(novo, service.cadastrar(novo));
    }

    @Test
    void naoDeveCadastrarVoluntarioComEmailJaUtilizado() {
        Voluntario novo = voluntario("carlos@email.com");
        when(repository.existePorEmail("carlos@email.com")).thenReturn(true);

        ConflitoException erro = assertThrows(ConflitoException.class, () -> service.cadastrar(novo));
        assertTrue(erro.getMessage().contains("carlos@email.com"));
        verify(repository, never()).inserir(any());
    }

    @Test
    void deveBuscarVoluntarioPorId() {
        Voluntario existente = voluntario("carlos@email.com");
        when(repository.buscarPorId("id1")).thenReturn(existente);

        assertSame(existente, service.buscarPorId("id1"));
    }

    @Test
    void deveLancarExcecaoQuandoVoluntarioNaoExiste() {
        when(repository.buscarPorId("x")).thenReturn(null);

        RecursoNaoEncontradoException erro = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.buscarPorId("x"));
        assertTrue(erro.getMessage().contains("Voluntario"));
    }

    @Test
    void deveListarVoluntarios() {
        when(repository.listarTodos()).thenReturn(List.of(voluntario("a@email.com"), voluntario("b@email.com")));

        assertEquals(2, service.listarTodos().size());
    }

    @Test
    void deveAtualizarSemConsultarEmailQuandoEleNaoMudou() {
        Voluntario existente = new Voluntario("id1", "Carlos", "carlos@email.com", List.of("Matematica"), true);
        Voluntario novosDados = new Voluntario("Carlos Pereira", "carlos@email.com", List.of("Fisica"));
        when(repository.buscarPorId("id1")).thenReturn(existente);

        Voluntario resultado = service.atualizar("id1", novosDados);

        assertEquals("Carlos Pereira", resultado.getNome());
        assertEquals(List.of("Fisica"), resultado.getDisciplinas());
        verify(repository, never()).existePorEmail(any());
        verify(repository).atualizar(existente);
    }

    @Test
    void deveAtualizarTrocandoParaEmailLivre() {
        Voluntario existente = new Voluntario("id1", "Carlos", "carlos@email.com", List.of("Matematica"), true);
        when(repository.buscarPorId("id1")).thenReturn(existente);
        when(repository.existePorEmail("novo@email.com")).thenReturn(false);

        Voluntario resultado = service.atualizar("id1", voluntario("novo@email.com"));

        assertEquals("novo@email.com", resultado.getEmail());
    }

    @Test
    void naoDeveAtualizarTrocandoParaEmailDeOutroVoluntario() {
        Voluntario existente = new Voluntario("id1", "Carlos", "carlos@email.com", List.of("Matematica"), true);
        when(repository.buscarPorId("id1")).thenReturn(existente);
        when(repository.existePorEmail("ocupado@email.com")).thenReturn(true);

        assertThrows(ConflitoException.class, () -> service.atualizar("id1", voluntario("ocupado@email.com")));
        assertEquals("carlos@email.com", existente.getEmail());
        verify(repository, never()).atualizar(any());
    }

    @Test
    void naoDeveAtualizarVoluntarioInexistente() {
        when(repository.buscarPorId("x")).thenReturn(null);

        assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizar("x", voluntario("a@email.com")));
    }

    @Test
    void deveRemoverVoluntarioExistente() {
        when(repository.remover("id1")).thenReturn(true);

        assertDoesNotThrow(() -> service.remover("id1"));
    }

    @Test
    void naoDeveRemoverVoluntarioInexistente() {
        when(repository.remover("x")).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> service.remover("x"));
    }
}
