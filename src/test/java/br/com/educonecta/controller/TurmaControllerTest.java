package br.com.educonecta.controller;

import br.com.educonecta.dto.TurmaRequest;
import br.com.educonecta.exception.ApiExceptionHandler;
import br.com.educonecta.exception.ConflitoException;
import br.com.educonecta.exception.RecursoNaoEncontradoException;
import br.com.educonecta.model.Turma;
import br.com.educonecta.service.TurmaService;
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

@ExtendWith(MockitoExtension.class)
class TurmaControllerTest {

    @Mock
    private TurmaService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TurmaController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    private String corpo(TurmaRequest request) throws Exception {
        return objectMapper.writeValueAsString(request);
    }

    private static Turma salva() {
        return new Turma("t1", "Matematica - Tarde A", "Matematica", "Tarde", 15, "vol1", true);
    }

    @Test
    void deveCadastrarTurmaERetornar201() throws Exception {
        when(service.cadastrar(any(Turma.class))).thenReturn(salva());

        mockMvc.perform(post("/api/turmas").contentType("application/json")
                        .content(corpo(new TurmaRequest("Matematica - Tarde A", "Matematica", "Tarde", 15, "vol1", null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("t1"))
                .andExpect(jsonPath("$.voluntarioId").value("vol1"))
                .andExpect(jsonPath("$.vagas").value(15))
                .andExpect(jsonPath("$.ativa").value(true));

        verify(service).cadastrar(argThat(t -> t.isAtiva() && t.getVagas() == 15));
    }

    @Test
    void deveRetornar400QuandoVagasNaoForInformada() throws Exception {
        mockMvc.perform(post("/api/turmas").contentType("application/json")
                        .content("{\"nome\":\"T\",\"disciplina\":\"Matematica\",\"turno\":\"Tarde\",\"voluntarioId\":\"vol1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Numero de vagas da turma e obrigatorio."));

        verifyNoInteractions(service);
    }

    @Test
    void deveRetornar400QuandoVagasForemInvalidas() throws Exception {
        mockMvc.perform(post("/api/turmas").contentType("application/json")
                        .content(corpo(new TurmaRequest("T", "Matematica", "Tarde", 0, "vol1", null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar409QuandoVoluntarioNaoLecionaADisciplina() throws Exception {
        when(service.cadastrar(any(Turma.class)))
                .thenThrow(new ConflitoException("Voluntario Carlos nao leciona a disciplina Historia."));

        mockMvc.perform(post("/api/turmas").contentType("application/json")
                        .content(corpo(new TurmaRequest("T", "Historia", "Tarde", 10, "vol1", null))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Voluntario Carlos nao leciona a disciplina Historia."));
    }

    @Test
    void deveRetornar404QuandoVoluntarioDaTurmaNaoExiste() throws Exception {
        when(service.cadastrar(any(Turma.class))).thenThrow(new RecursoNaoEncontradoException("Voluntario", "vol1"));

        mockMvc.perform(post("/api/turmas").contentType("application/json")
                        .content(corpo(new TurmaRequest("T", "Matematica", "Tarde", 10, "vol1", null))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveListarTurmas() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(salva()));

        mockMvc.perform(get("/api/turmas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].disciplina").value("Matematica"));
    }

    @Test
    void deveBuscarTurmaPorId() throws Exception {
        when(service.buscarPorId("t1")).thenReturn(salva());

        mockMvc.perform(get("/api/turmas/t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Matematica - Tarde A"));
    }

    @Test
    void deveRetornar404QuandoTurmaNaoExiste() throws Exception {
        when(service.buscarPorId("x")).thenThrow(new RecursoNaoEncontradoException("Turma", "x"));

        mockMvc.perform(get("/api/turmas/x"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Turma com id x nao encontrado."));
    }

    @Test
    void deveAtualizarTurma() throws Exception {
        when(service.atualizar(eq("t1"), any(Turma.class)))
                .thenReturn(new Turma("t1", "Nova", "Matematica", "Manha", 20, "vol1", false));

        mockMvc.perform(put("/api/turmas/t1").contentType("application/json")
                        .content(corpo(new TurmaRequest("Nova", "Matematica", "Manha", 20, "vol1", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.turno").value("Manha"))
                .andExpect(jsonPath("$.ativa").value(false));
    }

    @Test
    void deveRemoverTurmaERetornar204() throws Exception {
        mockMvc.perform(delete("/api/turmas/t1")).andExpect(status().isNoContent());

        verify(service).remover("t1");
    }

    @Test
    void deveRetornar409AoRemoverTurmaComAlunos() throws Exception {
        doThrow(new ConflitoException("Turma possui alunos matriculados e nao pode ser removida."))
                .when(service).remover("t1");

        mockMvc.perform(delete("/api/turmas/t1")).andExpect(status().isConflict());
    }
}
