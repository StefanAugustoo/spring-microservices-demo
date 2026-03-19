package br.pucrs.microdemo.microservico1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.pucrs.microdemo.microservico1.domain.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByMatricula(Long matricula);

    List<Student> findByNomeContainingIgnoreCase(String nomeParcial);
}

