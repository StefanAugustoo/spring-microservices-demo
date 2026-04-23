package br.pucrs.microdemo.pecas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.config.name=pecas-service-test")
class PecasServiceApplicationTests {

    @Test
    void contextLoads() {}
}
