package br.com.educonecta.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VoluntarioTest {

    private static Voluntario voluntarioValido() {
        return new Voluntario("Carlos Pereira", "carlos@email.com", List.of("Matematica", "Fisica"));
    }

    @Test
    void deveCriarVoluntarioValidoAtivoPorPadrao() {
        Voluntario voluntario = voluntarioValido();

        assertNull(voluntario.getId());
        assertEquals("Carlos Pereira", voluntario.getNome());
        assertEquals("carlos@email.com", voluntario.getEmail());
        assertEquals(List.of("Matematica", "Fisica"), voluntario.getDisciplinas());
        assertTrue(voluntario.isAtivo());
    }

    @Test
    void deveNormalizarEmailParaMinusculoESemEspacos() {
        Voluntario voluntario = new Voluntario("Carlos", "  Carlos.Pereira@Email.COM ", List.of("Matematica"));

        assertEquals("carlos.pereira@email.com", voluntario.getEmail());
    }

    @Test
    void naoDevePermitirEmailInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Voluntario("Carlos", "sem-arroba", List.of("Matematica")));
        assertThrows(IllegalArgumentException.class, () -> new Voluntario("Carlos", "a@b", List.of("Matematica")));
        assertThrows(IllegalArgumentException.class, () -> new Voluntario("Carlos", "a b@c.com", List.of("Matematica")));
        assertThrows(IllegalArgumentException.class, () -> new Voluntario("Carlos", " ", List.of("Matematica")));
    }

    @Test
    void naoDevePermitirNomeVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Voluntario(" ", "carlos@email.com", List.of("Matematica")));
    }

    @Test
    void naoDevePermitirListaDeDisciplinasNulaOuVazia() {
        assertThrows(IllegalArgumentException.class, () -> new Voluntario("Carlos", "carlos@email.com", null));
        assertThrows(IllegalArgumentException.class, () -> new Voluntario("Carlos", "carlos@email.com", List.of()));
    }

    @Test
    void naoDevePermitirDisciplinaVaziaOuNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Voluntario("Carlos", "carlos@email.com", List.of("Matematica", " ")));

        List<String> comNulo = new ArrayList<>();
        comNulo.add(null);
        assertThrows(IllegalArgumentException.class, () -> new Voluntario("Carlos", "carlos@email.com", comNulo));
    }

    @Test
    void deveRemoverEspacosEDisciplinasRepetidasIgnorandoMaiusculas() {
        Voluntario voluntario = new Voluntario("Carlos", "carlos@email.com",
                List.of(" Matematica ", "matematica", "Fisica", "FISICA"));

        assertEquals(List.of("Matematica", "Fisica"), voluntario.getDisciplinas());
    }

    @Test
    void deveProtegerDisciplinasContraAlteracaoExterna() {
        List<String> original = new ArrayList<>(List.of("Matematica"));
        Voluntario voluntario = new Voluntario("Carlos", "carlos@email.com", original);

        original.add("Fisica");

        assertEquals(1, voluntario.getDisciplinas().size());
        assertThrows(UnsupportedOperationException.class, () -> voluntario.getDisciplinas().add("Fisica"));
    }

    @Test
    void deveInformarSeLecionaDisciplinaIgnorandoMaiusculas() {
        Voluntario voluntario = voluntarioValido();

        assertTrue(voluntario.leciona("matematica"));
        assertTrue(voluntario.leciona("FISICA"));
        assertFalse(voluntario.leciona("Historia"));
        assertFalse(voluntario.leciona(null));
    }

    @Test
    void deveAtualizarDadosPreservandoOId() {
        Voluntario existente = new Voluntario("id1", "Carlos", "carlos@email.com", List.of("Matematica"), true);
        Voluntario novosDados = new Voluntario("Carlos Pereira", "novo@email.com", List.of("Historia"));
        novosDados.setAtivo(false);

        existente.atualizarDados(novosDados);

        assertEquals("id1", existente.getId());
        assertEquals("Carlos Pereira", existente.getNome());
        assertEquals("novo@email.com", existente.getEmail());
        assertEquals(List.of("Historia"), existente.getDisciplinas());
        assertFalse(existente.isAtivo());
    }
}
