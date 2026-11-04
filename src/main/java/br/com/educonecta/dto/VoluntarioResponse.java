package br.com.educonecta.dto;

import br.com.educonecta.model.Voluntario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Professor voluntario cadastrado")
public record VoluntarioResponse(
        @Schema(description = "Identificador gerado pelo MongoDB", example = "6512f1a2b3c4d5e6f7a8b9c1")
        String id,
        @Schema(description = "Nome completo do voluntario", example = "Carlos Pereira")
        String nome,
        @Schema(description = "E-mail do voluntario", example = "carlos.pereira@email.com")
        String email,
        @Schema(description = "Disciplinas que o voluntario leciona", example = "[\"Matematica\", \"Fisica\"]")
        List<String> disciplinas,
        @Schema(description = "Indica se o voluntario esta ativo", example = "true")
        boolean ativo) {

    public static VoluntarioResponse fromEntity(Voluntario voluntario) {
        return new VoluntarioResponse(
                voluntario.getId(),
                voluntario.getNome(),
                voluntario.getEmail(),
                voluntario.getDisciplinas(),
                voluntario.isAtivo());
    }
}
