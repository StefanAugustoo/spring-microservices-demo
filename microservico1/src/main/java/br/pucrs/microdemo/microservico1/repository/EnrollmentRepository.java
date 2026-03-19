package br.pucrs.microdemo.microservico1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.pucrs.microdemo.microservico1.domain.Enrollment;
import br.pucrs.microdemo.microservico1.domain.Student;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudentAndDisciplinaCodigo(Student student, String disciplinaCodigo);

    boolean existsByStudentAndDisciplinaCodigo(Student student, String disciplinaCodigo);
}

