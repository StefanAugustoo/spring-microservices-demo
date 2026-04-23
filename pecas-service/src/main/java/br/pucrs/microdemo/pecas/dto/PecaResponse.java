package br.pucrs.microdemo.pecas.dto;

import br.pucrs.microdemo.pecas.domain.Peca;

public class PecaResponse {

    private Long id;
    private String numeroIdentificacao;
    private String nome;
    private String descricao;

    public static PecaResponse from(Peca p) {
        PecaResponse r = new PecaResponse();
        r.id = p.getId();
        r.numeroIdentificacao = p.getNumeroIdentificacao();
        r.nome = p.getNome();
        r.descricao = p.getDescricao();
        return r;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
