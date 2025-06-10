package com.i9media.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.i9media.Conectar;


public class Executivo {
    private Integer id;
    private String nome;

    public Executivo() {
    }

    public boolean salvarNoBanco() throws SQLException {
        String sql = "INSERT INTO executivos (nome) VALUES (?)";
        try (
            Connection conn = Conectar.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, this.nome);
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
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Executivo executivo = null;

        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }

        try {
        	conn = Conectar.getConnection();

            String sql = "SELECT id, nome FROM executivos WHERE nome = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);

            rs = stmt.executeQuery();

            if (rs.next()) {
                executivo = new Executivo();
                executivo.setId(rs.getInt("id"));
                executivo.setNome(rs.getString("nome"));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar executivo por nome: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("Executivo: "+executivo);
        return executivo;
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
            String sql = "SELECT id, nome FROM executivos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                executivo = new Executivo();
                executivo.setId(rs.getInt("id"));
                executivo.setNome(rs.getString("nome"));
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
    
    public static Executivo buscarExecutivoPorAgencia(Integer agenciaId) {
        Executivo executivo = null;
        String sql = "SELECT e.id, e.nome FROM executivos e " +
                     "JOIN executivo_agencia ea ON e.id = ea.executivo_id " +
                     "WHERE ea.agencia_id = ? LIMIT 1";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, agenciaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    executivo = new Executivo();
                    executivo.setId(rs.getInt("id"));
                    executivo.setNome(rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return executivo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}