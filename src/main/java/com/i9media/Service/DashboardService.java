package com.i9media.Service;

import com.i9media.models.Executivo;
import com.i9media.models.PedidoInsercao;
import com.i9media.utils.CanvasComponent;
import com.vaadin.flow.component.notification.Notification;
import com.i9media.Conectar;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardService {
	
	public static double obterTotalLiquidoFinalPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
	    double total = 0.0;

	    String sql = "SELECT liquidofinal FROM pi WHERE vencimentopiagencia >= ? AND vencimentopiagencia <= ?";

	    try (Connection conn = Conectar.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setDate(1, java.sql.Date.valueOf(dataInicio));
	        stmt.setDate(2, java.sql.Date.valueOf(dataFim));

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                total += rs.getDouble("liquidofinal");
	            }
	        }
	    }

	    return total;
	}
	
	public static double obterContasReceberMesAtual() throws SQLException {
	    double total = 0.0;

	    String sql = "SELECT valorliquido FROM pi WHERE " +
	                 "(" +
	                   "vencimentopiagencia >= date_trunc('month', CURRENT_DATE) AND " +
	                   "vencimentopiagencia < date_trunc('month', CURRENT_DATE + INTERVAL '1 month')" +
	                 ") " +
	                 "OR " +
	                 "(" +
	                   "(pago_pela_agencia = false OR pago_pela_agencia IS NULL) AND " +
	                   "vencimentopiagencia < date_trunc('month', CURRENT_DATE)" +
	                 ")";

	    try (Connection conn = Conectar.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	        while (rs.next()) {
	            total += rs.getDouble("valorliquido");
	        }
	    }

	    return total;
	}

	public static double obterContasPagarMesAtual() throws SQLException {
	    double total = 0.0;

	    String sql = "SELECT repasseveiculo FROM pi WHERE " +
	                 "(" +
	                   "datapagamentoparaveiculo >= date_trunc('month', CURRENT_DATE) AND " +
	                   "datapagamentoparaveiculo < date_trunc('month', CURRENT_DATE + INTERVAL '1 month')" +
	                 ") " +
	                 "OR " +
	                 "(" +
	                   "(pago_para_veiculo = false OR pago_para_veiculo IS NULL) AND " +
	                   "datapagamentoparaveiculo < date_trunc('month', CURRENT_DATE)" +
	                 ")";

	    try (Connection conn = Conectar.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	        while (rs.next()) {
	            total += rs.getDouble("repasseveiculo");
	        }
	    }

	    return total;
	}

	public static List<PedidoInsercao> buscarAPagarMesAtual() throws SQLException {
	    List<PedidoInsercao> pedidos = new ArrayList<>();

	    String sql = "SELECT * FROM pi WHERE " +
	                 "(" +
	                   "datapagamentoparaveiculo >= date_trunc('month', CURRENT_DATE) AND " +
	                   "datapagamentoparaveiculo < date_trunc('month', CURRENT_DATE + INTERVAL '1 month')" +
	                 ") " +
	                 "OR " +
	                 "(" +
	                   "(pago_para_veiculo = false OR pago_para_veiculo IS NULL) AND " +
	                   "datapagamentoparaveiculo < date_trunc('month', CURRENT_DATE)" +
	                 ") " +
	                 "ORDER BY datapagamentoparaveiculo ASC";

	    try (Connection conn = Conectar.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	        while (rs.next()) {
	            pedidos.add(PedidoInsercao.mapear(rs));
	        }
	    }

	    return pedidos;
	}

	public static List<PedidoInsercao> buscarAReceberMesAtual() throws SQLException {
	    List<PedidoInsercao> pedidos = new ArrayList<>();

	    String sql = "SELECT * FROM pi WHERE " +
	                 "(" +
	                   "vencimentopiagencia >= date_trunc('month', CURRENT_DATE) AND " +
	                   "vencimentopiagencia < date_trunc('month', CURRENT_DATE + INTERVAL '1 month')" +
	                 ") " +
	                 "OR " +
	                 "(" +
	                   "(pago_pela_agencia = false OR pago_pela_agencia IS NULL) AND " +
	                   "vencimentopiagencia < date_trunc('month', CURRENT_DATE)" +
	                 ") " +
	                 "ORDER BY vencimentopiagencia ASC";

	    try (Connection conn = Conectar.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	        while (rs.next()) {
	            pedidos.add(PedidoInsercao.mapear(rs));
	        }
	    }

	    return pedidos;
	}
	
	public static double obterSaldoProjetadoMesAtual() throws SQLException {
	    double receber = obterContasReceberMesAtual();
	    double pagar = obterContasPagarMesAtual();
	    return receber - pagar;
	}
	
	public static int contarPisUltimoMes() throws SQLException {
	    String sql = "SELECT COUNT(*) FROM pi WHERE " +
	                 "vencimentopiagencia >= date_trunc('month', CURRENT_DATE - INTERVAL '1 month') " +
	                 "AND vencimentopiagencia < date_trunc('month', CURRENT_DATE)";

	    try (Connection conn = Conectar.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	    }
	    return 0;
	}
	
	public static List<PedidoInsercao> obterPedidosComComissaoMensal(String nomeExecutivo) throws SQLException {
	    List<PedidoInsercao> lista = new ArrayList<>();

	    Executivo executivo = Executivo.buscarPorNome(nomeExecutivo);
	    if (executivo == null) return lista;

	    String sql = """
	        SELECT pi.*, 
	               a.nome AS agencia_nome, 
	               c.nome AS cliente_nome,
	               (pi.liquidofinal * (e.porcganhos / 100)) AS comissao_calculada
	        FROM pi
	        JOIN agencia a ON pi.agencia_id = a.id
	        JOIN clientes c ON pi.cliente_id = c.id
	        JOIN executivos e ON pi.executivo_id = e.id
	        WHERE pi.executivo_id = ?
	          AND EXTRACT(MONTH FROM pi.vencimentopiagencia) = ?
	          AND EXTRACT(YEAR FROM pi.vencimentopiagencia) = ?
	    """;

	    try (Connection conn = Conectar.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        YearMonth now = YearMonth.now();
	        stmt.setInt(1, executivo.getId());
	        stmt.setInt(2, now.getMonthValue());
	        stmt.setInt(3, now.getYear());

	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            PedidoInsercao pi = new PedidoInsercao();

	            pi.setId(rs.getInt("id"));
	            pi.setClienteId(rs.getInt("cliente_id"));
	            pi.setAgenciaId(rs.getInt("agencia_id"));
	            pi.setExecutivoId(rs.getInt("executivo_id"));
	            pi.setLiquidoFinal(rs.getBigDecimal("liquidofinal"));

	            pi.setClienteNome(rs.getString("cliente_nome"));
	            pi.setAgenciaNome(rs.getString("agencia_nome"));
	            pi.setComissaoCalculada(rs.getBigDecimal("comissao_calculada"));

	            lista.add(pi);
	        }
	    }

	    return lista;
	}

    public static double obterTotalVendasMensal(String nomeExecutivo) throws SQLException {
        Executivo executivo = Executivo.buscarPorNome(nomeExecutivo);
        if (executivo == null) return 0.0;

        String sql = "SELECT COALESCE(SUM(liquidofinal), 0) FROM pi " +
                     "WHERE EXTRACT(MONTH FROM vencimentopiagencia) = ? " +
                     "AND EXTRACT(YEAR FROM vencimentopiagencia) = ? " +
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
                     "WHERE EXTRACT(MONTH FROM pi.vencimentopiagencia) = ? " +
                     "AND EXTRACT(YEAR FROM pi.vencimentopiagencia) = ? " +
                     "AND pi.executivo_id = ?";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            YearMonth now = YearMonth.now();
            stmt.setInt(1, now.getMonthValue());
            stmt.setInt(2, now.getYear());
            stmt.setInt(3, executivo.getId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal totalComissao = rs.getBigDecimal("total_comissao");
                return totalComissao != null ? totalComissao.doubleValue() : 0.0;
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
    
    public static void atualizarGraficoCampanhas(CanvasComponent canvas, String nomeExecutivo) {
        try {
            Map<String, Integer> campanhasPorMes = DashboardService.obterCampanhasPorMes(nomeExecutivo);

            List<String> labelsList = new ArrayList<>(campanhasPorMes.keySet());
            List<Integer> valuesList = new ArrayList<>(campanhasPorMes.values());

            canvas.clear();
            canvas.renderBarChart("Campanhas por Mês", labelsList, valuesList, "Quantidade");

        } catch (SQLException e) {
            e.printStackTrace();
            Notification.show("Erro ao atualizar gráfico de campanhas: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    public static void atualizarGraficoMidia(CanvasComponent canvas, String nomeExecutivo) {
        try {
            Map<String, Double> mediaVendas = DashboardService.obterMediaVendasPorMes(nomeExecutivo);
            List<String> labelsList = new ArrayList<>(mediaVendas.keySet());
            List<Double> valuesList = new ArrayList<>(mediaVendas.values());

            canvas.clear();
            canvas.renderLineChart("Média de Vendas por Mês", labelsList, valuesList, "Valor R$");

        } catch (SQLException e) {
            e.printStackTrace();
            Notification.show("Erro ao atualizar gráfico de média de vendas: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    public static Map<String, Integer> obterCampanhasPorMes(String nomeExecutivo) throws SQLException {
        Executivo executivo = Executivo.buscarPorNome(nomeExecutivo);
        if (executivo == null) return new LinkedHashMap<>();

        String sql = "SELECT TO_CHAR(vencimentopiagencia, 'Mon') AS mes, COUNT(*) AS total " +
                     "FROM pi " +
                     "WHERE EXTRACT(YEAR FROM vencimentopiagencia) = ? AND executivo_id = ? " +
                     "GROUP BY mes " +
                     "ORDER BY MIN(vencimentopiagencia)";

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
        System.out.println(">>> Buscando média de vendas por mês para o executivo: " + nomeExecutivo);

        Executivo executivo = Executivo.buscarPorNome(nomeExecutivo);
        if (executivo == null) {
            System.out.println(">>> Executivo não encontrado.");
            return new LinkedHashMap<>();
        }

        String sql = "SELECT TO_CHAR(vencimentopiagencia, 'MM/YYYY') AS mes, " +
                     "AVG(liquidofinal) AS media " +
                     "FROM pi " +
                     "WHERE EXTRACT(YEAR FROM vencimentopiagencia) = ? AND executivo_id = ? " +
                     "GROUP BY mes " +
                     "ORDER BY MIN(vencimentopiagencia)";

        Map<String, Double> medias = new LinkedHashMap<>();
        int anoAtual = LocalDate.now().getYear();
        System.out.println(">>> Ano usado para filtro: " + anoAtual);
        System.out.println(">>> ID do executivo: " + executivo.getId());

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, anoAtual);
            stmt.setInt(2, executivo.getId());

            ResultSet rs = stmt.executeQuery();
            System.out.println(">>> Resultados da consulta:");
            while (rs.next()) {
                String mes = rs.getString("mes");
                double media = rs.getDouble("media");
                System.out.println(" - Mês: " + mes + " | Média: " + media);
                medias.put(mes, media);
            }

        } catch (SQLException e) {
            System.err.println(">>> Erro ao buscar média de vendas por mês:");
            e.printStackTrace();
        }

        System.out.println(">>> Mapa final retornado: " + medias);
        return medias;
    }
}