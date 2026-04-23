package br.pucrs.microdemo.clientes.service;

import br.pucrs.microdemo.clientes.domain.Cliente;
import br.pucrs.microdemo.clientes.dto.ClienteRequest;
import br.pucrs.microdemo.clientes.dto.ClienteResponse;
import br.pucrs.microdemo.clientes.repository.ClienteRepository;
import br.pucrs.microdemo.clientes.util.CpfUtil;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ClienteResponse cadastrar(ClienteRequest request) {
        String cpf = CpfUtil.normalizar(request.getCpf());
        if (cpf.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido");
        }
        if (repository.findByCpf(cpf).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
        Cliente c = new Cliente();
        c.setCpf(cpf);
        c.setNome(request.getNome().trim());
        try {
            return ClienteResponse.from(repository.save(c));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        return repository.findById(id).map(ClienteResponse::from).orElseThrow(() -> notFoundId(id));
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorCpf(String cpfBruto) {
        String cpf = CpfUtil.normalizar(cpfBruto);
        if (cpf.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido");
        }
        return repository.findByCpf(cpf).map(ClienteResponse::from).orElseThrow(() -> notFoundCpf());
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetro nome é obrigatório");
        }
        return repository.findByNomeContainingIgnoreCase(nome.trim()).stream().map(ClienteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listarTodos() {
        return repository.findAll().stream().map(ClienteResponse::from).toList();
    }

    private static ResponseStatusException notFoundId(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado: " + id);
    }

    private static ResponseStatusException notFoundCpf() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado para o CPF informado");
    }
}
