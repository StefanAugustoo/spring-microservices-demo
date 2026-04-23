package br.pucrs.microdemo.representantes;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.config.name=representantes-service-test")
class RepresentantesHttpIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void post_e_busca() throws Exception {
        String cpf = String.format("%011d", System.currentTimeMillis() % 100_000_000_000L);
        MvcResult created =
                mockMvc.perform(
                                post("/api/representantes")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        Map.of("cpf", cpf, "nome", "Rep IT"))))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.cpf").value(cpf))
                        .andReturn();
        long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(get("/api/representantes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Rep IT"));
        String byCpf =
                mockMvc.perform(get("/api/representantes/cpf/{c}", cpf))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
        assertThat(objectMapper.readTree(byCpf).get("id").asLong()).isEqualTo(id);
    }
}
