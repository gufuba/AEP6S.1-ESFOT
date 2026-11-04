package br.com.educonecta.controller;

import br.com.educonecta.dto.AlunoRequest;
import br.com.educonecta.exception.ApiExceptionHandler;
import br.com.educonecta.exception.RecursoNaoEncontradoException;
import br.com.educonecta.model.Aluno;
import br.com.educonecta.service.AlunoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testa a camada web isoladamente (MockMvc standalone), com o servico mockado.
 * Nao sobe contexto Spring nem exige MongoDB em execucao.
 */
@ExtendWith(MockitoExtension.class)
class AlunoControllerTest {

    @Mock
    private AlunoService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AlunoController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void deveCadastrarAlunoERetornar201() throws Exception {
        AlunoRequest request = new AlunoRequest("Maria", 10, "Matematica", "Tarde");
        when(service.cadastrar(any(Aluno.class)))
                .thenReturn(new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true));

        mockMvc.perform(post("/api/alunos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("id1"))
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void deveRetornar400QuandoIdadeNaoForInformada() throws Exception {
        mockMvc.perform(post("/api/alunos")
                        .contentType("application/json")
                        .content("{\"nome\":\"Maria\",\"curso\":\"Matematica\",\"turno\":\"Tarde\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Idade do aluno e obrigatoria."));

        verifyNoInteractions(service);
    }

    @Test
    void deveRetornar400QuandoServicoRejeitarOCadastro() throws Exception {
        AlunoRequest request = new AlunoRequest("Bebe", 3, "Matematica", "Tarde");
        when(service.cadastrar(any(Aluno.class)))
                .thenThrow(new IllegalArgumentException("Aluno deve ter no minimo 6 anos para ingressar no programa."));

        mockMvc.perform(post("/api/alunos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveListarAlunosCadastrados() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(
                new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true),
                new Aluno("id2", "Joao", 12, "Portugues", "Manha", true)));

        mockMvc.perform(get("/api/alunos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Maria"));
    }

    @Test
    void deveBuscarAlunoPorIdComSucesso() throws Exception {
        when(service.buscarPorId("id1")).thenReturn(new Aluno("id1", "Maria", 10, "Matematica", "Tarde", true));

        mockMvc.perform(get("/api/alunos/id1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria"));
    }

    @Test
    void deveRetornar404QuandoAlunoNaoExiste() throws Exception {
        when(service.buscarPorId("naoExiste")).thenThrow(new RecursoNaoEncontradoException("Aluno", "naoExiste"));

        mockMvc.perform(get("/api/alunos/naoExiste"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Aluno com id naoExiste nao encontrado."));
    }

    @Test
    void deveAtualizarAlunoExistente() throws Exception {
        AlunoRequest request = new AlunoRequest("Maria Silva", 11, "Portugues", "Manha");
        when(service.atualizar(eq("id1"), any(Aluno.class)))
                .thenReturn(new Aluno("id1", "Maria Silva", 11, "Portugues", "Manha", true));

        mockMvc.perform(put("/api/alunos/id1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Silva"))
                .andExpect(jsonPath("$.curso").value("Portugues"));
    }

    @Test
    void deveRemoverAlunoERetornar204() throws Exception {
        mockMvc.perform(delete("/api/alunos/id1"))
                .andExpect(status().isNoContent());

        verify(service).remover("id1");
    }
}
