package br.com.educonecta.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Metadados exibidos no Swagger UI. Os grupos de endpoints (tags) sao descritos nas
 * anotacoes @Tag dos controllers; aqui apenas definimos a ordem de exibicao.
 */
@Configuration
public class OpenApiConfig {

    private static final List<String> ORDEM_DAS_TAGS = List.of("Alunos", "Voluntarios", "Turmas", "Matriculas");

    @Bean
    public OpenAPI educonectaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("EduConecta API")
                .version("2.0.0")
                .description("Gestao de programas de reforco escolar gratuito (ODS 4 - Educacao de Qualidade): "
                        + "alunos, professores voluntarios, turmas e matriculas."));
    }

    @Bean
    public OpenApiCustomizer ordenarTags() {
        return openApi -> {
            if (openApi.getTags() != null) {
                List<Tag> ordenadas = new ArrayList<>(openApi.getTags());
                ordenadas.sort(Comparator.comparingInt(tag -> ORDEM_DAS_TAGS.indexOf(tag.getName())));
                openApi.setTags(ordenadas);
            }
        };
    }
}
