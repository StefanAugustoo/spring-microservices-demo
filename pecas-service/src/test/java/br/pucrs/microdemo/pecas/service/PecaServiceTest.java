package br.pucrs.microdemo.pecas.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.pucrs.microdemo.pecas.domain.Peca;
import br.pucrs.microdemo.pecas.dto.PecaRequest;
import br.pucrs.microdemo.pecas.repository.PecaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class PecaServiceTest {

    @Mock
    private PecaRepository repository;

    @InjectMocks
    private PecaService service;

    private PecaRequest request;

    @BeforeEach
    void setUp() {
        request = new PecaRequest();
        request.setNumeroIdentificacao("N-1");
        request.setNome("  Parafuso  ");
        request.setDescricao(" aço ");
    }

    @Test
    void cadastrar_sucesso() {
        when(repository.findByNumeroIdentificacao("N-1")).thenReturn(Optional.empty());
        when(repository.save(any(Peca.class))).thenAnswer(i -> {
            Peca p = i.getArgument(0);
            p.setId(10L);
            return p;
        });

        var res = service.cadastrar(request);

        assertThat(res.getId()).isEqualTo(10L);
        assertThat(res.getNumeroIdentificacao()).isEqualTo("N-1");
        assertThat(res.getNome()).isEqualTo("Parafuso");
        assertThat(res.getDescricao()).isEqualTo("aço");
        verify(repository).save(any(Peca.class));
    }

    @Test
    void cadastrar_conflito_quandoNumeroExiste() {
        Peca existente = new Peca();
        when(repository.findByNumeroIdentificacao("N-1")).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> service.cadastrar(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void cadastrar_conflito_quandoSaveLancaDataIntegrity() {
        when(repository.findByNumeroIdentificacao("N-1")).thenReturn(Optional.empty());
        when(repository.save(any(Peca.class))).thenThrow(new DataIntegrityViolationException("dup"));

        assertThatThrownBy(() -> service.cadastrar(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void buscarPorId_encontrada() {
        Peca p = new Peca();
        p.setId(1L);
        p.setNumeroIdentificacao("X");
        p.setNome("Y");
        when(repository.findById(1L)).thenReturn(Optional.of(p));

        var r = service.buscarPorId(1L);
        assertThat(r.getId()).isEqualTo(1L);
        assertThat(r.getNome()).isEqualTo("Y");
    }

    @Test
    void buscarPorId_naoEncontrada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void buscarPorNome_sucesso() {
        Peca p = new Peca();
        p.setId(1L);
        p.setNumeroIdentificacao("A");
        p.setNome("Parafuso M10");
        when(repository.findByNomeContainingIgnoreCase("parafuso")).thenReturn(List.of(p));

        var list = service.buscarPorNome(" parafuso ");
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getNome()).isEqualTo("Parafuso M10");
    }

    @Test
    void buscarPorNome_nomeVazioLanca400() {
        assertThatThrownBy(() -> service.buscarPorNome("  "))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void buscarPorNome_nuloLanca400() {
        assertThatThrownBy(() -> service.buscarPorNome(null))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void listarTodas() {
        when(repository.findAll()).thenReturn(List.of());
        assertThat(service.listarTodas()).isEmpty();
    }
}
