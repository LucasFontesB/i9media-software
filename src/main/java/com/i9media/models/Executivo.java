package com.i9media.models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.i9media.Conectar;


public class Executivo {
    private Integer id;
    private String nome;
    private BigDecimal porcGanho;

    public Executivo() {
    }
    
    public static List<Executivo> buscarTodosNomes() {
        List<Executivo> executivos = new ArrayList<>();

        String sql = "SELECT id, nome FROM executivos ORDER BY nome";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Executivo executivo = new Executivo();
                executivo.setId(rs.getInt("id"));
                executivo.setNome(rs.getString("nome"));
                executivos.add(executivo);
            }

        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        return executivos;
    }

    public boolean salvarNoBanco() throws SQLException {
        String sql = "INSERT INTO executivos (nome, porcganhos) VALUES (?, ?)";
        try (
            Connection conn = Conectar.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, this.nome);
            ps.setBigDecimal(2, porcGanho);
            int linhasAfetadas = ps.executeUpdate();
            if (linhasAfetadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.setId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public static Executivo buscarPorNome(String nome) {
        String sql = "SELECT id, nome, porcganhos FROM executivos WHERE LOWER(nome) = LOWER(?)";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Executivo e = new Executivo();
                    e.setId(rs.getInt("id"));
                    e.setNome(rs.getString("nome"));
                    e.setPorcGanho(rs.getBigDecimal("porcganhos"));
                    return e;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Executivo buscarPorId(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Executivo executivo = null;

        if (id == null) {
            return null;
        }

        try {
        	conn = Conectar.getConnection();
            String sql = "SELECT id, nome, porcganhos FROM executivos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                executivo = new Executivo();
                executivo.setId(rs.getInt("id"));
                executivo.setNome(rs.getString("nome"));
                executivo.setPorcGanho(rs.getBigDecimal("porcganhos"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar executivo por ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
        return executivo;
    }

    public boolean atualizarNomeNoBanco() throws SQLException {
        String sql = "UPDATE executivos SET nome = ? WHERE id = ?";
        try (
            Connection conn = Conectar.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, this.nome);
            ps.setInt(2, this.id);
            return ps.executeUpdate() > 0;
        }
    }
    
    public static List<Executivo> buscarExecutivoPorAgencia(Integer agenciaId) {
        List<Executivo> executivos = new ArrayList<>();
        String sql = "SELECT e.id, e.nome, e.porcganhos FROM executivos e " +
                     "JOIN executivo_agencia ea ON e.id = ea.executivo_id " +
                     "WHERE ea.agencia_id = ?";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agenciaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Executivo executivo = new Executivo();
                    executivo.setId(rs.getInt("id"));
                    executivo.setNome(rs.getString("nome"));
                    executivo.setPorcGanho(rs.getBigDecimal("porcganhos"));
                    executivos.add(executivo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return executivos;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public BigDecimal getPorcGanho() {
    	return porcGanho;
    }
    
    public void setPorcGanho(BigDecimal porcGanho) {
    	this.porcGanho = porcGanho;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}