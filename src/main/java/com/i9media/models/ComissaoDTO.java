package com.i9media.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.i9media.Conectar;

public class ComissaoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String executivo;
    private String cliente;
    private String agencia;
    private BigDecimal valorLiquidoFinal;
    private BigDecimal porcentagemGanho;
    private BigDecimal comissaoCalculada;
    private LocalDate vencimento;

    // Construtor vazio
    public ComissaoDTO() {
    }

    // Construtor que popula a partir do ResultSet
    public ComissaoDTO(ResultSet rs) throws SQLException {
        this.executivo = rs.getString("executivo_nome");
        this.cliente = rs.getString("cliente_nome");
        this.agencia = rs.getString("agencia_nome");
        this.valorLiquidoFinal = rs.getBigDecimal("liquidofinal");
        this.porcentagemGanho = rs.getBigDecimal("porcganhos");
        this.comissaoCalculada = rs.getBigDecimal("comissao_calculada");

        java.sql.Date dataSql = rs.getDate("vencimentopiagencia");
        this.vencimento = (dataSql != null) ? dataSql.toLocalDate() : null;
    }
    
    public static List<ComissaoDTO> buscarComissaoPorExecutivo(String nomeExecutivo, int mes, int ano) throws SQLException {
        String sql = """
            SELECT pi.*, 
                   a.nome AS agencia_nome, 
                   c.nome AS cliente_nome,
                   e.nome AS executivo_nome,
                   e.porcganhos,
                   (pi.liquidofinal * (e.porcganhos / 100)) AS comissao_calculada
            FROM pi
            JOIN agencia a ON pi.agencia_id = a.id
            JOIN clientes c ON pi.cliente_id = c.id
            JOIN executivos e ON pi.executivo_id = e.id
            WHERE LOWER(e.nome) = LOWER(?)
              AND EXTRACT(MONTH FROM pi.vencimentopiagencia) = ?
              AND EXTRACT(YEAR FROM pi.vencimentopiagencia) = ?
        """;

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeExecutivo);
            stmt.setInt(2, mes);
            stmt.setInt(3, ano);

            ResultSet rs = stmt.executeQuery();
            List<ComissaoDTO> resultados = new ArrayList<>();

            while (rs.next()) {
                ComissaoDTO dto = new ComissaoDTO(rs);
                resultados.add(dto);
            }

            return resultados;
        }
    }
    
    public static List<ComissaoDTO> buscarComissaoTodosExecutivos(int mes, int ano) throws SQLException {
        String sql = """
            SELECT pi.*, 
                   a.nome AS agencia_nome, 
                   c.nome AS cliente_nome,
                   e.nome AS executivo_nome,
                   e.porcganhos,
                   (pi.liquidofinal * (e.porcganhos / 100)) AS comissao_calculada
            FROM pi
            JOIN agencia a ON pi.agencia_id = a.id
            JOIN clientes c ON pi.cliente_id = c.id
            JOIN executivos e ON pi.executivo_id = e.id
            WHERE EXTRACT(MONTH FROM pi.vencimentopiagencia) = ?
              AND EXTRACT(YEAR FROM pi.vencimentopiagencia) = ?
        """;

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mes);
            stmt.setInt(2, ano);

            ResultSet rs = stmt.executeQuery();
            List<ComissaoDTO> resultados = new ArrayList<>();

            while (rs.next()) {
                ComissaoDTO dto = new ComissaoDTO(rs);
                resultados.add(dto);
            }

            return resultados;
        }
    }
    
    public static List<ComissaoDTO> buscarComissaoTodosExecutivos(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        String sql = """
            SELECT pi.*, 
                   a.nome AS agencia_nome, 
                   c.nome AS cliente_nome,
                   e.nome AS executivo_nome,
                   e.porcganhos,
                   (pi.liquidofinal * (e.porcganhos / 100)) AS comissao_calculada
            FROM pi
            JOIN agencia a ON pi.agencia_id = a.id
            JOIN clientes c ON pi.cliente_id = c.id
            JOIN executivos e ON pi.executivo_id = e.id
            WHERE pi.vencimentopiagencia >= ? AND pi.vencimentopiagencia <= ?
        """;

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(dataInicio.atStartOfDay()));
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(dataFim.atTime(23, 59, 59)));

            ResultSet rs = stmt.executeQuery();
            List<ComissaoDTO> resultados = new ArrayList<>();

            while (rs.next()) {
                ComissaoDTO dto = new ComissaoDTO(rs);
                resultados.add(dto);
            }
            return resultados;
        }
    }
    
    public static List<ComissaoDTO> buscarComissaoPorExecutivo(String nomeExecutivo, LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        String sql = """
            SELECT pi.*, 
                   a.nome AS agencia_nome, 
                   c.nome AS cliente_nome,
                   e.nome AS executivo_nome,
                   e.porcganhos,
                   (pi.liquidofinal * (e.porcganhos / 100)) AS comissao_calculada
            FROM pi
            JOIN agencia a ON pi.agencia_id = a.id
            JOIN clientes c ON pi.cliente_id = c.id
            JOIN executivos e ON pi.executivo_id = e.id
            WHERE LOWER(e.nome) = LOWER(?)
              AND pi.vencimentopiagencia BETWEEN ? AND ?
        """;

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nomeExecutivo);
            stmt.setDate(2, java.sql.Date.valueOf(dataInicio));
            stmt.setDate(3, java.sql.Date.valueOf(dataFim));

            ResultSet rs = stmt.executeQuery();
            List<ComissaoDTO> resultados = new ArrayList<>();

            while (rs.next()) {
                ComissaoDTO dto = new ComissaoDTO(rs);
                resultados.add(dto);
            }
            return resultados;
        }
    }

    // Getters e Setters
    public String getExecutivo() {
        return executivo;
    }

    public void setExecutivo(String executivo) {
        this.executivo = executivo;
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

    public BigDecimal getValorLiquidoFinal() {
        return valorLiquidoFinal;
    }

    public void setValorLiquidoFinal(BigDecimal valorLiquidoFinal) {
        this.valorLiquidoFinal = valorLiquidoFinal;
    }

    public BigDecimal getPorcentagemGanho() {
        return porcentagemGanho;
    }

    public void setPorcentagemGanho(BigDecimal porcentagemGanho) {
        this.porcentagemGanho = porcentagemGanho;
    }

    public BigDecimal getComissaoCalculada() {
        return comissaoCalculada;
    }

    public void setComissaoCalculada(BigDecimal comissaoCalculada) {
        this.comissaoCalculada = comissaoCalculada;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    @Override
    public String toString() {
        return "ComissaoDTO{" +
                "executivo='" + executivo + '\'' +
                ", cliente='" + cliente + '\'' +
                ", agencia='" + agencia + '\'' +
                ", valorLiquidoFinal=" + valorLiquidoFinal +
                ", porcentagemGanho=" + porcentagemGanho +
                ", comissaoCalculada=" + comissaoCalculada +
                ", vencimento=" + vencimento +
                '}';
    }
}
