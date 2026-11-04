package br.com.educonecta.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
                () -> new Aluno("Maria", Aluno.IDADE_MAXIMA + 1, "Matematica", "Tarde"));
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
    void deveAtualizarDadosPreservandoOId() {
        Aluno existente = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        Aluno novosDados = new Aluno("Maria Souza", 11, "Portugues", "Manha");
        novosDados.setAtivo(false);

        existente.atualizarDados(novosDados);

        assertEquals("id1", existente.getId());
        assertEquals("Maria Souza", existente.getNome());
        assertEquals(11, existente.getIdade());
        assertEquals("Portugues", existente.getCurso());
        assertEquals("Manha", existente.getTurno());
        assertFalse(existente.isAtivo());
    }

    @Test
    void deveIniciarSemEnderecoENemResponsaveis() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");

        assertNull(aluno.getEndereco());
        assertTrue(aluno.getResponsaveis().isEmpty());
    }

    @Test
    void deveGuardarEnderecoEResponsaveis() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");
        Endereco endereco = new Endereco("Rua A", "Centro", "Maringa", "PR");
        Responsavel mae = new Responsavel("Ana", "Mae", "44999991234");

        aluno.setEndereco(endereco);
        aluno.setResponsaveis(List.of(mae));

        assertEquals(endereco, aluno.getEndereco());
        assertEquals(List.of(mae), aluno.getResponsaveis());
    }

    @Test
    void deveTratarListaNulaDeResponsaveisComoVazia() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");
        aluno.setResponsaveis(List.of(new Responsavel("Ana", "Mae", "44999991234")));

        aluno.setResponsaveis(null);

        assertTrue(aluno.getResponsaveis().isEmpty());
    }

    @Test
    void naoDevePermitirResponsavelNuloNaLista() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");
        List<Responsavel> comNulo = new ArrayList<>();
        comNulo.add(null);

        assertThrows(IllegalArgumentException.class, () -> aluno.setResponsaveis(comNulo));
    }

    @Test
    void deveProtegerListaDeResponsaveisContraAlteracaoExterna() {
        Aluno aluno = new Aluno("Maria", 10, "Matematica", "Tarde");
        List<Responsavel> original = new ArrayList<>(List.of(new Responsavel("Ana", "Mae", "44999991234")));
        aluno.setResponsaveis(original);

        original.add(new Responsavel("Jose", "Pai", "44988887777"));

        assertEquals(1, aluno.getResponsaveis().size());
        assertThrows(UnsupportedOperationException.class,
                () -> aluno.getResponsaveis().add(new Responsavel("Jose", "Pai", "44988887777")));
    }

    @Test
    void deveGuardarTurmaEPreservarlaAoAtualizarDados() {
        Aluno existente = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        existente.setTurmaId("turma1");
        assertNull(new Aluno("Maria", 10, "Matematica", "Tarde").getTurmaId());

        existente.atualizarDados(new Aluno("Maria Souza", 10, "Matematica", "Tarde"));

        assertEquals("turma1", existente.getTurmaId());
        assertTrue(existente.toString().contains("turma1"));
    }

    @Test
    void deveIdentificarMenorDeIdade() {
        assertTrue(new Aluno("Maria", 17, "Matematica", "Tarde").isMenorDeIdade());
        assertFalse(new Aluno("Maria", 18, "Matematica", "Tarde").isMenorDeIdade());
    }

    @Test
    void deveCopiarEnderecoEResponsaveisAoAtualizarDados() {
        Aluno existente = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        Aluno novosDados = new Aluno("Maria", 10, "Matematica", "Tarde");
        novosDados.setEndereco(new Endereco("Rua B", "Vila", "Sarandi", "PR"));
        novosDados.setResponsaveis(List.of(new Responsavel("Ana", "Mae", "44999991234")));

        existente.atualizarDados(novosDados);

        assertEquals("Sarandi", existente.getEndereco().cidade());
        assertEquals(1, existente.getResponsaveis().size());
    }

    @Test
    void doisAlunosComMesmoIdDevemSerIguais() {
        Aluno a1 = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        Aluno a2 = new Aluno("id1", "Outro Nome", 15, "Portugues", "Manha", false);

        assertEquals(a1, a2);
        assertEquals(a1.hashCode(), a2.hashCode());
    }

    @Test
    void alunosSemIdNaoDevemSerIguais() {
        Aluno a1 = new Aluno("Maria", 10, "Matematica", "Tarde");
        Aluno a2 = new Aluno("Maria", 10, "Matematica", "Tarde");

        assertNotEquals(a1, a2);
    }

    @Test
    void alunoNuncaEIgualANullNemAOutroTipo() {
        Aluno aluno = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);

        assertFalse(aluno.equals(null));
        assertFalse(aluno.equals("texto"));
    }

    @Test
    void alunosComIdsDiferentesOuApenasUmComIdNaoSaoIguais() {
        Aluno a1 = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        Aluno a2 = new Aluno("id2", "Maria", 10, "Matematica", "Tarde", true);
        Aluno semId = new Aluno("Maria", 10, "Matematica", "Tarde");

        assertNotEquals(a1, a2);
        assertNotEquals(a1, semId);
        assertNotEquals(semId, a1);
    }

    @Test
    void entidadesDeTiposDiferentesNaoSaoIguaisMesmoComIdIgual() {
        Aluno aluno = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        Turma turma = new Turma("id1", "Turma", "Matematica", "Tarde", 10, "vol1", true);

        assertNotEquals(aluno, turma);
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
