package br.com.educonecta.dto;

import br.com.educonecta.model.Responsavel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Responsavel pelo aluno (item da lista de subdocumentos)")
public record ResponsavelDto(
        @Schema(description = "Nome completo do responsavel", example = "Ana Souza")
        String nome,
        @Schema(description = "Grau de parentesco", example = "Mae")
        String parentesco,
        @Schema(description = "Telefone com DDD (10 ou 11 digitos)", example = "(44) 99999-1234")
        String telefone) {

    public Responsavel toEntity() {
        return new Responsavel(nome, parentesco, telefone);
    }

    public static ResponsavelDto fromEntity(Responsavel responsavel) {
        return new ResponsavelDto(responsavel.nome(), responsavel.parentesco(), responsavel.telefone());
    }

    public static List<Responsavel> toEntities(List<ResponsavelDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(dto -> {
            if (dto == null) {
                throw new IllegalArgumentException("Responsavel nao pode ser nulo.");
            }
            return dto.toEntity();
        }).toList();
    }
}
