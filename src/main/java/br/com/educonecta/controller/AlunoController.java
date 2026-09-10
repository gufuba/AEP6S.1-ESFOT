package br.com.educonecta.controller;

import br.com.educonecta.dto.AlunoRequest;
import br.com.educonecta.dto.AlunoResponse;
import br.com.educonecta.model.Aluno;
import br.com.educonecta.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST de cadastro de alunos do programa de reforco escolar.
 * Documentados via Swagger/OpenAPI (springdoc), disponivel em /swagger-ui.html.
 */
@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Alunos", description = "Cadastro de alunos do programa de reforco escolar (ODS 4)")
public class AlunoController {

    private final AlunoService service;

    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar um novo aluno no programa de reforco escolar")
    public ResponseEntity<AlunoResponse> cadastrar(@RequestBody AlunoRequest request) {
        Aluno aluno = new Aluno(request.getNome(), request.getIdade(), request.getCurso(), request.getTurno());
        Aluno cadastrado = service.cadastrar(aluno);
        return ResponseEntity.status(HttpStatus.CREATED).body(AlunoResponse.fromEntity(cadastrado));
    }

    @GetMapping
    @Operation(summary = "Listar todos os alunos cadastrados")
    public ResponseEntity<List<AlunoResponse>> listar() {
        List<AlunoResponse> alunos = service.listarTodos().stream()
                .map(AlunoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um aluno pelo id")
    public ResponseEntity<AlunoResponse> buscarPorId(@PathVariable String id) {
        Aluno aluno = service.buscarPorId(id);
        return ResponseEntity.ok(AlunoResponse.fromEntity(aluno));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um aluno existente")
    public ResponseEntity<AlunoResponse> atualizar(@PathVariable String id, @RequestBody AlunoRequest request) {
        Aluno existente = service.buscarPorId(id);
        existente.setNome(request.getNome());
        existente.setIdade(request.getIdade());
        existente.setCurso(request.getCurso());
        existente.setTurno(request.getTurno());

        Aluno atualizado = service.atualizar(existente);
        return ResponseEntity.ok(AlunoResponse.fromEntity(atualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover um aluno do programa")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
