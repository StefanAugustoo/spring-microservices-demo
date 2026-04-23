package br.pucrs.microdemo.clientes.dto;

import br.pucrs.microdemo.clientes.domain.Cliente;

public class ClienteResponse {

    private Long id;
    private String cpf;
    private String nome;

    public static ClienteResponse from(Cliente c) {
        ClienteResponse r = new ClienteResponse();
        r.id = c.getId();
        r.cpf = c.getCpf();
        r.nome = c.getNome();
        return r;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
