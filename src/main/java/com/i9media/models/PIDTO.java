package com.i9media.models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

import com.i9media.Conectar;

public class PIDTO {

    private Integer id;

    private Integer clienteId;
    private String nomeCliente;
    private Integer agenciaId;
    private String nomeAgencia;
    private Integer executivoId;
    private String nomeExecutivo;

    private String veiculo;
    private String praca;
    private BigDecimal valorLiquido;
    private BigDecimal repasseVeiculo;
    private BigDecimal imposto;
    private BigDecimal bvAgencia;
    private BigDecimal comissaoPercentual;
    private BigDecimal valorComissao;
    private BigDecimal totalLiquido;
    private String midiaResponsavel;
    private BigDecimal percentualIndicacao;
    private BigDecimal midia;
    private BigDecimal liquidoFinal;

    private BigDecimal porcImposto;
    private BigDecimal porcBV;

    private String piAgencia;
    private Date vencimentopiAgencia;
    private Date checkingEnviado;
    private String piI9Id;
    private Date dataPagamentoParaVeiculo;
    private String nfVeiculo;
    
    private String emEdicaoPor; 
    private Date edicaoInicio;
    
    private String criadoPor;
    private LocalDateTime dataCriacao;
    
    private Boolean pagoPelaAgencia;
    private Date dataConfirmadaPagamentoPelaAgencia;
    private String responsavelPagamentoAgencia;

    private Boolean pagoParaVeiculo;
    private Date dataConfirmadaPagamentoParaVeiculo;
    private String responsavelPagamentoVeiculo;
    
    public PIDTO(PIDTO other) {
        if (other != null) {
            this.id = other.getId();
            this.clienteId = other.getClienteId();
            this.agenciaId = other.getAgenciaId();
            this.executivoId = other.getExecutivoId();
            this.veiculo = other.getVeiculo();
            this.praca = other.getPraca();
            this.valorLiquido = other.getValorLiquido();
            this.repasseVeiculo = other.getRepasseVeiculo();
            this.imposto = other.getImposto();
            this.bvAgencia = other.getBvAgencia();
            this.comissaoPercentual = other.getComissaoPercentual();
            this.valorComissao = other.getValorComissao();
            this.totalLiquido = other.getTotalLiquido();
            this.midiaResponsavel = other.getMidiaResponsavel();
            this.percentualIndicacao = other.getPercentualIndicacao();
            this.midia = other.getMidia();
            this.liquidoFinal = other.getLiquidoFinal();
            this.porcImposto = other.getPorcImposto();
            this.porcBV = other.getPorcBV();
            this.piAgencia = other.getPiAgencia();
            this.vencimentopiAgencia = other.getVencimentopiAgencia();
            this.checkingEnviado = other.getCheckingEnviado();
            this.piI9Id = other.getPiI9Id();
            this.dataPagamentoParaVeiculo = other.getDataPagamentoParaVeiculo();
            this.nfVeiculo = other.getNfVeiculo();
            this.emEdicaoPor = other.getEmEdicaoPor();
            this.edicaoInicio = other.getEdicaoInicio();
            this.criadoPor = other.getCriadoPor();
            this.dataCriacao = other.getDataCriacao();

            this.pagoPelaAgencia = other.getPagoPelaAgencia();
            this.dataConfirmadaPagamentoPelaAgencia = other.getDataConfirmadaPagamentoPelaAgencia();
            this.responsavelPagamentoAgencia = other.getResponsavelPagamentoAgencia();

            this.pagoParaVeiculo = other.getPagoParaVeiculo();
            this.dataConfirmadaPagamentoParaVeiculo = other.getdataConfirmadaPagamentoParaVeiculo();
            this.responsavelPagamentoVeiculo = other.getResponsavelPagamentoVeiculo();
        }
    }
    
