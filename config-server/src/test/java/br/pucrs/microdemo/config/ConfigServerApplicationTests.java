package br.pucrs.microdemo.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.cloud.config.server.native.search-locations=classpath:/config/")
class ConfigServerApplicationTests {

    @Test
    void contextLoads() {}
}
