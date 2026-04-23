package br.pucrs.microdemo.representantes.dto;

import br.pucrs.microdemo.representantes.domain.Representante;

public class RepresentanteResponse {

    private Long id;
    private String cpf;
    private String nome;

    public static RepresentanteResponse from(Representante r) {
        RepresentanteResponse x = new RepresentanteResponse();
        x.id = r.getId();
        x.cpf = r.getCpf();
        x.nome = r.getNome();
        return x;
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
