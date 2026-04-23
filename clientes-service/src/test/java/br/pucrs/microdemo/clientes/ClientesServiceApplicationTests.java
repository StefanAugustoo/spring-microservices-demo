package br.pucrs.microdemo.clientes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.config.name=clientes-service-test")
class ClientesServiceApplicationTests {

    @Test
    void contextLoads() {}
}
