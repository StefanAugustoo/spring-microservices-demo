package br.pucrs.microdemo.microservico1.service;

import java.util.List;
import java.util.Map;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import br.pucrs.microdemo.microservico1.domain.Enrollment;
import br.pucrs.microdemo.microservico1.domain.Student;
import br.pucrs.microdemo.microservico1.repository.EnrollmentRepository;
import br.pucrs.microdemo.microservico1.repository.StudentRepository;

@Service
public class StudentService {

    public record StudentResponse(Long matricula, String nome) {
    }

    public record DisciplineSummary(String codigo, String nome) {
    }

    public record DisciplineWithHorarios(String codigo, String nome, List<String> horarios) {
    }

    public record EnrollmentResponse(Long matricula, String nomeEstudante, String codigoDisciplina, String nomeDisciplina,
            String horario) {
    }

    public static class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(String message) {
            super(message);
        }
    }

    public static class DisciplineNotFoundException extends RuntimeException {
        public DisciplineNotFoundException(String message) {
            super(message);
        }
    }

    public static class EnrollmentConflictException extends RuntimeException {
        public EnrollmentConflictException(String message) {
            super(message);
        }
    }

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final RestTemplate restTemplate;

    private final String disciplinaServiceUrl;

    public StudentService(StudentRepository studentRepository, EnrollmentRepository enrollmentRepository,
            RestTemplate restTemplate,
            @Value("${disciplina.service.url}") String disciplinaServiceUrl) {
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.restTemplate = restTemplate;
        this.disciplinaServiceUrl = stripTrailingSlash(disciplinaServiceUrl);
    }

    public Student createStudent(Long matricula, String nome) {
        if (matricula == null) {
            throw new IllegalArgumentException("O campo 'matricula' é obrigatório.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O campo 'nome' é obrigatório.");
        }
        if (studentRepository.findByMatricula(matricula).isPresent()) {
            throw new EnrollmentConflictException("Já existe estudante cadastrado com matrícula '" + matricula + "'.");
        }

        Student student = new Student(matricula, nome.trim());
        return studentRepository.save(student);
    }

    public Student getByMatricula(Long matricula) {
        return studentRepository.findByMatricula(matricula)
                .orElseThrow(() -> new StudentNotFoundException("Estudante não encontrado para matrícula '" + matricula + "'."));
    }

    public List<Student> searchByNomeParcial(String nomeParcial) {
        if (nomeParcial == null || nomeParcial.isBlank()) {
            throw new IllegalArgumentException("O parâmetro 'nome' é obrigatório.");
        }
        return studentRepository.findByNomeContainingIgnoreCase(nomeParcial.trim());
    }

    public List<DisciplineSummary> listDisciplinesProxy() {
        String url = disciplinaServiceUrl + "/disciplinas";
        ResponseEntity<DisciplineSummary[]> resp = restTemplate.getForEntity(url, DisciplineSummary[].class);
        DisciplineSummary[] body = resp.getBody();
        return body == null ? List.of() : List.of(body);
    }

    public DisciplineSummary getDisciplineByCodigoProxy(String codigoDisciplina) {
        String url = disciplinaServiceUrl + "/disciplinas/" + normalizeCodigo(codigoDisciplina);
        try {
            ResponseEntity<DisciplineSummary> resp = restTemplate.getForEntity(url, DisciplineSummary.class);
            return resp.getBody();
        } catch (HttpClientErrorException.NotFound notFound) {
            throw new DisciplineNotFoundException("Disciplina não encontrada para código '" + codigoDisciplina + "'.");
        }
    }

    public DisciplineWithHorarios registerDisciplineProxy(String codigo, String nome, List<String> horarios) {
        String url = disciplinaServiceUrl + "/disciplinas";
        Map<String, Object> req = Map.of("codigo", codigo, "nome", nome, "horarios", horarios);
        try {
            ResponseEntity<DisciplineWithHorarios> resp = restTemplate.postForEntity(url, req, DisciplineWithHorarios.class);
            return resp.getBody();
        } catch (HttpClientErrorException.BadRequest badRequest) {
            throw new IllegalArgumentException(badRequest.getResponseBodyAsString());
        } catch (HttpClientErrorException.Conflict conflict) {
            throw new EnrollmentConflictException(conflict.getResponseBodyAsString());
        }
    }

    public List<String> listHorariosProxy(String codigoDisciplina) {
        String url = disciplinaServiceUrl + "/disciplinas/" + normalizeCodigo(codigoDisciplina) + "/horarios";
        ResponseEntity<String[]> resp = restTemplate.getForEntity(url, String[].class);
        String[] body = resp.getBody();
        return body == null ? List.of() : List.of(body);
    }

    public EnrollmentResponse enroll(Long matricula, String codigoDisciplina, String horario) {
        Student student = getByMatricula(matricula);

        String codigoNorm = normalizeCodigo(codigoDisciplina);
        String horarioNorm = normalizeHorario(horario);

        if (enrollmentRepository.existsByStudentAndDisciplinaCodigo(student, codigoNorm)) {
            throw new EnrollmentConflictException(
                    "Estudante '" + matricula + "' já está matriculado na disciplina '" + codigoNorm + "'.");
        }

        DisciplineSummary disc = getDisciplineByCodigoOrThrow(codigoNorm);
        List<String> horarios = listHorariosProxy(codigoNorm);
        if (!horarios.contains(horarioNorm)) {
            throw new IllegalArgumentException("Horário '" + horarioNorm + "' não disponível para a disciplina '" + codigoNorm + "'.");
        }

        Enrollment enrollment = new Enrollment(student, codigoNorm, disc.nome(), horarioNorm);
        Enrollment saved = enrollmentRepository.save(enrollment);

        return new EnrollmentResponse(saved.getStudent().getMatricula(), saved.getStudent().getNome(), saved.getDisciplinaCodigo(),
                saved.getDisciplinaNome(), saved.getHorario());
    }

    private DisciplineSummary getDisciplineByCodigoOrThrow(String codigoNorm) {
        String url = disciplinaServiceUrl + "/disciplinas/" + codigoNorm;
        try {
            ResponseEntity<DisciplineSummary> resp = restTemplate.getForEntity(url, DisciplineSummary.class);
            return resp.getBody();
        } catch (HttpClientErrorException.NotFound notFound) {
            throw new DisciplineNotFoundException("Disciplina não encontrada para código '" + codigoNorm + "'.");
        }
    }

    private static String stripTrailingSlash(String base) {
        if (base == null) {
            return "";
        }
        return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }

    private static String normalizeCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("O campo 'codigoDisciplina' é obrigatório.");
        }
        return codigo.trim().toUpperCase(Locale.ROOT);
    }

    private static String normalizeHorario(String horario) {
        if (horario == null || horario.isBlank()) {
            throw new IllegalArgumentException("O campo 'horario' é obrigatório.");
        }
        String hNorm = horario.trim().toUpperCase(Locale.ROOT);
        if (!hNorm.matches("^[A-G]$")) {
            throw new IllegalArgumentException("Horário inválido '" + horario + "'. Use apenas A-G.");
        }
        return hNorm;
    }
}

