package br.pucrs.microdemo.representantes.controller;

import br.pucrs.microdemo.representantes.dto.RepresentanteRequest;
import br.pucrs.microdemo.representantes.dto.RepresentanteResponse;
import br.pucrs.microdemo.representantes.service.RepresentanteService;
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
@RequestMapping("/api/representantes")
public class RepresentanteController {

    private final RepresentanteService service;

    public RepresentanteController(RepresentanteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RepresentanteResponse cadastrar(@Valid @RequestBody RepresentanteRequest request) {
        return service.cadastrar(request);
    }

    @GetMapping
    public List<RepresentanteResponse> listar(@RequestParam(required = false) String nome) {
        if (nome != null && !nome.isBlank()) {
            return service.buscarPorNome(nome);
        }
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public RepresentanteResponse porId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/cpf/{cpf}")
    public RepresentanteResponse porCpf(@PathVariable String cpf) {
        return service.buscarPorCpf(cpf);
    }
}
