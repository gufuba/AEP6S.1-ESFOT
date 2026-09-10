package br.com.educonecta.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Corpo padrao de resposta de erro da API")
public class ErroResponse {

    @Schema(example = "404")
    private final int status;

    @Schema(example = "Aluno com id ... nao encontrado.")
    private final String mensagem;

    public ErroResponse(int status, String mensagem) {
        this.status = status;
        this.mensagem = mensagem;
    }

    public int getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }
}
