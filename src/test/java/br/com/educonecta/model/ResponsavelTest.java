package br.com.educonecta.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponsavelTest {

    @Test
    void deveCriarResponsavelValido() {
        Responsavel responsavel = new Responsavel("Ana Souza", "Mae", "44999991234");

        assertEquals("Ana Souza", responsavel.nome());
        assertEquals("Mae", responsavel.parentesco());
        assertEquals("44999991234", responsavel.telefone());
    }

    @Test
    void deveGuardarTelefoneApenasComDigitos() {
        assertEquals("44999991234", new Responsavel("Ana", "Mae", "(44) 99999-1234").telefone());
        assertEquals("4433221100", new Responsavel("Ana", "Mae", "44 3322-1100").telefone());
    }

    @Test
    void naoDevePermitirCamposVazios() {
        assertThrows(IllegalArgumentException.class, () -> new Responsavel("", "Mae", "44999991234"));
        assertThrows(IllegalArgumentException.class, () -> new Responsavel("Ana", null, "44999991234"));
        assertThrows(IllegalArgumentException.class, () -> new Responsavel("Ana", "Mae", "  "));
    }

    @Test
    void naoDevePermitirTelefoneComQuantidadeInvalidaDeDigitos() {
        assertThrows(IllegalArgumentException.class, () -> new Responsavel("Ana", "Mae", "99999-123"));
        assertThrows(IllegalArgumentException.class, () -> new Responsavel("Ana", "Mae", "449999912345"));
    }
}
