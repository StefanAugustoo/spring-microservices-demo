package br.pucrs.microdemo.clientes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.pucrs.microdemo.clientes.dto.ClienteRequest;
import br.pucrs.microdemo.clientes.dto.ClienteResponse;
import br.pucrs.microdemo.clientes.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService service;

    @Test
    void post_cadastrar() throws Exception {
        ClienteResponse res = new ClienteResponse();
        res.setId(1L);
        res.setCpf("111");
        res.setNome("A");
        when(service.cadastrar(any(ClienteRequest.class))).thenReturn(res);

        ClienteRequest body = new ClienteRequest();
        body.setCpf("11111111111");
        body.setNome("A");

        mockMvc.perform(
                        post("/api/clientes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void get_listar() throws Exception {
        when(service.listarTodos()).thenReturn(List.of());
        mockMvc.perform(get("/api/clientes")).andExpect(status().isOk());
        verify(service).listarTodos();
        verify(service, never()).buscarPorNome(anyString());
    }

    @Test
    void get_porNome() throws Exception {
        when(service.buscarPorNome("x")).thenReturn(List.of());
        mockMvc.perform(get("/api/clientes").param("nome", "x")).andExpect(status().isOk());
        verify(service).buscarPorNome("x");
    }

    @Test
    void get_porId() throws Exception {
        ClienteResponse r = new ClienteResponse();
        r.setId(3L);
        r.setCpf("1");
        r.setNome("B");
        when(service.buscarPorId(3L)).thenReturn(r);
        mockMvc.perform(get("/api/clientes/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void get_porCpf() throws Exception {
        ClienteResponse r = new ClienteResponse();
        r.setId(1L);
        r.setCpf("12345678901");
        r.setNome("C");
        when(service.buscarPorCpf("12345678901")).thenReturn(r);
        mockMvc.perform(get("/api/clientes/cpf/12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("12345678901"));
    }
}
