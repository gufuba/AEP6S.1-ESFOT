package br.com.educonecta.controller;

import br.com.educonecta.dto.AlunoResponse;
import br.com.educonecta.service.MatriculaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints do relacionamento entre turmas e alunos (matriculas).
 */
@RestController
@RequestMapping("/api/turmas/{turmaId}/alunos")
@Tag(name = "Matriculas", description = "Matricula de alunos em turmas (relacionamento entre colecoes)")
public class MatriculaController {

    private final MatriculaService service;

    public MatriculaController(MatriculaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar os alunos matriculados em uma turma")
    public ResponseEntity<List<AlunoResponse>> listar(@PathVariable String turmaId) {
        return ResponseEntity.ok(service.listarAlunosDaTurma(turmaId).stream().map(AlunoResponse::fromEntity).toList());
    }

    @PostMapping("/{alunoId}")
    @Operation(summary = "Matricular um aluno na turma (valida disciplina, vagas e situacao ativa)")
    public ResponseEntity<AlunoResponse> matricular(@PathVariable String turmaId, @PathVariable String alunoId) {
        return ResponseEntity.ok(AlunoResponse.fromEntity(service.matricular(turmaId, alunoId)));
    }

    @DeleteMapping("/{alunoId}")
    @Operation(summary = "Remover a matricula de um aluno da turma")
    public ResponseEntity<Void> desmatricular(@PathVariable String turmaId, @PathVariable String alunoId) {
        service.desmatricular(turmaId, alunoId);
        return ResponseEntity.noContent().build();
    }
}
