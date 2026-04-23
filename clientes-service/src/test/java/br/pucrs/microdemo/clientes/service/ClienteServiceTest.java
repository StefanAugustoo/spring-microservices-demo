package br.pucrs.microdemo.clientes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.pucrs.microdemo.clientes.domain.Cliente;
import br.pucrs.microdemo.clientes.dto.ClienteRequest;
import br.pucrs.microdemo.clientes.repository.ClienteRepository;
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
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @Test
    void cadastrar_sucesso() {
        ClienteRequest req = new ClienteRequest();
        req.setCpf("529.982.247-25");
        req.setNome("  Maria  ");
        when(repository.findByCpf("52998224725")).thenReturn(Optional.empty());
        when(repository.save(any(Cliente.class)))
                .thenAnswer(inv -> {
                    Cliente c = inv.getArgument(0);
                    c.setId(2L);
                    return c;
                });

        var r = service.cadastrar(req);
        assertThat(r.getCpf()).isEqualTo("52998224725");
        assertThat(r.getNome()).isEqualTo("Maria");
        assertThat(r.getId()).isEqualTo(2L);
    }

    @Test
    void cadastrar_cpfInvalido_aposNormalizacao() {
        ClienteRequest req = new ClienteRequest();
        req.setCpf("  ..  ");
        req.setNome("X");
        assertThatThrownBy(() -> service.cadastrar(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(
                        ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void cadastrar_cpfEmConflito() {
        ClienteRequest req = new ClienteRequest();
        req.setCpf("12345678901");
        req.setNome("A");
        when(repository.findByCpf("12345678901")).thenReturn(Optional.of(new Cliente()));
        assertThatThrownBy(() -> service.cadastrar(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(
                        ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void cadastrar_dataIntegrity() {
        ClienteRequest req = new ClienteRequest();
        req.setCpf("12345678901");
        req.setNome("A");
        when(repository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(repository.save(any(Cliente.class))).thenThrow(new DataIntegrityViolationException("x"));
        assertThatThrownBy(() -> service.cadastrar(req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(
                        ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void buscarPorId_encontrado() {
        Cliente c = new Cliente();
        c.setId(1L);
        c.setCpf("1");
        c.setNome("N");
        when(repository.findById(1L)).thenReturn(Optional.of(c));
        assertThat(service.buscarPorId(1L).getNome()).isEqualTo("N");
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
        Cliente c = new Cliente();
        c.setId(1L);
        c.setCpf("11111111111");
        c.setNome("A");
        when(repository.findByCpf("11111111111")).thenReturn(Optional.of(c));
        assertThat(service.buscarPorCpf("111.111.111-11").getNome()).isEqualTo("A");
    }

    @Test
    void buscarPorCpf_cpfVazio() {
        assertThatThrownBy(() -> service.buscarPorCpf("  "))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(
                        ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void buscarPorCpf_naoEncontrado() {
        when(repository.findByCpf("00000000000")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorCpf("00000000000"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(
                        ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void buscarPorNome() {
        Cliente c = new Cliente();
        c.setId(1L);
        c.setCpf("1");
        c.setNome("Ana");
        when(repository.findByNomeContainingIgnoreCase("an")).thenReturn(List.of(c));
        assertThat(service.buscarPorNome(" an ")).hasSize(1);
    }

    @Test
    void buscarPorNome_invalido() {
        assertThatThrownBy(() -> service.buscarPorNome("  "))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(
                        ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void listarTodos() {
        when(repository.findAll()).thenReturn(List.of());
        assertThat(service.listarTodos()).isEmpty();
    }
}
