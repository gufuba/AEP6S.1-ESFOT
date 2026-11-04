package br.com.educonecta.dto;

import br.com.educonecta.model.Aluno;
import br.com.educonecta.model.Endereco;
import br.com.educonecta.model.Responsavel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlunoDtoTest {

    private static final EnderecoDto ENDERECO = new EnderecoDto("Rua A, 10", "Centro", "Maringa", "pr");
    private static final ResponsavelDto MAE = new ResponsavelDto("Ana Souza", "Mae", "(44) 99999-1234");

    @Test
    void deveConverterRequestCompletoParaEntidade() {
        AlunoRequest request = new AlunoRequest("Maria", 10, "Matematica", "Tarde", false, ENDERECO, List.of(MAE));

        Aluno aluno = request.toEntity();

        assertEquals("Maria", aluno.getNome());
        assertFalse(aluno.isAtivo());
        assertEquals("PR", aluno.getEndereco().estado());
        assertEquals("44999991234", aluno.getResponsaveis().get(0).telefone());
    }

    @Test
    void deveAssumirAtivoQuandoNaoInformado() {
        AlunoRequest request = new AlunoRequest("Maria", 20, "Matematica", "Tarde", null, null, null);

        Aluno aluno = request.toEntity();

        assertTrue(aluno.isAtivo());
        assertNull(aluno.getEndereco());
        assertTrue(aluno.getResponsaveis().isEmpty());
    }

    @Test
    void naoDeveConverterRequestSemIdade() {
        AlunoRequest request = new AlunoRequest("Maria", null, "Matematica", "Tarde", null, null, null);

        assertThrows(IllegalArgumentException.class, request::toEntity);
    }

    @Test
    void naoDeveConverterRequestComResponsavelNulo() {
        List<ResponsavelDto> comNulo = new ArrayList<>();
        comNulo.add(null);
        AlunoRequest request = new AlunoRequest("Maria", 10, "Matematica", "Tarde", null, null, comNulo);

        assertThrows(IllegalArgumentException.class, request::toEntity);
    }

    @Test
    void naoDeveConverterRequestComEnderecoInvalido() {
        EnderecoDto invalido = new EnderecoDto("Rua A", "Centro", "Maringa", "PRR");
        AlunoRequest request = new AlunoRequest("Maria", 10, "Matematica", "Tarde", null, invalido, List.of(MAE));

        assertThrows(IllegalArgumentException.class, request::toEntity);
    }

    @Test
    void deveConverterEntidadeParaResponseComSubdocumentos() {
        Aluno aluno = new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true);
        aluno.setEndereco(new Endereco("Rua A", "Centro", "Maringa", "PR"));
        aluno.setResponsaveis(List.of(new Responsavel("Ana Souza", "Mae", "44999991234")));

        AlunoResponse response = AlunoResponse.fromEntity(aluno);

        assertEquals("id1", response.id());
        assertEquals("Maringa", response.endereco().cidade());
        assertEquals("Ana Souza", response.responsaveis().get(0).nome());
    }

    @Test
    void deveConverterEntidadeSemEnderecoParaResponseComEnderecoNulo() {
        Aluno aluno = new Aluno("id1", "Carlos", 20, "Matematica", "Noite", true);

        AlunoResponse response = AlunoResponse.fromEntity(aluno);

        assertNull(response.endereco());
        assertTrue(response.responsaveis().isEmpty());
    }
}
