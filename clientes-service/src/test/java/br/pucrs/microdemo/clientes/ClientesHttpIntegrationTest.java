package br.pucrs.microdemo.clientes;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
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
@TestPropertySource(properties = "spring.config.name=clientes-service-test")
class ClientesHttpIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void post_e_get_porCpf() throws Exception {
        String cpf = String.format("%011d", System.currentTimeMillis() % 100_000_000_000L);

        MvcResult created =
                mockMvc.perform(
                                post("/api/clientes")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        Map.of("cpf", cpf, "nome", "Integration Cliente"))))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.cpf").value(cpf))
                        .andReturn();

        JsonNode node = objectMapper.readTree(created.getResponse().getContentAsString());
        long id = node.get("id").asLong();

        mockMvc.perform(get("/api/clientes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Integration Cliente"));

        mockMvc.perform(get("/api/clientes/cpf/{cpf}", cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }
}
