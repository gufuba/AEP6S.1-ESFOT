package br.com.educonecta.dto;

import br.com.educonecta.model.Endereco;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Endereco do aluno (subdocumento)")
public record EnderecoDto(
        @Schema(description = "Rua e numero", example = "Rua das Flores, 120")
        String rua,
        @Schema(description = "Bairro", example = "Jardim Alvorada")
        String bairro,
        @Schema(description = "Cidade", example = "Maringa")
        String cidade,
        @Schema(description = "Sigla do estado (UF)", example = "PR")
        String estado) {

    public Endereco toEntity() {
        return new Endereco(rua, bairro, cidade, estado);
    }

    public static EnderecoDto fromEntity(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new EnderecoDto(endereco.rua(), endereco.bairro(), endereco.cidade(), endereco.estado());
    }
}
