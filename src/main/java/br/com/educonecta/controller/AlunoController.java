package br.com.educonecta.controller;

import br.com.educonecta.dto.AlunoRequest;
import br.com.educonecta.dto.AlunoResponse;
import br.com.educonecta.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST de cadastro de alunos do programa de reforco escolar.
 */
@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Alunos", description = "Cadastro de alunos, com endereco (subdocumento) e responsaveis (lista de subdocumentos)")
public class AlunoController {

    private final AlunoService service;

    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo aluno no programa de reforco escolar")
    public ResponseEntity<AlunoResponse> cadastrar(@RequestBody AlunoRequest request) {
        var cadastrado = service.cadastrar(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(AlunoResponse.fromEntity(cadastrado));
    }

    @GetMapping
    @Operation(summary = "Listar todos os alunos cadastrados")
    public ResponseEntity<List<AlunoResponse>> listar() {
        return ResponseEntity.ok(service.listarTodos().stream().map(AlunoResponse::fromEntity).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um aluno pelo id")
    public ResponseEntity<AlunoResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(AlunoResponse.fromEntity(service.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um aluno existente")
    public ResponseEntity<AlunoResponse> atualizar(@PathVariable String id, @RequestBody AlunoRequest request) {
        return ResponseEntity.ok(AlunoResponse.fromEntity(service.atualizar(id, request.toEntity())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover um aluno do programa")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
