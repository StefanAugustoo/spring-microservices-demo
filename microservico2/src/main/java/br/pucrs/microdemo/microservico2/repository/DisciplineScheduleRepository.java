package br.pucrs.microdemo.microservico2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.pucrs.microdemo.microservico2.domain.DisciplineSchedule;

@Repository
public interface DisciplineScheduleRepository extends JpaRepository<DisciplineSchedule, Long> {

    List<DisciplineSchedule> findAllByCodigo(String codigo);

    boolean existsByCodigoAndHorario(String codigo, String horario);
}

