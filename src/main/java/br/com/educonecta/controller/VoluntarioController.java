package br.com.educonecta.controller;

import br.com.educonecta.dto.VoluntarioRequest;
import br.com.educonecta.dto.VoluntarioResponse;
import br.com.educonecta.service.VoluntarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST de cadastro de professores voluntarios.
 */
@RestController
@RequestMapping("/api/voluntarios")
@Tag(name = "Voluntarios", description = "Professores voluntarios que conduzem as turmas de reforco")
public class VoluntarioController {

    private final VoluntarioService service;

    public VoluntarioController(VoluntarioService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo voluntario")
    public ResponseEntity<VoluntarioResponse> cadastrar(@RequestBody VoluntarioRequest request) {
        var cadastrado = service.cadastrar(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(VoluntarioResponse.fromEntity(cadastrado));
    }

    @GetMapping
    @Operation(summary = "Listar todos os voluntarios")
    public ResponseEntity<List<VoluntarioResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos().stream().map(VoluntarioResponse::fromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um voluntario pelo id")
    public ResponseEntity<VoluntarioResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(VoluntarioResponse.fromEntity(service.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um voluntario existente")
    public ResponseEntity<VoluntarioResponse> atualizar(@PathVariable String id,
                                                        @RequestBody VoluntarioRequest request) {
        return ResponseEntity.ok(VoluntarioResponse.fromEntity(service.atualizar(id, request.toEntity())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover um voluntario")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
