package br.com.educonecta.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TurmaTest {

    private static Turma turmaValida() {
        return new Turma("Matematica - Tarde A", "Matematica", "Tarde", 15, "vol1");
    }

    @Test
    void deveCriarTurmaValidaAtivaPorPadrao() {
        Turma turma = turmaValida();

        assertNull(turma.getId());
        assertEquals("Matematica - Tarde A", turma.getNome());
        assertEquals("Matematica", turma.getDisciplina());
        assertEquals("Tarde", turma.getTurno());
        assertEquals(15, turma.getVagas());
        assertEquals("vol1", turma.getVoluntarioId());
        assertTrue(turma.isAtiva());
    }

    @Test
    void deveRemoverEspacosDosCampos() {
        Turma turma = new Turma(" Turma ", " Matematica ", " Tarde ", 10, " vol1 ");

        assertEquals("Turma", turma.getNome());
        assertEquals("Matematica", turma.getDisciplina());
        assertEquals("Tarde", turma.getTurno());
        assertEquals("vol1", turma.getVoluntarioId());
    }

    @Test
    void naoDevePermitirCamposTextuaisVazios() {
        assertThrows(IllegalArgumentException.class, () -> new Turma(" ", "Matematica", "Tarde", 10, "vol1"));
        assertThrows(IllegalArgumentException.class, () -> new Turma("Turma", "", "Tarde", 10, "vol1"));
        assertThrows(IllegalArgumentException.class, () -> new Turma("Turma", "Matematica", null, 10, "vol1"));
        assertThrows(IllegalArgumentException.class, () -> new Turma("Turma", "Matematica", "Tarde", 10, " "));
    }

    @Test
    void naoDevePermitirVagasForaDoIntervalo() {
        assertThrows(IllegalArgumentException.class, () -> new Turma("Turma", "Matematica", "Tarde", 0, "vol1"));
        assertThrows(IllegalArgumentException.class,
                () -> new Turma("Turma", "Matematica", "Tarde", Turma.VAGAS_MAXIMAS + 1, "vol1"));
        assertDoesNotThrow(() -> new Turma("Turma", "Matematica", "Tarde", 1, "vol1"));
        assertDoesNotThrow(() -> new Turma("Turma", "Matematica", "Tarde", Turma.VAGAS_MAXIMAS, "vol1"));
    }

    @Test
    void deveAtualizarDadosPreservandoOId() {
        Turma existente = new Turma("id1", "Turma A", "Matematica", "Tarde", 10, "vol1", true);
        Turma novosDados = new Turma("Turma B", "Fisica", "Manha", 20, "vol2");
        novosDados.setAtiva(false);

        existente.atualizarDados(novosDados);

        assertEquals("id1", existente.getId());
        assertEquals("Turma B", existente.getNome());
        assertEquals("Fisica", existente.getDisciplina());
        assertEquals("Manha", existente.getTurno());
        assertEquals(20, existente.getVagas());
        assertEquals("vol2", existente.getVoluntarioId());
        assertFalse(existente.isAtiva());
    }
}
