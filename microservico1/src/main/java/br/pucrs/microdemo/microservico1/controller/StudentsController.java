package br.pucrs.microdemo.microservico1.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonAlias;

import br.pucrs.microdemo.microservico1.domain.Student;
import br.pucrs.microdemo.microservico1.service.StudentService;

@RestController
@RequestMapping
public class StudentsController {

    public record CadastrarEstudanteRequest(
            @JsonAlias({"nroMatricula", "nro_matricula", "matricula", "numeroMatricula", "numero_matricula"})
            Long matricula,
            String nome) {
    }

    public record MatricularRequest(
            @JsonAlias({"matricula", "numeroMatricula", "numero_matricula"})
            Long matricula,
            @JsonAlias({"codigo", "codigoDisciplina", "codDisciplina"})
            String codigoDisciplina,
            @JsonAlias({"horario", "horarios"})
            String horario) {
    }

    public record CadastrarDisciplinaRequest(
            @JsonAlias({"codigo", "codigoDisciplina"})
            String codigo,
            String nome,
            List<String> horarios,
            @JsonAlias({"horario"})
            String horario) {
    }

    private final StudentService service;

    public StudentsController(StudentService service) {
        this.service = service;
    }

    @PostMapping("/estudantes")
    public ResponseEntity<StudentService.StudentResponse> cadastrar(@RequestBody CadastrarEstudanteRequest req) {
        Student created = service.createStudent(req.matricula(), req.nome());
        StudentService.StudentResponse out = new StudentService.StudentResponse(created.getMatricula(), created.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @GetMapping("/estudantes/{matricula}")
    public ResponseEntity<StudentService.StudentResponse> buscarPorMatricula(@PathVariable Long matricula) {
        Student student = service.getByMatricula(matricula);
        StudentService.StudentResponse out = new StudentService.StudentResponse(student.getMatricula(), student.getNome());
        return ResponseEntity.ok(out);
    }

    @GetMapping("/estudantes/search")
    public ResponseEntity<?> buscarPorNomeParcial(@RequestParam("nome") String nomeParcial) {
        List<Student> matches = service.searchByNomeParcial(nomeParcial);
        List<StudentService.StudentResponse> mapped = matches.stream()
                .map(s -> new StudentService.StudentResponse(s.getMatricula(), s.getNome()))
                .collect(Collectors.toList());

        if (mapped.size() == 1) {
            return ResponseEntity.ok(mapped.get(0));
        }
        return ResponseEntity.ok(mapped);
    }

    // "escolher disciplina" + "escolher horário" (via proxy para o microserviço de disciplinas)
    @GetMapping("/disciplinas")
    public List<StudentService.DisciplineSummary> listarDisciplinas() {
        return service.listDisciplinesProxy();
    }

    @GetMapping("/disciplinas/{codigo}")
    public StudentService.DisciplineSummary buscarDisciplinaPorCodigo(@PathVariable("codigo") String codigo) {
        return service.getDisciplineByCodigoProxy(codigo);
    }

    @GetMapping("/disciplinas/{codigo}/horarios")
    public List<String> listarHorarios(@PathVariable("codigo") String codigo) {
        return service.listHorariosProxy(codigo);
    }

    @PostMapping("/disciplinas")
    public ResponseEntity<StudentService.DisciplineWithHorarios> cadastrarDisciplina(@RequestBody CadastrarDisciplinaRequest req) {
        List<String> horarios = req.horarios();
        if ((horarios == null || horarios.isEmpty()) && req.horario() != null && !req.horario().isBlank()) {
            horarios = List.of(req.horario());
        }
        StudentService.DisciplineWithHorarios created = service.registerDisciplineProxy(req.codigo(), req.nome(), horarios);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/matriculas")
    public ResponseEntity<StudentService.EnrollmentResponse> matricular(@RequestBody MatricularRequest req) {
        StudentService.EnrollmentResponse created = service.enroll(req.matricula(), req.codigoDisciplina(), req.horario());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @ExceptionHandler(StudentService.StudentNotFoundException.class)
    public ResponseEntity<String> handleStudentNotFound(StudentService.StudentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(StudentService.DisciplineNotFoundException.class)
    public ResponseEntity<String> handleDisciplineNotFound(StudentService.DisciplineNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(StudentService.EnrollmentConflictException.class)
    public ResponseEntity<String> handleConflict(StudentService.EnrollmentConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}

