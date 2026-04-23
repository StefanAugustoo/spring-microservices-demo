package br.pucrs.microdemo.eureka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.config.name=eureka-server-test")
class EurekaServerApplicationTests {

    @Test
    void contextLoads() {}
}
