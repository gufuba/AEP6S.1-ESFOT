package br.com.educonecta.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlunoTest {

    @Test
    void deveCriarAlunoValidoComValoresPadrao() {
        Aluno aluno = new Aluno("Maria Silva", 10, "Matematica", "Tarde");

        assertNull(aluno.getId());
        assertEquals("Maria Silva", aluno.getNome());
        assertEquals(10, aluno.getIdade());
        assertEquals("Matematica", aluno.getCurso());
        assertEquals("Tarde", aluno.getTurno());
        assertTrue(aluno.isAtivo());
    }

    @Test
    void deveRemoverEspacosEmBrancoDosCampos() {
        Aluno aluno = new Aluno("  Joao  ", 12, " Portugues ", " Manha ");

        assertEquals("Joao", aluno.getNome());
        assertEquals("Portugues", aluno.getCurso());
        assertEquals("Manha", aluno.getTurno());
    }

    @Test
    void naoDevePermitirNomeVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aluno("   ", 10, "Matematica", "Tarde"));
    }

    @Test
    void naoDevePermitirNomeNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aluno(null, 10, "Matematica", "Tarde"));
    }

    @Test
    void naoDevePermitirIdadeNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aluno("Maria", -1, "Matematica", "Tarde"));
    }

    @Test
    void naoDevePermitirIdadeAcimaDoLimite() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aluno("Maria", 121, "Matematica", "Tarde"));
    }

    @Test
    void naoDevePermitirCursoVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aluno("Maria", 10, "", "Tarde"));
    }

    @Test
    void naoDevePermitirTurnoVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Aluno("Maria", 10, "Matematica", ""));
    }

    @Test
    void devePermitirAtualizarAtivo() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");
        aluno.setAtivo(false);
        assertFalse(aluno.isAtivo());
    }

    @Test
    void devePermitirDefinirId() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");
        aluno.setId("abc123");
        assertEquals("abc123", aluno.getId());
    }

    @Test
    void doisAlunosComMesmoIdDevemSerIguais() {
        Aluno a1 = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        Aluno a2 = new Aluno("id1", "Outro Nome", 15, "Portugues", "Manha", false);

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void alunoNuncaEIgualANull() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");
        assertNotEquals(null, aluno);
    }

    @Test
    void alunoEIgualAEleMesmo() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");
        assertEquals(aluno, aluno);
    }

    @Test
    void toStringDeveConterCamposPrincipais() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");
        String texto = aluno.toString();

        assertTrue(texto.contains("Maria"));
        assertTrue(texto.contains("Matematica"));
    }
}
