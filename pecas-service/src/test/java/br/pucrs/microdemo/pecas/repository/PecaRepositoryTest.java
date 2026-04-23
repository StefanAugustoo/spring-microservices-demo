package br.pucrs.microdemo.pecas.repository;

import static org.assertj.core.api.Assertions.assertThat;

import br.pucrs.microdemo.pecas.domain.Peca;
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
            "spring.datasource.url=jdbc:h2:mem:pecarepo;DB_CLOSE_DELAY=-1",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.cloud.config.enabled=false",
            "eureka.client.enabled=false"
        })
class PecaRepositoryTest {

    @Autowired
    private PecaRepository repository;

    @Test
    void save_e_findByNumeroIdentificacao() {
        Peca p = new Peca();
        p.setNumeroIdentificacao("K-9");
        p.setNome("Engrenagem");
        p.setDescricao("d");
        repository.save(p);
        assertThat(p.getId()).isNotNull();

        assertThat(repository.findByNumeroIdentificacao("K-9")).isPresent();
        assertThat(repository.findByNumeroIdentificacao("K-9").get().getNome()).isEqualTo("Engrenagem");
    }

    @Test
    void findByNomeContainingIgnoreCase() {
        Peca a = new Peca();
        a.setNumeroIdentificacao("A1");
        a.setNome("Parafuso longo");
        repository.save(a);
        Peca b = new Peca();
        b.setNumeroIdentificacao("A2");
        b.setNome("Porca");
        repository.save(b);

        List<Peca> r = repository.findByNomeContainingIgnoreCase("parafu");
        assertThat(r).hasSize(1);
        assertThat(r.get(0).getNumeroIdentificacao()).isEqualTo("A1");
    }
}
