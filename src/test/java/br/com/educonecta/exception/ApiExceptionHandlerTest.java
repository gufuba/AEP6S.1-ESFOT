package br.com.educonecta.exception;

import com.mongodb.MongoTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApiExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ControllerQueLancaExcecoes())
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void deveMapearRecursoNaoEncontradoPara404() throws Exception {
        mockMvc.perform(get("/teste/nao-encontrado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Aluno com id x nao encontrado."));
    }

    @Test
    void deveMapearConflitoPara409() throws Exception {
        mockMvc.perform(get("/teste/conflito"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem").value("regra violada"));
    }

    @Test
    void deveMapearArgumentoInvalidoPara400() throws Exception {
        mockMvc.perform(get("/teste/argumento"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("dado invalido"));
    }

    @Test
    void deveMapearJsonMalFormatadoPara400() throws Exception {
        mockMvc.perform(post("/teste/corpo").contentType("application/json").content("{nao-e-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveMapearCorpoAusentePara400() throws Exception {
        mockMvc.perform(post("/teste/corpo").contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveMapearBancoIndisponivelPara503SemVazarDetalhesInternos() throws Exception {
        mockMvc.perform(get("/teste/banco"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.mensagem").value("Banco de dados indisponivel no momento."));
    }

    @RestController
    @RequestMapping("/teste")
    static class ControllerQueLancaExcecoes {

        @GetMapping("/nao-encontrado")
        void naoEncontrado() {
            throw new RecursoNaoEncontradoException("Aluno", "x");
        }

        @GetMapping("/conflito")
        void conflito() {
            throw new ConflitoException("regra violada");
        }

        @GetMapping("/argumento")
        void argumento() {
            throw new IllegalArgumentException("dado invalido");
        }

        @GetMapping("/banco")
        void banco() {
            throw new MongoTimeoutException("detalhe interno do driver");
        }

        @PostMapping("/corpo")
        void corpo(@RequestBody Map<String, Object> corpo) {
        }
    }
}
