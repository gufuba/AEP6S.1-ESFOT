package br.com.educonecta.controller;

import br.com.educonecta.dto.TurmaRequest;
import br.com.educonecta.dto.TurmaResponse;
import br.com.educonecta.service.TurmaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST de cadastro de turmas de reforco.
 */
@RestController
@RequestMapping("/api/turmas")
@Tag(name = "Turmas", description = "Turmas de reforco, cada uma conduzida por um voluntario (referencia entre colecoes)")
public class TurmaController {

    private final TurmaService service;

    public TurmaController(TurmaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma nova turma (exige voluntario existente, ativo e que lecione a disciplina)")
    public ResponseEntity<TurmaResponse> cadastrar(@RequestBody TurmaRequest request) {
        var cadastrada = service.cadastrar(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(TurmaResponse.fromEntity(cadastrada));
    }

    @GetMapping
    @Operation(summary = "Listar todas as turmas")
    public ResponseEntity<List<TurmaResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos().stream().map(TurmaResponse::fromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar uma turma pelo id")
    public ResponseEntity<TurmaResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(TurmaResponse.fromEntity(service.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar uma turma existente")
    public ResponseEntity<TurmaResponse> atualizar(@PathVariable String id, @RequestBody TurmaRequest request) {
        return ResponseEntity.ok(TurmaResponse.fromEntity(service.atualizar(id, request.toEntity())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover uma turma (somente se nao houver alunos matriculados)")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