    public void atualizarPagamentoFinanceiro() throws SQLException {
        String sql = "UPDATE pi SET " +
                     "pago_pela_agencia = ?, " +
                     "data_pagamento_pela_agencia = ?, " +
                     "pago_para_veiculo = ?, " +
                     "data_pagamento_para_veiculo = ?, responsavel_pagamento_veiculo = ?, responsavel_pagamento_agencia = ? " +
                     "WHERE id = ?";

        try (Connection conn = Conectar.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, this.pagoPelaAgencia != null ? this.pagoPelaAgencia : false);
            stmt.setDate(2, this.dataConfirmadaPagamentoPelaAgencia != null ? new java.sql.Date(this.dataConfirmadaPagamentoPelaAgencia.getTime()) : null);

            stmt.setBoolean(3, this.pagoParaVeiculo != null ? this.pagoParaVeiculo : false);
            stmt.setDate(4, this.dataConfirmadaPagamentoParaVeiculo != null ? new java.sql.Date(this.dataConfirmadaPagamentoParaVeiculo.getTime()) : null);
            stmt.setString(5, this.responsavelPagamentoVeiculo);
            stmt.setString(6, this.responsavelPagamentoAgencia);
            
            
            stmt.setInt(7, this.id);

            stmt.executeUpdate();
        }
    }
    
    public static PIDTO convertToDTO(PedidoInsercao pi) {
        if (pi == null) {
            return null;
        }
        PIDTO dto = new PIDTO();
        dto.setId(pi.getId());

        dto.setClienteId(pi.getClienteId());
        dto.setAgenciaId(pi.getAgenciaId());
        dto.setExecutivoId(pi.getExecutivoId());

        dto.setVeiculo(pi.getVeiculo());
        dto.setPraca(pi.getPraca());
        dto.setValorLiquido(pi.getValorLiquido());
        dto.setRepasseVeiculo(pi.getRepasseVeiculo());
        dto.setImposto(pi.getImposto());
        dto.setBvAgencia(pi.getBvAgencia());
        dto.setComissaoPercentual(pi.getComissaoPercentual());
        dto.setValorComissao(pi.getValorComissao());
        dto.setTotalLiquido(pi.getTotalLiquido());
        dto.setMidiaResponsavel(pi.getMidiaResponsavel());
        dto.setPercentualIndicacao(pi.getPercentualIndicacao());
        dto.setMidia(pi.getMidia());
        dto.setLiquidoFinal(pi.getLiquidoFinal());

        dto.setPorcImposto(pi.getPorcImposto());
        dto.setPorcBV(pi.getPorcBV());

        dto.setPiAgencia(pi.getPiAgencia());
        dto.setVencimentopiAgencia(pi.getVencimentopiAgencia());
        dto.setCheckingEnviado(pi.getCheckingEnviado());
        dto.setPiI9Id(pi.getPiI9Id());
        dto.setDataPagamentoParaVeiculo(pi.getDataPagamentoParaVeiculo());
        dto.setNfVeiculo(pi.getNfVeiculo());

        dto.setEmEdicaoPor(pi.getEmEdicaoPor());
        dto.setEdicaoInicio(pi.getEdicaoInicio());

        dto.setCriadoPor(pi.getCriadoPor());
        dto.setDataCriacao(pi.getDataCriacao());

        dto.setPagoPelaAgencia(pi.getPagoPelaAgencia());
        dto.setDataConfirmadaPagamentoPelaAgencia(pi.getDataConfirmadaPagamentoPelaAgencia());
        dto.setResponsavelPagamentoAgencia(pi.getResponsavelPagamentoAgencia());
        dto.setdataConfirmadaPagamentoParaVeiculo(pi.getdataConfirmadaPagamentoParaVeiculo());

        
        dto.setPagoParaVeiculo(pi.getPagoParaVeiculo());
        dto.setResponsavelPagamentoVeiculo(pi.getResponsavelPagamentoVeiculo());

        return dto;
    }
    
    public Boolean getPagoPelaAgencia() {
        return pagoPelaAgencia;
    }

    public void setPagoPelaAgencia(Boolean pagoPelaAgencia) {
        this.pagoPelaAgencia = pagoPelaAgencia;
    }

    public Date getDataConfirmadaPagamentoPelaAgencia() {
        return dataConfirmadaPagamentoPelaAgencia;
    }

    public void setDataConfirmadaPagamentoPelaAgencia(Date dataPagamentoPelaAgencia) {
        this.dataConfirmadaPagamentoPelaAgencia = dataPagamentoPelaAgencia;
    }

    public String getResponsavelPagamentoAgencia() {
        return responsavelPagamentoAgencia;
    }

    public void setResponsavelPagamentoAgencia(String responsavelPagamentoAgencia) {
        this.responsavelPagamentoAgencia = responsavelPagamentoAgencia;
    }

    public Boolean getPagoParaVeiculo() {
        return pagoParaVeiculo;
    }

    public void setPagoParaVeiculo(Boolean pagoParaVeiculo) {
        this.pagoParaVeiculo = pagoParaVeiculo;
    }

    public Date getdataConfirmadaPagamentoParaVeiculo() {
        return dataConfirmadaPagamentoParaVeiculo;
    }

    public void setdataConfirmadaPagamentoParaVeiculo(Date PagamentoParaVeiculo) {
        this.dataConfirmadaPagamentoParaVeiculo = PagamentoParaVeiculo;
    }

    public String getResponsavelPagamentoVeiculo() {
        return responsavelPagamentoVeiculo;
    }

    public void setResponsavelPagamentoVeiculo(String responsavelPagamentoVeiculo) {
        this.responsavelPagamentoVeiculo = responsavelPagamentoVeiculo;
    }
    
    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    

    public PIDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getAgenciaId() {
        return agenciaId;
    }

    public void setAgenciaId(Integer agenciaId) {
        this.agenciaId = agenciaId;
    }

    public Integer getExecutivoId() {
        return executivoId;
    }

    public void setExecutivoId(Integer executivoId) {
        this.executivoId = executivoId;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public String getPraca() {
        return praca;
    }

    public void setPraca(String praca) {
        this.praca = praca;
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

    public BigDecimal getPercentualIndicacao() {
        return percentualIndicacao;
    }

    public void setPercentualIndicacao(BigDecimal percentualIndicacao) {
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

    public BigDecimal getPorcImposto() {
        return porcImposto;
    }

    public void setPorcImposto(BigDecimal porcImposto) {
        this.porcImposto = porcImposto;
    }

    public BigDecimal getPorcBV() {
        return porcBV;
    }

    public void setPorcBV(BigDecimal porcBV) {
        this.porcBV = porcBV;
    }

    public String getPiAgencia() {
        return piAgencia;
    }

    public void setPiAgencia(String piAgencia) {
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

    public void setCheckingEnviado(Date date) {
        this.checkingEnviado = date;
    }

    public String getPiI9Id() {
        return piI9Id;
    }

    public void setPiI9Id(String piI9Id) {
        this.piI9Id = piI9Id;
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
    
    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getNomeAgencia() {
        return nomeAgencia;
    }

    public void setNomeAgencia(String nomeAgencia) {
        this.nomeAgencia = nomeAgencia;
    }

    public String getNomeExecutivo() {
        return nomeExecutivo;
    }

    public void setNomeExecutivo(String nomeExecutivo) {
        this.nomeExecutivo = nomeExecutivo;
    }
    
    public String getEmEdicaoPor() { return emEdicaoPor; }
    public void setEmEdicaoPor(String emEdicaoPor) { this.emEdicaoPor = emEdicaoPor; }
    
    public Date getEdicaoInicio() { return edicaoInicio; }
    public void setEdicaoInicio(Date edicaoInicio) { this.edicaoInicio = edicaoInicio; }
}