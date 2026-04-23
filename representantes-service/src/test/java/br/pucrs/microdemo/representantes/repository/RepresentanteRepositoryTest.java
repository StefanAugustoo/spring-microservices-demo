package br.pucrs.microdemo.representantes.repository;

import static org.assertj.core.api.Assertions.assertThat;

import br.pucrs.microdemo.representantes.domain.Representante;
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
            "spring.datasource.url=jdbc:h2:mem:representanterepo;DB_CLOSE_DELAY=-1",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.cloud.config.enabled=false",
            "eureka.client.enabled=false"
        })
class RepresentanteRepositoryTest {

    @Autowired
    private RepresentanteRepository repository;

    @Test
    void findByCpf() {
        Representante r = new Representante();
        r.setCpf("12345678901");
        r.setNome("Rep 1");
        repository.save(r);
        assertThat(repository.findByCpf("12345678901")).isPresent();
    }

    @Test
    void findByNomeContainingIgnoreCase() {
        Representante a = new Representante();
        a.setCpf("11111111111");
        a.setNome("Carla Mendes");
        repository.save(a);
        Representante b = new Representante();
        b.setCpf("22222222222");
        b.setNome("Davi");
        repository.save(b);
        List<Representante> found = repository.findByNomeContainingIgnoreCase("mend");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getCpf()).isEqualTo("11111111111");
    }
}
