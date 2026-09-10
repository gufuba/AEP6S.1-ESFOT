package br.com.educonecta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da API REST. A interacao com a PoC acontece via
 * Swagger UI (http://localhost:8080/swagger-ui.html), sem necessidade
 * de front-end proprio nesta entrega.
 */
@SpringBootApplication
public class EduConectaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduConectaApplication.class, args);
    }
}
