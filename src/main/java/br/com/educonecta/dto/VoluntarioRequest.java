package br.com.educonecta.dto;

import br.com.educonecta.model.Voluntario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Dados de entrada para cadastro ou atualizacao (substituicao completa) de um voluntario")
public record VoluntarioRequest(
        @Schema(description = "Nome completo do voluntario", example = "Carlos Pereira")
        String nome,
        @Schema(description = "E-mail (unico entre os voluntarios)", example = "carlos.pereira@email.com")
        String email,
        @Schema(description = "Disciplinas que o voluntario leciona (ao menos uma)", example = "[\"Matematica\", \"Fisica\"]")
        List<String> disciplinas,
        @Schema(description = "Voluntario ativo (opcional, padrao true)", example = "true")
        Boolean ativo) {

    public Voluntario toEntity() {
        Voluntario voluntario = new Voluntario(nome, email, disciplinas);
        voluntario.setAtivo(ativo == null || ativo);
        return voluntario;
    }
}
