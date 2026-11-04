package br.com.educonecta.controller;

import br.com.educonecta.exception.ApiExceptionHandler;
import br.com.educonecta.exception.ConflitoException;
import br.com.educonecta.exception.RecursoNaoEncontradoException;
import br.com.educonecta.model.Aluno;
import br.com.educonecta.service.MatriculaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MatriculaControllerTest {

    @Mock
    private MatriculaService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MatriculaController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    private static Aluno alunoMatriculado() {
        Aluno aluno = new Aluno("a1", "Maria", 10, "Matematica", "Tarde", true);
        aluno.setTurmaId("t1");
        return aluno;
    }

    @Test
    void deveMatricularAlunoEDevolverOAlunoComATurma() throws Exception {
        when(service.matricular("t1", "a1")).thenReturn(alunoMatriculado());

        mockMvc.perform(post("/api/turmas/t1/alunos/a1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("a1"))
                .andExpect(jsonPath("$.turmaId").value("t1"));
    }

    @Test
    void deveRetornar409QuandoTurmaEstiverLotada() throws Exception {
        when(service.matricular("t1", "a1")).thenThrow(new ConflitoException("Turma sem vagas disponiveis."));

        mockMvc.perform(post("/api/turmas/t1/alunos/a1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Turma sem vagas disponiveis."));
    }

    @Test
    void deveRetornar404QuandoTurmaOuAlunoNaoExistem() throws Exception {
        when(service.matricular("x", "a1")).thenThrow(new RecursoNaoEncontradoException("Turma", "x"));

        mockMvc.perform(post("/api/turmas/x/alunos/a1")).andExpect(status().isNotFound());
    }

    @Test
    void deveDesmatricularAlunoERetornar204() throws Exception {
        mockMvc.perform(delete("/api/turmas/t1/alunos/a1")).andExpect(status().isNoContent());

        verify(service).desmatricular("t1", "a1");
    }

    @Test
    void deveRetornar409AoDesmatricularAlunoDeOutraTurma() throws Exception {
        doThrow(new ConflitoException("Aluno nao esta matriculado nesta turma."))
                .when(service).desmatricular("t1", "a1");

        mockMvc.perform(delete("/api/turmas/t1/alunos/a1")).andExpect(status().isConflict());
    }

    @Test
    void deveListarAlunosDaTurma() throws Exception {
        when(service.listarAlunosDaTurma("t1")).thenReturn(List.of(alunoMatriculado()));

        mockMvc.perform(get("/api/turmas/t1/alunos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Maria"))
                .andExpect(jsonPath("$[0].turmaId").value("t1"));
    }

    @Test
    void deveRetornar404AoListarAlunosDeTurmaInexistente() throws Exception {
        when(service.listarAlunosDaTurma("x")).thenThrow(new RecursoNaoEncontradoException("Turma", "x"));

        mockMvc.perform(get("/api/turmas/x/alunos")).andExpect(status().isNotFound());
    }
}
