package br.com.educonecta;

import br.com.educonecta.controller.AlunoController;
import br.com.educonecta.controller.MatriculaController;
import br.com.educonecta.controller.TurmaController;
import br.com.educonecta.controller.VoluntarioController;
import br.com.educonecta.exception.ApiExceptionHandler;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Sobe o contexto Spring completo (injecao de dependencias, MVC, Swagger) com o banco
 * simulado, sem exigir um MongoDB em execucao.
 */
@SpringBootTest(properties = "educonecta.mongodb.uri=mongodb://localhost:1/?serverSelectionTimeoutMS=300")
@AutoConfigureMockMvc
class EduConectaApplicationTests {

    @MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
    private MongoDatabase database;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCarregarOContextoComTodosOsComponentes() {
        assertNotNull(context.getBean(AlunoController.class));
        assertNotNull(context.getBean(VoluntarioController.class));
        assertNotNull(context.getBean(TurmaController.class));
        assertNotNull(context.getBean(MatriculaController.class));
        assertNotNull(context.getBean(ApiExceptionHandler.class));
    }

    @Test
    void deveDocumentarAApiNoOpenApiComTagsUnicasEOrdenadas() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("EduConecta API"))
                .andExpect(jsonPath("$.tags.length()").value(4))
                .andExpect(jsonPath("$.tags[0].name").value("Alunos"))
                .andExpect(jsonPath("$.tags[1].name").value("Voluntarios"))
                .andExpect(jsonPath("$.tags[2].name").value("Turmas"))
                .andExpect(jsonPath("$.tags[3].name").value("Matriculas"))
                .andExpect(jsonPath("$.paths.length()").value(8));
    }

    @Test
    void deveResponderErroPadronizadoParaIdInvalidoNoContextoCompleto() throws Exception {
        mockMvc.perform(get("/api/alunos/id-invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value(
                        "Id invalido: 'id-invalido'. Informe um identificador de 24 caracteres hexadecimais."));
    }
}
