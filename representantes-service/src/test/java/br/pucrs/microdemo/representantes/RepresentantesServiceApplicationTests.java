package br.pucrs.microdemo.representantes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.config.name=representantes-service-test")
class RepresentantesServiceApplicationTests {

    @Test
    void contextLoads() {}
}
