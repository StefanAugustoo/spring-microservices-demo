package br.pucrs.microdemo.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.config.name=api-gateway-test")
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {}
}
