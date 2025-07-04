package com.i9media.models;

public class ComissaoEspecialDTO {
    private String nome;
    private double percentual;
    private double valor;

    public ComissaoEspecialDTO(String nome, double percentual, double valor) {
        this.nome = nome;
        this.percentual = percentual;
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public double getPercentual() {
        return percentual;
    }

    public double getValor() {
        return valor;
    }
}