package br.pucrs.microdemo.clientes.controller;

import br.pucrs.microdemo.clientes.dto.ClienteRequest;
import br.pucrs.microdemo.clientes.dto.ClienteResponse;
import br.pucrs.microdemo.clientes.service.ClienteService;
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
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse cadastrar(@Valid @RequestBody ClienteRequest request) {
        return service.cadastrar(request);
    }

    @GetMapping
    public List<ClienteResponse> listar(@RequestParam(required = false) String nome) {
        if (nome != null && !nome.isBlank()) {
            return service.buscarPorNome(nome);
        }
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ClienteResponse porId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/cpf/{cpf}")
    public ClienteResponse porCpf(@PathVariable String cpf) {
        return service.buscarPorCpf(cpf);
    }
}
