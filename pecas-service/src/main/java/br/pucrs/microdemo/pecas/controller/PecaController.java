package br.pucrs.microdemo.pecas.controller;

import br.pucrs.microdemo.pecas.dto.PecaRequest;
import br.pucrs.microdemo.pecas.dto.PecaResponse;
import br.pucrs.microdemo.pecas.service.PecaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pecas")
public class PecaController {

    private final PecaService service;

    public PecaController(PecaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PecaResponse cadastrar(@Valid @RequestBody PecaRequest request) {
        return service.cadastrar(request);
    }

    @GetMapping
    public List<PecaResponse> listar(@RequestParam(required = false) String nome) {
        if (nome != null && !nome.isBlank()) {
            return service.buscarPorNome(nome);
        }
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public PecaResponse porId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}
