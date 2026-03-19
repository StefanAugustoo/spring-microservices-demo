package br.pucrs.microdemo.microservico2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonAlias;

import br.pucrs.microdemo.microservico2.service.DisciplineService;

@RestController
public class DisciplinesController {

    public record CadastrarDisciplinaRequest(
            @JsonAlias({"codigo", "codigoDisciplina"}) String codigo,
            String nome,
            List<String> horarios,
            @JsonAlias({"horario"}) String horario) {
    }

    private final DisciplineService service;

    public DisciplinesController(DisciplineService service) {
        this.service = service;
    }

    @PostMapping("/disciplinas")
    public ResponseEntity<DisciplineService.DisciplineWithHorarios> cadastrar(@RequestBody CadastrarDisciplinaRequest req) {
        List<String> horarios = req.horarios();
        if ((horarios == null || horarios.isEmpty()) && req.horario() != null && !req.horario().isBlank()) {
            horarios = List.of(req.horario());
        }
        DisciplineService.DisciplineWithHorarios created = service.register(req.codigo(), req.nome(), horarios);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/disciplinas")
    public List<DisciplineService.DisciplineSummary> listar() {
        return service.listDistinct();
    }

    @GetMapping("/disciplinas/{codigo}")
    public ResponseEntity<DisciplineService.DisciplineSummary> buscarPorCodigo(@PathVariable String codigo) {
        DisciplineService.DisciplineSummary disc = service.getByCodigo(codigo);
        if (disc == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(disc);
    }

    @GetMapping("/disciplinas/{codigo}/horarios")
    public ResponseEntity<List<String>> horarios(@PathVariable String codigo) {
        DisciplineService.DisciplineSummary disc = service.getByCodigo(codigo);
        if (disc == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.listHorarios(codigo));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(DisciplineService.DisciplineConflictException.class)
    public ResponseEntity<String> handleConflict(DisciplineService.DisciplineConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}

