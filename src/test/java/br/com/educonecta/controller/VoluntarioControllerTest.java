package br.com.educonecta.controller;

import br.com.educonecta.dto.VoluntarioRequest;
import br.com.educonecta.exception.ApiExceptionHandler;
import br.com.educonecta.exception.ConflitoException;
import br.com.educonecta.exception.RecursoNaoEncontradoException;
import br.com.educonecta.model.Voluntario;
import br.com.educonecta.service.VoluntarioService;
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
class VoluntarioControllerTest {

    @Mock
    private VoluntarioService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new VoluntarioController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    private String corpo(VoluntarioRequest request) throws Exception {
        return objectMapper.writeValueAsString(request);
    }

    private static Voluntario salvo() {
        return new Voluntario("id1", "Carlos", "carlos@email.com", List.of("Matematica", "Fisica"), true);
    }

    @Test
    void deveCadastrarVoluntarioERetornar201() throws Exception {
        when(service.cadastrar(any(Voluntario.class))).thenReturn(salvo());

        mockMvc.perform(post("/api/voluntarios").contentType("application/json")
                        .content(corpo(new VoluntarioRequest("Carlos", "carlos@email.com",
                                List.of("Matematica", "Fisica"), null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("id1"))
                .andExpect(jsonPath("$.disciplinas[1]").value("Fisica"))
                .andExpect(jsonPath("$.ativo").value(true));

        verify(service).cadastrar(argThat(v -> v.isAtivo() && "carlos@email.com".equals(v.getEmail())));
    }

    @Test
    void deveRetornar400QuandoEmailForInvalido() throws Exception {
        mockMvc.perform(post("/api/voluntarios").contentType("application/json")
                        .content(corpo(new VoluntarioRequest("Carlos", "sem-arroba", List.of("Matematica"), null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("E-mail do voluntario invalido."));

        verifyNoInteractions(service);
    }

    @Test
    void deveRetornar400QuandoNaoInformarDisciplinas() throws Exception {
        mockMvc.perform(post("/api/voluntarios").contentType("application/json")
                        .content("{\"nome\":\"Carlos\",\"email\":\"carlos@email.com\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar409QuandoEmailJaCadastrado() throws Exception {
        when(service.cadastrar(any(Voluntario.class)))
                .thenThrow(new ConflitoException("Ja existe voluntario cadastrado com o e-mail carlos@email.com."));

        mockMvc.perform(post("/api/voluntarios").contentType("application/json")
                        .content(corpo(new VoluntarioRequest("Carlos", "carlos@email.com",
                                List.of("Matematica"), null))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void deveListarVoluntarios() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(salvo()));

        mockMvc.perform(get("/api/voluntarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Carlos"));
    }

    @Test
    void deveBuscarVoluntarioPorId() throws Exception {
        when(service.buscarPorId("id1")).thenReturn(salvo());

        mockMvc.perform(get("/api/voluntarios/id1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("carlos@email.com"));
    }

    @Test
    void deveRetornar404QuandoVoluntarioNaoExiste() throws Exception {
        when(service.buscarPorId("x")).thenThrow(new RecursoNaoEncontradoException("Voluntario", "x"));

        mockMvc.perform(get("/api/voluntarios/x"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Voluntario com id x nao encontrado."));
    }

    @Test
    void deveAtualizarVoluntario() throws Exception {
        when(service.atualizar(eq("id1"), any(Voluntario.class)))
                .thenReturn(new Voluntario("id1", "Carlos Pereira", "carlos@email.com", List.of("Historia"), false));

        mockMvc.perform(put("/api/voluntarios/id1").contentType("application/json")
                        .content(corpo(new VoluntarioRequest("Carlos Pereira", "carlos@email.com",
                                List.of("Historia"), false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Pereira"))
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    void deveRemoverVoluntarioERetornar204() throws Exception {
        mockMvc.perform(delete("/api/voluntarios/id1"))
                .andExpect(status().isNoContent());

        verify(service).remover("id1");
    }
}
