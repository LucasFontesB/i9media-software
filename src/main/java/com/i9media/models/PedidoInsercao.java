package com.i9media.models;

import java.math.BigDecimal;
import java.sql.Date;

public class PedidoInsercao {
	private String cliente;
    private String agencia;
    private String executivo;
    private String veiculo;
    private BigDecimal valorLiquido;
    private BigDecimal repasseVeiculo;
    private BigDecimal imposto;
    private BigDecimal bvAgencia;
    private BigDecimal comissaoPercentual;
    private BigDecimal valorComissao;
    private BigDecimal totalLiquido;
    private String midiaResponsavel;
    private int percentualIndicacao;
    private BigDecimal midia;
    private BigDecimal liquidoFinal;
    private int piAgencia;
    private Date vencimentopiAgencia;
    private Date checkingEnviado;
    private BigDecimal piI9;
    private Date dataPagamentoParaVeiculo;
    private String nfVeiculo;
    
    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getExecutivo() {
        return executivo;
    }

    public void setExecutivo(String executivo) {
        this.executivo = executivo;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public BigDecimal getRepasseVeiculo() {
        return repasseVeiculo;
    }

    public void setRepasseVeiculo(BigDecimal repasseVeiculo) {
        this.repasseVeiculo = repasseVeiculo;
    }

    public BigDecimal getImposto() {
        return imposto;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getBvAgencia() {
        return bvAgencia;
    }

    public void setBvAgencia(BigDecimal bvAgencia) {
        this.bvAgencia = bvAgencia;
    }

    public BigDecimal getComissaoPercentual() {
        return comissaoPercentual;
    }

    public void setComissaoPercentual(BigDecimal comissaoPercentual) {
        this.comissaoPercentual = comissaoPercentual;
    }

    public BigDecimal getValorComissao() {
        return valorComissao;
    }

    public void setValorComissao(BigDecimal valorComissao) {
        this.valorComissao = valorComissao;
    }

    public BigDecimal getTotalLiquido() {
        return totalLiquido;
    }

    public void setTotalLiquido(BigDecimal totalLiquido) {
        this.totalLiquido = totalLiquido;
    }

    public String getMidiaResponsavel() {
        return midiaResponsavel;
    }

    public void setMidiaResponsavel(String midiaResponsavel) {
        this.midiaResponsavel = midiaResponsavel;
    }

    public int getPercentualIndicacao() {
        return percentualIndicacao;
    }

    public void setPercentualIndicacao(int percentualIndicacao) {
        this.percentualIndicacao = percentualIndicacao;
    }

    public BigDecimal getMidia() {
        return midia;
    }

    public void setMidia(BigDecimal midia) {
        this.midia = midia;
    }

    public BigDecimal getLiquidoFinal() {
        return liquidoFinal;
    }

    public void setLiquidoFinal(BigDecimal liquidoFinal) {
        this.liquidoFinal = liquidoFinal;
    }
    
    public int getPiAgencia() {
        return piAgencia;
    }

    public void setPiAgencia(int piAgencia) {
        this.piAgencia = piAgencia;
    }

    public Date getVencimentopiAgencia() {
        return vencimentopiAgencia;
    }

    public void setVencimentopiAgencia(Date vencimentopiAgencia) {
        this.vencimentopiAgencia = vencimentopiAgencia;
    }

    public Date getCheckingEnviado() {
        return checkingEnviado;
    }

    public void setCheckingEnviado(Date checkingEnviado) {
        this.checkingEnviado = checkingEnviado;
    }

    public BigDecimal getPiI9() {
        return piI9;
    }

    public void setPiI9(BigDecimal piI9) {
        this.piI9 = piI9;
    }

    public Date getDataPagamentoParaVeiculo() {
        return dataPagamentoParaVeiculo;
    }

    public void setDataPagamentoParaVeiculo(Date dataPagamentoParaVeiculo) {
        this.dataPagamentoParaVeiculo = dataPagamentoParaVeiculo;
    }

    public String getNfVeiculo() {
        return nfVeiculo;
    }

    public void setNfVeiculo(String nfVeiculo) {
        this.nfVeiculo = nfVeiculo;
    }
}
