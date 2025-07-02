package com.i9media.models;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Locale;

import com.i9media.Conectar;

public class ContaPagarDTO {

    private String veiculo;
    private BigDecimal valor;
    private LocalDate dataPagamento;
    private String status;

    public ContaPagarDTO() {}

    public ContaPagarDTO(String veiculo, BigDecimal valor, LocalDate dataPagamento, String status) {
        this.veiculo = veiculo;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
        this.status = status;
    }
    
    public static List<ContaPagarDTO> buscarPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<ContaPagarDTO> resultados = new ArrayList<>();

        String sql = "SELECT veiculo, datapagamentoparaveiculo, repasseveiculo, pago_para_veiculo " +
                     "FROM pi " +
                     "WHERE datapagamentoparaveiculo BETWEEN ? AND ? " +
                     "ORDER BY datapagamentoparaveiculo ASC";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(inicio));
            stmt.setDate(2, Date.valueOf(fim));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ContaPagarDTO dto = new ContaPagarDTO();
                dto.setVeiculo(rs.getString("veiculo"));
                dto.setDataPagamento(rs.getDate("datapagamentoparaveiculo").toLocalDate());
                dto.setValor(rs.getBigDecimal("repasseveiculo"));
                dto.setStatus(rs.getBoolean("pago_para_veiculo") ? "Pago" : "Pendente");

                resultados.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultados;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValorFormatado() {
        if (valor == null) return "R$ 0,00";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatter.format(valor);
    }
}