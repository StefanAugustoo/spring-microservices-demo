package br.pucrs.microdemo.pecas.service;

import br.pucrs.microdemo.pecas.domain.Peca;
import br.pucrs.microdemo.pecas.dto.PecaRequest;
import br.pucrs.microdemo.pecas.dto.PecaResponse;
import br.pucrs.microdemo.pecas.repository.PecaRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PecaService {

    private final PecaRepository repository;

    public PecaService(PecaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PecaResponse cadastrar(PecaRequest request) {
        if (repository.findByNumeroIdentificacao(request.getNumeroIdentificacao()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Número de identificação já cadastrado");
        }
        Peca p = new Peca();
        p.setNumeroIdentificacao(request.getNumeroIdentificacao().trim());
        p.setNome(request.getNome().trim());
        p.setDescricao(request.getDescricao() != null ? request.getDescricao().trim() : null);
        try {
            return PecaResponse.from(repository.save(p));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Número de identificação já cadastrado");
        }
    }

    @Transactional(readOnly = true)
    public PecaResponse buscarPorId(Long id) {
        return repository.findById(id).map(PecaResponse::from).orElseThrow(() -> notFound(id));
    }

    @Transactional(readOnly = true)
    public List<PecaResponse> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parâmetro nome é obrigatório");
        }
        return repository.findByNomeContainingIgnoreCase(nome.trim()).stream().map(PecaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PecaResponse> listarTodas() {
        return repository.findAll().stream().map(PecaResponse::from).toList();
    }

    private static ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Peça não encontrada: " + id);
    }
}
