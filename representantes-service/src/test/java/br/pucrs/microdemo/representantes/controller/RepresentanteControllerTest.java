package br.pucrs.microdemo.representantes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.pucrs.microdemo.representantes.dto.RepresentanteRequest;
import br.pucrs.microdemo.representantes.dto.RepresentanteResponse;
import br.pucrs.microdemo.representantes.service.RepresentanteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = RepresentanteController.class)
class RepresentanteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RepresentanteService service;

    @Test
    void post_cadastrar() throws Exception {
        RepresentanteResponse res = new RepresentanteResponse();
        res.setId(1L);
        res.setCpf("111");
        res.setNome("A");
        when(service.cadastrar(any(RepresentanteRequest.class))).thenReturn(res);
        RepresentanteRequest body = new RepresentanteRequest();
        body.setCpf("12345678901");
        body.setNome("A");
        mockMvc.perform(
                        post("/api/representantes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    void get_listar() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());
        mockMvc.perform(get("/api/representantes")).andExpect(status().isOk());
        verify(service, never()).buscarPorNome(anyString());
    }

    @Test
    void get_porNome() throws Exception {
        when(service.buscarPorNome("a")).thenReturn(List.of());
        mockMvc.perform(get("/api/representantes").param("nome", "a")).andExpect(status().isOk());
    }

    @Test
    void get_porCpf() throws Exception {
        RepresentanteResponse r = new RepresentanteResponse();
        r.setId(1L);
        r.setCpf("12345678901");
        r.setNome("R");
        when(service.buscarPorCpf("12345678901")).thenReturn(r);
        mockMvc.perform(get("/api/representantes/cpf/12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("R"));
    }
}
