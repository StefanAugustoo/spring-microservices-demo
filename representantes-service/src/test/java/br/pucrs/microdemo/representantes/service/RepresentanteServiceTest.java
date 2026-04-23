package br.pucrs.microdemo.representantes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.pucrs.microdemo.representantes.domain.Representante;
import br.pucrs.microdemo.representantes.dto.RepresentanteRequest;
import br.pucrs.microdemo.representantes.repository.RepresentanteRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class RepresentanteServiceTest {

    @Mock
    private RepresentanteRepository repository;

    @InjectMocks
    private RepresentanteService service;

    @Test
    void cadastrar_sucesso() {
        RepresentanteRequest req = new RepresentanteRequest();
        req.setCpf("111.444.777-35");
        req.setNome("  Rep  ");
        when(repository.findByCpf("11144477735")).thenReturn(Optional.empty());
        when(repository.save(any(Representante.class)))
                .thenAnswer(inv -> {
                    Representante r = inv.getArgument(0);
                    r.setId(7L);
                    return r;
                });
        var r = service.cadastrar(req);
        assertThat(r.getId()).isEqualTo(7L);
        assertThat(r.getNome()).isEqualTo("Rep");
    }

    @Test
    void cadastrar_cpfInvalido() {
        RepresentanteRequest req = new RepresentanteRequest();
        req.setCpf("   ");
        req.setNome("A");
        assertThatThrownBy(() -> service.cadastrar(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(
                        ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void cadastrar_conflito() {
        RepresentanteRequest req = new RepresentanteRequest();
        req.setCpf("12345678901");
        req.setNome("A");
        when(repository.findByCpf("12345678901")).thenReturn(Optional.of(new Representante()));
        assertThatThrownBy(() -> service.cadastrar(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(
                        ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void cadastrar_dataIntegrity() {
        RepresentanteRequest req = new RepresentanteRequest();
        req.setCpf("12345678901");
        req.setNome("A");
        when(repository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(repository.save(any(Representante.class)))
                .thenThrow(new DataIntegrityViolationException("x"));
        assertThatThrownBy(() -> service.cadastrar(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(
                        ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void buscarPorId_naoEncontrado() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorId(1L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(
                        ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void buscarPorCpf() {
        Representante r = new Representante();
        r.setId(1L);
        r.setCpf("99999999999");
        r.setNome("R");
        when(repository.findByCpf("99999999999")).thenReturn(Optional.of(r));
        assertThat(service.buscarPorCpf("999.999.999-99").getNome()).isEqualTo("R");
    }

    @Test
    void buscarPorNome() {
        Representante r = new Representante();
        r.setId(1L);
        r.setCpf("1");
        r.setNome("Paulo");
        when(repository.findByNomeContainingIgnoreCase("aul")).thenReturn(List.of(r));
        assertThat(service.buscarPorNome("aul")).hasSize(1);
    }

    @Test
    void listar() {
        when(repository.findAll()).thenReturn(List.of());
        assertThat(service.listarTodos()).isEmpty();
    }
}
