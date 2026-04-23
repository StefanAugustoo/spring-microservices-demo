package br.pucrs.microdemo.representantes.repository;

import br.pucrs.microdemo.representantes.domain.Representante;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepresentanteRepository extends JpaRepository<Representante, Long> {

    Optional<Representante> findByCpf(String cpf);

    List<Representante> findByNomeContainingIgnoreCase(String nome);
}
