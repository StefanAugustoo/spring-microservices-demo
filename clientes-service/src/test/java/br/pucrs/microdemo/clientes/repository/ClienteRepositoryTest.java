package br.pucrs.microdemo.clientes.repository;

import static org.assertj.core.api.Assertions.assertThat;

import br.pucrs.microdemo.clientes.domain.Cliente;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
@TestPropertySource(
        properties = {
            "spring.datasource.url=jdbc:h2:mem:clienterepo;DB_CLOSE_DELAY=-1",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.cloud.config.enabled=false",
            "eureka.client.enabled=false"
        })
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository repository;

    @Test
    void findByCpf() {
        Cliente c = new Cliente();
        c.setCpf("12345678901");
        c.setNome("João");
        repository.save(c);
        assertThat(repository.findByCpf("12345678901")).isPresent();
    }

    @Test
    void findByNomeContainingIgnoreCase() {
        Cliente c1 = new Cliente();
        c1.setCpf("11111111111");
        c1.setNome("Ana Souza");
        repository.save(c1);
        Cliente c2 = new Cliente();
        c2.setCpf("22222222222");
        c2.setNome("Bruno");
        repository.save(c2);
        List<Cliente> a = repository.findByNomeContainingIgnoreCase("souz");
        assertThat(a).hasSize(1);
        assertThat(a.get(0).getNome()).isEqualTo("Ana Souza");
    }
}
