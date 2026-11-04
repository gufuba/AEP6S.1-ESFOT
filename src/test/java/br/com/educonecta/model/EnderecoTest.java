package br.com.educonecta.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnderecoTest {

    @Test
    void deveCriarEnderecoValido() {
        Endereco endereco = new Endereco("Rua A, 10", "Centro", "Maringa", "PR");

        assertEquals("Rua A, 10", endereco.rua());
        assertEquals("Centro", endereco.bairro());
        assertEquals("Maringa", endereco.cidade());
        assertEquals("PR", endereco.estado());
    }

    @Test
    void deveNormalizarEspacosEEstadoEmMaiusculo() {
        Endereco endereco = new Endereco("  Rua A ", " Centro ", " Maringa ", " pr ");

        assertEquals("Rua A", endereco.rua());
        assertEquals("Centro", endereco.bairro());
        assertEquals("Maringa", endereco.cidade());
        assertEquals("PR", endereco.estado());
    }

    @Test
    void naoDevePermitirCamposObrigatoriosVazios() {
        assertThrows(IllegalArgumentException.class, () -> new Endereco(" ", "Centro", "Maringa", "PR"));
        assertThrows(IllegalArgumentException.class, () -> new Endereco("Rua A", null, "Maringa", "PR"));
        assertThrows(IllegalArgumentException.class, () -> new Endereco("Rua A", "Centro", "", "PR"));
        assertThrows(IllegalArgumentException.class, () -> new Endereco("Rua A", "Centro", "Maringa", " "));
    }

    @Test
    void naoDevePermitirEstadoForaDoFormatoDeSigla() {
        assertThrows(IllegalArgumentException.class, () -> new Endereco("Rua A", "Centro", "Maringa", "PRR"));
        assertThrows(IllegalArgumentException.class, () -> new Endereco("Rua A", "Centro", "Maringa", "P1"));
    }
}
