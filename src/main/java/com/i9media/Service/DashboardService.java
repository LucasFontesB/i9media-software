package com.i9media.Service;

import com.i9media.models.Executivo;
import com.i9media.Conectar;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

import com.i9media.models.Executivo;
import com.i9media.Conectar;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardService {

    public static double obterTotalVendasMensal(String nomeExecutivo) throws SQLException {
        Executivo executivo = Executivo.buscarPorNome(nomeExecutivo);
        if (executivo == null) return 0.0;

        String sql = "SELECT COALESCE(SUM(liquidofinal), 0) FROM pi " +
                     "WHERE EXTRACT(MONTH FROM datapagamentoparaveiculo) = ? " +
                     "AND EXTRACT(YEAR FROM datapagamentoparaveiculo) = ? " +
                     "AND executivo_id = ?";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            YearMonth now = YearMonth.now();
            stmt.setInt(1, now.getMonthValue());
            stmt.setInt(2, now.getYear());
            stmt.setInt(3, executivo.getId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    public static double obterTotalComissaoMensal(String nomeExecutivo) throws SQLException {
        Executivo executivo = Executivo.buscarPorNome(nomeExecutivo);
        if (executivo == null) return 0.0;

        String sql = "SELECT COALESCE(SUM(pi.liquidofinal * (e.porcganhos / 100)), 0) AS total_comissao " +
                     "FROM pi " +
                     "JOIN executivos e ON pi.executivo_id = e.id " +
                     "WHERE EXTRACT(MONTH FROM pi.datapagamentoparaveiculo) = ? " +
                     "AND EXTRACT(YEAR FROM pi.datapagamentoparaveiculo) = ? " +
                     "AND pi.executivo_id = ?";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            YearMonth now = YearMonth.now();
            stmt.setInt(1, now.getMonthValue());
            stmt.setInt(2, now.getYear());
            stmt.setInt(3, executivo.getId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_comissao");
            }
        }
        return 0.0;
    }

    public static double obterMetaMensal(String nomeExecutivo) throws SQLException {
        Executivo executivo = Executivo.buscarPorNome(nomeExecutivo);
        if (executivo == null) return 0.0;

        String sql = "SELECT valor FROM meta_executivo " +
                     "WHERE executivo_id = ? AND ano = ? AND mes = ?";

        YearMonth agora = YearMonth.now();
        int ano = agora.getYear();
        int mes = agora.getMonthValue();

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, executivo.getId());
            stmt.setInt(2, ano);
            stmt.setInt(3, mes);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("valor");
            }
        }

        return 0.0;
    }

    public static Map<String, Integer> obterCampanhasPorMes(String nomeExecutivo) throws SQLException {
        Executivo executivo = Executivo.buscarPorNome(nomeExecutivo);
        if (executivo == null) return new LinkedHashMap<>();

        String sql = "SELECT TO_CHAR(datapagamentoparaveiculo, 'Mon') AS mes, COUNT(*) AS total " +
                     "FROM pi " +
                     "WHERE EXTRACT(YEAR FROM datapagamentoparaveiculo) = ? AND executivo_id = ? " +
                     "GROUP BY mes " +
                     "ORDER BY MIN(datapagamentoparaveiculo)";

        Map<String, Integer> campanhas = new LinkedHashMap<>();
        int anoAtual = LocalDate.now().getYear();

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, anoAtual);
            stmt.setInt(2, executivo.getId());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                campanhas.put(rs.getString("mes"), rs.getInt("total"));
            }
        }
        return campanhas;
    }

    public static Map<String, Double> obterMediaVendasPorMes(String nomeExecutivo) throws SQLException {
        Executivo executivo = Executivo.buscarPorNome(nomeExecutivo);
        if (executivo == null) return new LinkedHashMap<>();

        String sql = "SELECT TO_CHAR(datapagamentoparaveiculo, 'Mon') AS mes, AVG(liquidofinal) AS media " +
                     "FROM pi " +
                     "WHERE EXTRACT(YEAR FROM datapagamentoparaveiculo) = ? AND executivo_id = ? " +
                     "GROUP BY mes " +
                     "ORDER BY MIN(datapagamentoparaveiculo)";

        Map<String, Double> medias = new LinkedHashMap<>();
        int anoAtual = LocalDate.now().getYear();

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, anoAtual);
            stmt.setInt(2, executivo.getId());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                medias.put(rs.getString("mes"), rs.getDouble("media"));
            }
        }
        return medias;
    }
}