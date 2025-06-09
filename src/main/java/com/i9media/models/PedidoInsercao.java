package com.i9media.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private String midia;
    private BigDecimal liquidoFinal;
    private int piAgencia;
    private Date vencimentopiAgencia;
    private Date checkingEnviado;
    private BigDecimal piI9;
    private Date dataPagamentoParaVeiculo;
    private String nfVeiculo;
    
    public BigDecimal CalcularBVAgencia(String bv) {
    	BigDecimal resultado;
    	BigDecimal valor = valorLiquido;
    	BigDecimal valor_bv = new BigDecimal(bv);
    	resultado = valor.multiply(valor_bv).setScale(2, RoundingMode.HALF_UP);
    	
    	return resultado;
    }
    
    public BigDecimal CalcularTotalLiquido() {
    	BigDecimal resultado;
    	BigDecimal valor_liquido = valorLiquido;
    	BigDecimal valor_repasse = repasseVeiculo;
    	BigDecimal valor_comissao = valorComissao;
    	BigDecimal valor_bvagencia = bvAgencia;
    	BigDecimal valor_imposto = imposto;
    	
    	resultado = valor_liquido
    	        .subtract(valor_repasse)
    	        .subtract(valor_bvagencia)
    	        .subtract(valor_imposto)
    	        .add(valor_comissao);
    	resultado = resultado.setScale(2, RoundingMode.HALF_UP);
    	
    	return resultado;
    }
    
    public BigDecimal CalcularComissao() {
    	BigDecimal resultado;
    	BigDecimal valor = repasseVeiculo;
    	BigDecimal comissao = new BigDecimal("0.10");
    	resultado = valor.multiply(comissao).setScale(2, RoundingMode.HALF_UP);
    	
    	return resultado;
    }
    
    public BigDecimal CalcularImposto(String porc_imposto) {
        BigDecimal valor = valorLiquido;
        BigDecimal percentual = new BigDecimal(porc_imposto);
        BigDecimal impostoDecimal = percentual.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
        BigDecimal resultado = valor.multiply(impostoDecimal).setScale(2, RoundingMode.HALF_UP);

        return resultado;
    }
    
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

    public String getMidia() {
        return midia;
    }

    public void setMidia(String midia) {
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
