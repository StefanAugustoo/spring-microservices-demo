package br.pucrs.microdemo.microservico1.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "disciplina_codigo"})
)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(nullable = false)
    private String disciplinaCodigo;

    @Column(nullable = false)
    private String disciplinaNome;

    @Column(nullable = false)
    private String horario; // A-G

    public Enrollment(Student student, String disciplinaCodigo, String disciplinaNome, String horario) {
        this.student = student;
        this.disciplinaCodigo = disciplinaCodigo;
        this.disciplinaNome = disciplinaNome;
        this.horario = horario;
    }
}

