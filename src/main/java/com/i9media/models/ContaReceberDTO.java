package com.i9media.models;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

import com.i9media.Conectar;

public class ContaReceberDTO {
    private String cliente;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private boolean pago; 

    public String getCliente() {
        return cliente;
    }
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public BigDecimal getValor() {
        return valor;
    }
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }
    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public boolean isPago() {
        return pago;
    }
    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public String getStatus() {
        return pago ? "Pago" : "Pendente";
    }

    public String getValorFormatado() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        nf.setRoundingMode(java.math.RoundingMode.HALF_UP);
        return nf.format(valor);
    }

    public static List<ContaReceberDTO> buscarPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<ContaReceberDTO> resultados = new ArrayList<>();

        String sql = "SELECT a.nome AS agencia_nome, p.valorliquido, p.vencimentopiagencia, p.pago_pela_agencia " +
                     "FROM pi p " +
                     "JOIN agencia a ON p.agencia_id = a.id " +
                     "WHERE p.vencimentopiagencia BETWEEN ? AND ? " +
                     "ORDER BY p.vencimentopiagencia ASC";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(inicio));
            stmt.setDate(2, Date.valueOf(fim));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ContaReceberDTO dto = new ContaReceberDTO();
                dto.setCliente(rs.getString("agencia_nome")); 
                dto.setValor(rs.getBigDecimal("valorliquido"));
                dto.setDataVencimento(rs.getDate("vencimentopiagencia").toLocalDate());
                dto.setPago(rs.getBoolean("pago_pela_agencia"));
                resultados.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultados;
    }
}
