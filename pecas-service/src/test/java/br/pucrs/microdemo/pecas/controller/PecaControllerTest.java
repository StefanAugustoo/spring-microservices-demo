package br.pucrs.microdemo.pecas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.pucrs.microdemo.pecas.dto.PecaRequest;
import br.pucrs.microdemo.pecas.dto.PecaResponse;
import br.pucrs.microdemo.pecas.service.PecaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PecaController.class)
class PecaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PecaService service;

    @Test
    void post_cadastrar() throws Exception {
        PecaResponse res = new PecaResponse();
        res.setId(1L);
        res.setNumeroIdentificacao("N-1");
        res.setNome("Peça A");
        res.setDescricao("d");
        when(service.cadastrar(any(PecaRequest.class))).thenReturn(res);

        PecaRequest body = new PecaRequest();
        body.setNumeroIdentificacao("N-1");
        body.setNome("Peça A");
        body.setDescricao("d");

        mockMvc.perform(
                        post("/api/pecas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Peça A"));
    }

    @Test
    void post_validacao_corpoVazio() throws Exception {
        mockMvc.perform(post("/api/pecas").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        verify(service, never()).cadastrar(any());
    }

    @Test
    void get_listarSemFiltro() throws Exception {
        when(service.listarTodas()).thenReturn(List.of());
        mockMvc.perform(get("/api/pecas")).andExpect(status().isOk());
        verify(service).listarTodas();
        verify(service, never()).buscarPorNome(anyString());
    }

    @Test
    void get_listarComNome() throws Exception {
        when(service.buscarPorNome("ab")).thenReturn(List.of());
        mockMvc.perform(get("/api/pecas").param("nome", "ab")).andExpect(status().isOk());
        verify(service).buscarPorNome("ab");
    }

    @Test
    void get_porId() throws Exception {
        PecaResponse r = new PecaResponse();
        r.setId(5L);
        r.setNumeroIdentificacao("X");
        r.setNome("N");
        when(service.buscarPorId(5L)).thenReturn(r);

        mockMvc.perform(get("/api/pecas/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }
}
