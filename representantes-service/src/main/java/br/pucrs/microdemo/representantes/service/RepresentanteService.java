package br.pucrs.microdemo.representantes.service;

import br.pucrs.microdemo.representantes.domain.Representante;
import br.pucrs.microdemo.representantes.dto.RepresentanteRequest;
import br.pucrs.microdemo.representantes.dto.RepresentanteResponse;
import br.pucrs.microdemo.representantes.repository.RepresentanteRepository;
import br.pucrs.microdemo.representantes.util.CpfUtil;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RepresentanteService {

    private final RepresentanteRepository repository;

    public RepresentanteService(RepresentanteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public RepresentanteResponse cadastrar(RepresentanteRequest request) {
        String cpf = CpfUtil.normalizar(request.getCpf());
        if (cpf.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido");
        }
        if (repository.findByCpf(cpf).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
        Representante r = new Representante();
        r.setCpf(cpf);
        r.setNome(request.getNome().trim());
        try {
            return RepresentanteResponse.from(repository.save(r));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
    }

    @Transactional(readOnly = true)
    public RepresentanteResponse buscarPorId(Long id) {
        return repository.findById(id).map(RepresentanteResponse::from).orElseThrow(() -> notFoundId(id));
    }

    @Transactional(readOnly = true)
    public RepresentanteResponse buscarPorCpf(String cpfBruto) {
        String cpf = CpfUtil.normalizar(cpfBruto);
        if (cpf.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido");
        }
        return repository.findByCpf(cpf).map(RepresentanteResponse::from).orElseThrow(() -> notFoundCpf());
    }

    @Transactional(readOnly = true)
    public List<RepresentanteResponse> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetro nome é obrigatório");
        }
        return repository.findByNomeContainingIgnoreCase(nome.trim()).stream()
                .map(RepresentanteResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RepresentanteResponse> listarTodos() {
        return repository.findAll().stream().map(RepresentanteResponse::from).toList();
    }

    private static ResponseStatusException notFoundId(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Representante não encontrado: " + id);
    }

    private static ResponseStatusException notFoundCpf() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Representante não encontrado para o CPF informado");
    }
}
