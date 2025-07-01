package com.i9media.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.i9media.CaixaMensagem;
import com.i9media.Conectar;

import java.util.Date; 

public class PedidoInsercao {
    private Integer id;

    private Integer clienteId;
    private Integer agenciaId;
    private Integer executivoId;
    private String clienteNome;
    private String agenciaNome;
    private BigDecimal comissaoCalculada;

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
    private Integer piI9Id; 
    private Date dataPagamentoParaVeiculo;
    private String nfVeiculo;
    
    private String emEdicaoPor; 
    private Date edicaoInicio;
    
    private String criadoPor;
    private LocalDateTime dataDeCriacao;
    
    private Boolean pagoPelaAgencia;
    private Date dataConfirmadaPagamentoPelaAgencia;
    private String responsavelPagamentoAgencia;

    private Boolean pagoParaVeiculo;
    private Date dataConfirmadaPagamentoParaVeiculo;
    private String responsavelPagamentoVeiculo;

    public PedidoInsercao() {}
    
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
        return dataDeCriacao;
    }

    public void setDataCriacao(LocalDateTime dataDeCriacao) {
        this.dataDeCriacao = dataDeCriacao;
    }
    
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public String getAgenciaNome() { return agenciaNome; }
    public void setAgenciaNome(String agenciaNome) { this.agenciaNome = agenciaNome; }

    public BigDecimal getComissaoCalculada() { return comissaoCalculada; }
    public void setComissaoCalculada(BigDecimal comissaoCalculada) { this.comissaoCalculada = comissaoCalculada; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public Integer getAgenciaId() { return agenciaId; }
    public void setAgenciaId(Integer agenciaId) { this.agenciaId = agenciaId; }

    public Integer getExecutivoId() { return executivoId; }
    public void setExecutivoId(Integer executivoId) { this.executivoId = executivoId; }

    public String getVeiculo() { return veiculo; }
    public void setVeiculo(String veiculo) { this.veiculo = veiculo; }

    public String getPraca() { return praca; }
    public void setPraca(String praca) { this.praca = praca; }

    public BigDecimal getValorLiquido() { return valorLiquido; }
    public void setValorLiquido(BigDecimal valorLiquido) { this.valorLiquido = valorLiquido; }

    public BigDecimal getRepasseVeiculo() { return repasseVeiculo; }
    public void setRepasseVeiculo(BigDecimal repasseVeiculo) { this.repasseVeiculo = repasseVeiculo; }

    public BigDecimal getImposto() { return imposto; }
    public void setImposto(BigDecimal imposto) { this.imposto = imposto; }

    public BigDecimal getBvAgencia() { return bvAgencia; }
    public void setBvAgencia(BigDecimal bvAgencia) { this.bvAgencia = bvAgencia; }

    public BigDecimal getComissaoPercentual() { return comissaoPercentual; }
    public void setComissaoPercentual(BigDecimal comissaoPercentual) { this.comissaoPercentual = comissaoPercentual; }

    public BigDecimal getValorComissao() { return valorComissao; }
    public void setValorComissao(BigDecimal valorComissao) { this.valorComissao = valorComissao; }

    public BigDecimal getTotalLiquido() { return totalLiquido; }
    public void setTotalLiquido(BigDecimal totalLiquido) { this.totalLiquido = totalLiquido; }

    public String getMidiaResponsavel() { return midiaResponsavel; }
    public void setMidiaResponsavel(String midiaResponsavel) { this.midiaResponsavel = midiaResponsavel; }

    public BigDecimal getPercentualIndicacao() { return percentualIndicacao; }
    public void setPercentualIndicacao(BigDecimal percentualIndicacao) { this.percentualIndicacao = percentualIndicacao; }

    public BigDecimal getMidia() { return midia; }
    public void setMidia(BigDecimal midia) { this.midia = midia; }

    public BigDecimal getLiquidoFinal() { return liquidoFinal; }
    public void setLiquidoFinal(BigDecimal liquidoFinal) { this.liquidoFinal = liquidoFinal; }

    public BigDecimal getPorcImposto() { return porcImposto; }
    public void setPorcImposto(BigDecimal porcImposto) { this.porcImposto = porcImposto; }

    public BigDecimal getPorcBV() { return porcBV; }
    public void setPorcBV(BigDecimal porcBV) { this.porcBV = porcBV; }

    public String getPiAgencia() { return piAgencia; }
    public void setPiAgencia(String piAgencia) { this.piAgencia = piAgencia; }

    public Date getVencimentopiAgencia() { return vencimentopiAgencia; }
    public void setVencimentopiAgencia(Date vencimentopiAgencia) { this.vencimentopiAgencia = vencimentopiAgencia; }

    public Date getCheckingEnviado() { return checkingEnviado; }
    public void setCheckingEnviado(Date checkingEnviado) { this.checkingEnviado = checkingEnviado; }

    public Integer getPiI9Id() { return piI9Id; }
    public void setPiI9Id(Integer piI9Id) { this.piI9Id = piI9Id; }

    public Date getDataPagamentoParaVeiculo() { return dataPagamentoParaVeiculo; }
    public void setDataPagamentoParaVeiculo(Date dataPagamentoParaVeiculo) { this.dataPagamentoParaVeiculo = dataPagamentoParaVeiculo; }

    public String getNfVeiculo() { return nfVeiculo; }
    public void setNfVeiculo(String nfVeiculo) { this.nfVeiculo = nfVeiculo; }
    
    public String getEmEdicaoPor() { return emEdicaoPor; }
    public void setEmEdicaoPor(String emEdicaoPor) { this.emEdicaoPor = emEdicaoPor; }
    
    public Date getEdicaoInicio() { return edicaoInicio; }
    public void setEdicaoInicio(Date edicaoInicio) { this.edicaoInicio = edicaoInicio; }
    
    public static List<PedidoInsercao> buscarAPagarNosProximosDias() throws SQLException {
        List<PedidoInsercao> lista = new ArrayList<>();

        String sql = "SELECT * FROM pi " +
                     "WHERE pago_para_veiculo = false " +
                     "AND datapagamentoparaveiculo <= CURRENT_DATE + INTERVAL '7 day' " +
                     "ORDER BY datapagamentoparaveiculo ASC";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }

        return lista;
    }

    public static List<PedidoInsercao> buscarAReceberNosProximosDias() throws SQLException {
        List<PedidoInsercao> lista = new ArrayList<>();

        String sql = "SELECT * FROM pi " +
                     "WHERE pago_pela_agencia = false " +
                     "AND vencimentopiagencia <= CURRENT_DATE + INTERVAL '7 day' " +
                     "ORDER BY vencimentopiagencia ASC";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }

        return lista;
    }
    
    public static List<PedidoInsercao> buscarPedidosDoMesAtual() throws SQLException {
        List<PedidoInsercao> pedidos = new ArrayList<>();

        String sql = "SELECT * FROM pi WHERE vencimentopiagencia >= date_trunc('month', CURRENT_DATE) " +
                     "AND vencimentopiagencia < date_trunc('month', CURRENT_DATE + INTERVAL '1 month')";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                pedidos.add(PedidoInsercao.mapear(rs));
            }
        }

        return pedidos;
    }
    
    public static PedidoInsercao mapear(ResultSet rs) throws SQLException {
        PedidoInsercao pi = new PedidoInsercao();

        pi.setId(rs.getInt("id"));
        pi.setClienteId(rs.getInt("cliente_id"));
        pi.setAgenciaId(rs.getInt("agencia_id"));
        pi.setExecutivoId(rs.getInt("executivo_id"));
        pi.setVeiculo(rs.getString("veiculo"));
        pi.setPraca(rs.getString("praca")); 
        pi.setValorLiquido(rs.getBigDecimal("valorLiquido"));
        pi.setRepasseVeiculo(rs.getBigDecimal("repasseVeiculo"));
        pi.setImposto(rs.getBigDecimal("imposto"));
        pi.setBvAgencia(rs.getBigDecimal("bvAgencia"));
        pi.setComissaoPercentual(rs.getBigDecimal("comissaoPercentual"));
        pi.setValorComissao(rs.getBigDecimal("valorComissao"));
        pi.setTotalLiquido(rs.getBigDecimal("totalLiquido"));
        pi.setMidiaResponsavel(rs.getString("midiaResponsavel"));
        pi.setPercentualIndicacao(rs.getBigDecimal("percentualIndicacao"));
        pi.setMidia(rs.getBigDecimal("midia"));
        pi.setLiquidoFinal(rs.getBigDecimal("liquidoFinal"));
        pi.setPorcImposto(rs.getBigDecimal("porcimposto"));
        pi.setPorcBV(rs.getBigDecimal("porcbv"));
        pi.setPiAgencia(rs.getString("piAgencia"));
        pi.setVencimentopiAgencia(rs.getDate("vencimentopiAgencia"));
        pi.setCheckingEnviado(rs.getDate("checkingEnviado"));
        pi.setPiI9Id(rs.getObject("piI9_id", Integer.class)); 
        pi.setDataPagamentoParaVeiculo(rs.getDate("dataPagamentoParaVeiculo"));
        pi.setNfVeiculo(rs.getString("nfVeiculo"));
        pi.setEmEdicaoPor(rs.getString("em_edicao_por"));
        pi.setEdicaoInicio(rs.getTimestamp("edicao_inicio"));

        // NOVAS LINHAS
        pi.setCriadoPor(rs.getString("criado_por"));
        Timestamp timestamp = rs.getTimestamp("data_de_criacao");
        if (timestamp != null) {
            pi.setDataCriacao(timestamp.toLocalDateTime());
        } else {
            pi.setDataCriacao(null);
        }
        pi.setPagoPelaAgencia(rs.getBoolean("pago_pela_agencia"));
        pi.setDataConfirmadaPagamentoPelaAgencia(rs.getDate("data_pagamento_pela_agencia"));
        pi.setResponsavelPagamentoAgencia(rs.getString("responsavel_pagamento_agencia"));

        pi.setPagoParaVeiculo(rs.getBoolean("pago_para_veiculo"));
        pi.setResponsavelPagamentoVeiculo(rs.getString("responsavel_pagamento_veiculo"));
        pi.setdataConfirmadaPagamentoParaVeiculo(rs.getDate("data_pagamento_para_veiculo"));

        return pi;
    }

    public BigDecimal CalcularBVAgencia(String bv) {
        BigDecimal valor = this.valorLiquido;
        BigDecimal valor_bv = new BigDecimal(bv);

        BigDecimal percentualBVDecimal = valor_bv.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
        BigDecimal resultado = valor.multiply(percentualBVDecimal).setScale(2, RoundingMode.HALF_UP);
        return resultado;
    }
    
    public static List<PedidoInsercao> buscarTodos() {
        List<PedidoInsercao> pedidos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conectar.getConnection();

            String sql = "SELECT " +
                         "id, cliente_id, agencia_id, executivo_id, veiculo, praca, valorLiquido, " +
                         "repasseVeiculo, imposto, bvAgencia, comissaoPercentual, valorComissao, " +
                         "totalLiquido, midiaResponsavel, percentualIndicacao, midia, liquidoFinal, " +
                         "porcimposto, porcbv, piAgencia, vencimentopiAgencia, checkingEnviado, " +
                         "piI9_id, dataPagamentoParaVeiculo, nfVeiculo, em_edicao_por, edicao_inicio, " +
                         "pago_pela_agencia, data_pagamento_pela_agencia, responsavel_pagamento_agencia, " + 
                         "pago_para_veiculo, responsavel_pagamento_veiculo, criado_por, data_de_criacao, data_pagamento_para_veiculo " +
                         "FROM PI";

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                PedidoInsercao pi = new PedidoInsercao();

                pi.setId(rs.getInt("id"));
                pi.setClienteId(rs.getInt("cliente_id"));
                pi.setAgenciaId(rs.getInt("agencia_id"));
                pi.setExecutivoId(rs.getInt("executivo_id"));
                pi.setVeiculo(rs.getString("veiculo"));
                pi.setPraca(rs.getString("praca")); 
                pi.setValorLiquido(rs.getBigDecimal("valorLiquido"));
                pi.setRepasseVeiculo(rs.getBigDecimal("repasseVeiculo"));
                pi.setImposto(rs.getBigDecimal("imposto"));
                pi.setBvAgencia(rs.getBigDecimal("bvAgencia"));
                pi.setComissaoPercentual(rs.getBigDecimal("comissaoPercentual"));
                pi.setValorComissao(rs.getBigDecimal("valorComissao"));
                pi.setTotalLiquido(rs.getBigDecimal("totalLiquido"));
                pi.setMidiaResponsavel(rs.getString("midiaResponsavel"));
                pi.setPercentualIndicacao(rs.getBigDecimal("percentualIndicacao"));
                pi.setMidia(rs.getBigDecimal("midia"));
                pi.setLiquidoFinal(rs.getBigDecimal("liquidoFinal"));
                pi.setPorcImposto(rs.getBigDecimal("porcimposto"));
                pi.setPorcBV(rs.getBigDecimal("porcbv"));
                pi.setPiAgencia(rs.getString("piAgencia"));
                pi.setVencimentopiAgencia(rs.getDate("vencimentopiAgencia"));
                pi.setCheckingEnviado(rs.getDate("checkingEnviado"));
                pi.setPiI9Id(rs.getObject("piI9_id", Integer.class)); 
                pi.setDataPagamentoParaVeiculo(rs.getDate("dataPagamentoParaVeiculo"));
                pi.setNfVeiculo(rs.getString("nfVeiculo"));
                pi.setEmEdicaoPor(rs.getString("em_edicao_por"));
                pi.setEdicaoInicio(rs.getTimestamp("edicao_inicio"));

                // Novas colunas:
                pi.setCriadoPor(rs.getString("criado_por"));
                Timestamp timestamp = rs.getTimestamp("data_de_criacao");
                if (timestamp != null) {
                    pi.setDataCriacao(timestamp.toLocalDateTime());
                } else {
                    pi.setDataCriacao(null);
                }
                pi.setPagoPelaAgencia(rs.getBoolean("pago_pela_agencia"));
                pi.setDataConfirmadaPagamentoPelaAgencia(rs.getDate("data_pagamento_pela_agencia"));
                pi.setResponsavelPagamentoAgencia(rs.getString("responsavel_pagamento_agencia"));
                pi.setPagoParaVeiculo(rs.getBoolean("pago_para_veiculo"));
                pi.setResponsavelPagamentoVeiculo(rs.getString("responsavel_pagamento_veiculo"));
                pi.setdataConfirmadaPagamentoParaVeiculo(rs.getDate("data_pagamento_para_veiculo"));

                pedidos.add(pi);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os PIs: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos do banco de dados: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return pedidos;
    }

    public BigDecimal CalcularTotalLiquido() {
        BigDecimal valor_liquido = this.valorLiquido != null ? this.valorLiquido : BigDecimal.ZERO;
        BigDecimal valor_repasse = this.repasseVeiculo != null ? this.repasseVeiculo : BigDecimal.ZERO;
        BigDecimal valor_comissao = this.valorComissao != null ? this.valorComissao : BigDecimal.ZERO;
        BigDecimal valor_bvagencia = this.bvAgencia != null ? this.bvAgencia : BigDecimal.ZERO;
        BigDecimal valor_imposto = this.imposto != null ? this.imposto : BigDecimal.ZERO;

        BigDecimal resultado = valor_liquido
                .subtract(valor_repasse)
                .subtract(valor_bvagencia)
                .subtract(valor_imposto)
                .add(valor_comissao);
        resultado = resultado.setScale(2, RoundingMode.HALF_UP);
        return resultado;
    }
    
    public static PedidoInsercao buscarPorId(Integer id) {
        String sql = "SELECT * FROM pi WHERE id = ?";
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;
        PedidoInsercao pi = null;
        
        try {
            conn = Conectar.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                pi = new PedidoInsercao();
                pi.setId(rs.getInt("id"));
                pi.setClienteId(rs.getInt("cliente_id"));
                pi.setAgenciaId(rs.getInt("agencia_id"));
                pi.setExecutivoId(rs.getInt("executivo_id"));
                pi.setVeiculo(rs.getString("veiculo"));
                pi.setPraca(rs.getString("praca")); 
                pi.setValorLiquido(rs.getBigDecimal("valorLiquido"));
                pi.setRepasseVeiculo(rs.getBigDecimal("repasseVeiculo"));
                pi.setImposto(rs.getBigDecimal("imposto"));
                pi.setBvAgencia(rs.getBigDecimal("bvAgencia"));
                pi.setComissaoPercentual(rs.getBigDecimal("comissaoPercentual"));
                pi.setValorComissao(rs.getBigDecimal("valorComissao"));
                pi.setTotalLiquido(rs.getBigDecimal("totalLiquido"));
                pi.setMidiaResponsavel(rs.getString("midiaResponsavel"));
                pi.setPercentualIndicacao(rs.getBigDecimal("percentualIndicacao"));
                pi.setMidia(rs.getBigDecimal("midia"));
                pi.setLiquidoFinal(rs.getBigDecimal("liquidoFinal"));
                pi.setPorcImposto(rs.getBigDecimal("porcimposto"));
                pi.setPorcBV(rs.getBigDecimal("porcbv"));
                pi.setPiAgencia(rs.getString("piAgencia"));
                pi.setVencimentopiAgencia(rs.getDate("vencimentopiAgencia"));
                pi.setCheckingEnviado(rs.getDate("checkingEnviado"));
                pi.setPiI9Id(rs.getObject("piI9_id", Integer.class)); 
                pi.setDataPagamentoParaVeiculo(rs.getDate("dataPagamentoParaVeiculo"));
                pi.setNfVeiculo(rs.getString("nfVeiculo"));
                pi.setEmEdicaoPor(rs.getString("em_edicao_por"));
                pi.setEdicaoInicio(rs.getTimestamp("edicao_inicio"));
                
                // Novas colunas:
                pi.setCriadoPor(rs.getString("criado_por"));
                Timestamp timestamp = rs.getTimestamp("data_de_criacao");
                if (timestamp != null) {
                    pi.setDataCriacao(timestamp.toLocalDateTime());
                } else {
                    pi.setDataCriacao(null);
                }
                pi.setPagoPelaAgencia(rs.getBoolean("pago_pela_agencia"));
                pi.setDataConfirmadaPagamentoPelaAgencia(rs.getDate("data_pagamento_pela_agencia"));
                pi.setResponsavelPagamentoAgencia(rs.getString("responsavel_pagamento_agencia"));
                pi.setPagoParaVeiculo(rs.getBoolean("pago_para_veiculo"));
                pi.setResponsavelPagamentoVeiculo(rs.getString("responsavel_pagamento_veiculo"));
                pi.setdataConfirmadaPagamentoParaVeiculo(rs.getDate("data_pagamento_para_veiculo"));
            } 
        } catch (Exception e) {
            CaixaMensagem.info_box("Erro", "Erro ao buscar PI no banco");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();  
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return pi;
    }


    public BigDecimal CalcularComissao() {
        BigDecimal valor = this.repasseVeiculo != null ? this.repasseVeiculo : BigDecimal.ZERO;
        BigDecimal comissao = new BigDecimal("0.10"); 
        BigDecimal resultado = valor.multiply(comissao).setScale(2, RoundingMode.HALF_UP);
        return resultado;
    }

    public BigDecimal CalcularImposto(String porc_imposto) {
        BigDecimal valor = this.valorLiquido;
        BigDecimal percentual = new BigDecimal(porc_imposto);
        BigDecimal impostoDecimal = percentual.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
        BigDecimal resultado = valor.multiply(impostoDecimal).setScale(2, RoundingMode.HALF_UP);
        return resultado;
    }
    
    public void atualizar() {
        String sql = "UPDATE pi SET em_edicao_por = ?, edicao_inicio = ? WHERE id = ?";
        System.out.println("Iniciando Atualização No Banco");

        try (
            Connection conn = Conectar.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            System.out.println("Em edição (No PI): " + this.emEdicaoPor);
            stmt.setString(1, this.emEdicaoPor);
            if (this.edicaoInicio != null) {
                stmt.setTimestamp(2, new Timestamp(this.edicaoInicio.getTime()));
            } else {
                stmt.setTimestamp(2, null);
            }
            stmt.setInt(3, this.id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar PI no banco: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void salvar() throws SQLException {
        String sql = "INSERT INTO PI (" +
                     "cliente_id, agencia_id, executivo_id, veiculo, praca, valorLiquido, " +
                     "repasseVeiculo, imposto, bvAgencia, comissaoPercentual, valorComissao, " +
                     "totalLiquido, midiaResponsavel, percentualIndicacao, midia, liquidoFinal, " +
                     "porcimposto, porcbv, piAgencia, vencimentopiAgencia, checkingEnviado, " +
                     "piI9_id, dataPagamentoParaVeiculo, nfVeiculo, " +
                     "pago_pela_agencia, data_pagamento_pela_agencia, responsavel_pagamento_agencia, " +
                     "pago_para_veiculo, responsavel_pagamento_veiculo, criado_por, data_de_criacao, data_pagamento_para_veiculo" +
                     ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection conn = Conectar.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setInt(1, this.clienteId);
            stmt.setInt(2, this.agenciaId);
            stmt.setInt(3, this.executivoId);
            stmt.setString(4, this.veiculo);
            stmt.setString(5, this.praca);
            stmt.setBigDecimal(6, this.valorLiquido);
            stmt.setBigDecimal(7, this.repasseVeiculo); 
            stmt.setBigDecimal(8, this.imposto);
            stmt.setBigDecimal(9, this.bvAgencia);
            stmt.setBigDecimal(10, this.comissaoPercentual); 
            stmt.setBigDecimal(11, this.valorComissao); 
            stmt.setBigDecimal(12, this.totalLiquido);
            stmt.setString(13, this.midiaResponsavel);
            stmt.setBigDecimal(14, this.percentualIndicacao); 
            stmt.setBigDecimal(15, this.midia);
            stmt.setBigDecimal(16, this.liquidoFinal); 
            stmt.setBigDecimal(17, this.porcImposto);
            stmt.setBigDecimal(18, this.porcBV);
            stmt.setString(19, this.piAgencia);

            stmt.setDate(20, this.vencimentopiAgencia != null ? new java.sql.Date(this.vencimentopiAgencia.getTime()) : null);
            stmt.setDate(21, this.checkingEnviado != null ? new java.sql.Date(this.checkingEnviado.getTime()) : null);
            if (this.piI9Id != null) {
                stmt.setInt(22, this.piI9Id);
            } else {
                stmt.setNull(22, java.sql.Types.INTEGER);
            }
            stmt.setDate(23, this.dataPagamentoParaVeiculo != null ? new java.sql.Date(this.dataPagamentoParaVeiculo.getTime()) : null);
            stmt.setString(24, this.nfVeiculo);

            // Novos campos
            if (this.pagoPelaAgencia != null) {
                stmt.setBoolean(25, this.pagoPelaAgencia);
            } else {
                stmt.setNull(25, java.sql.Types.BOOLEAN);
            }

            stmt.setDate(26, this.dataConfirmadaPagamentoPelaAgencia != null ? new java.sql.Date(this.dataConfirmadaPagamentoPelaAgencia.getTime()) : null);

            stmt.setString(27, this.responsavelPagamentoAgencia);

            if (this.pagoParaVeiculo != null) {
                stmt.setBoolean(28, this.pagoParaVeiculo);
            } else {
                stmt.setNull(28, java.sql.Types.BOOLEAN);
            }

            stmt.setString(29, this.responsavelPagamentoVeiculo);
            stmt.setString(30, this.criadoPor);
            stmt.setTimestamp(31, Timestamp.valueOf(this.dataDeCriacao));
            stmt.setDate(32, this.dataConfirmadaPagamentoParaVeiculo != null ? new java.sql.Date(this.dataConfirmadaPagamentoParaVeiculo.getTime()) : null);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.id = generatedKeys.getInt(1);
                    }
                }
                System.out.println("PI salva com ID: " + this.id);
            }
        }
    }
}