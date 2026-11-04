package br.com.educonecta.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.tags.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OpenApiConfigTest {

    @Test
    void deveDefinirTituloEVersaoDaApi() {
        OpenAPI api = new OpenApiConfig().educonectaOpenApi();

        assertEquals("EduConecta API", api.getInfo().getTitle());
        assertEquals("2.0.0", api.getInfo().getVersion());
    }

    @Test
    void deveOrdenarAsTagsConformeAOrdemDefinida() {
        OpenAPI api = new OpenAPI().tags(List.of(
                new Tag().name("Matriculas"), new Tag().name("Turmas"),
                new Tag().name("Alunos"), new Tag().name("Voluntarios")));

        new OpenApiConfig().ordenarTags().customise(api);

        assertEquals(List.of("Alunos", "Voluntarios", "Turmas", "Matriculas"),
                api.getTags().stream().map(Tag::getName).toList());
    }

    @Test
    void deveIgnorarDocumentoSemTags() {
        OpenAPI api = new OpenAPI();

        new OpenApiConfig().ordenarTags().customise(api);

        assertNull(api.getTags());
    }
}
