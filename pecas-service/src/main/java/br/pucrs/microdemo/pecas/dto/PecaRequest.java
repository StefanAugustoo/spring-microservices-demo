package br.pucrs.microdemo.pecas.dto;

import jakarta.validation.constraints.NotBlank;

public class PecaRequest {

    @NotBlank
    private String numeroIdentificacao;

    @NotBlank
    private String nome;

    private String descricao;

    public String getNumeroIdentificacao() {
        return numeroIdentificacao;
    }

    public void setNumeroIdentificacao(String numeroIdentificacao) {
        this.numeroIdentificacao = numeroIdentificacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
