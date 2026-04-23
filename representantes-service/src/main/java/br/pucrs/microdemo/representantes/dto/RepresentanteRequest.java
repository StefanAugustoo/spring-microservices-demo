package br.pucrs.microdemo.representantes.dto;

import jakarta.validation.constraints.NotBlank;

public class RepresentanteRequest {

    @NotBlank
    private String cpf;

    @NotBlank
    private String nome;

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
