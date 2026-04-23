package br.pucrs.microdemo.clientes.repository;

import br.pucrs.microdemo.clientes.domain.Cliente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

    List<Cliente> findByNomeContainingIgnoreCase(String nome);
}
