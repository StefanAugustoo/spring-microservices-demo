package br.pucrs.microdemo.pecas;

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

/**
 * Integração: contexto Spring completo, persistência H2, sem Config/Eureka.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.config.name=pecas-service-test")
class PecasHttpIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void post_e_get_porId() throws Exception {
        String num = "INT-" + System.currentTimeMillis();
        MvcResult created =
                mockMvc.perform(
                                post("/api/pecas")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(
                                                        Map.of(
                                                                "numeroIdentificacao",
                                                                num,
                                                                "nome",
                                                                "Peca IT",
                                                                "descricao",
                                                                "d"))))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.numeroIdentificacao").value(num))
                        .andReturn();

        JsonNode root = objectMapper.readTree(created.getResponse().getContentAsString());
        long id = root.get("id").asLong();

        mockMvc.perform(get("/api/pecas/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Peca IT"));
    }
}
