package com.i9media.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.i9media.Conectar;

public class Cliente {
    private Integer id;
    private String nome;
    private String endereco;
    private String contato;


    public Cliente() {
    }


    public Cliente(Integer id, String nome, String endereco, String contato) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.contato = contato;
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

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }
    
    public static boolean existePorNomeIgnoreCase(String nome) {
        try (Connection conn = Conectar.getConnection()) {
            String sql = "SELECT COUNT(*) FROM clientes WHERE LOWER(nome) = LOWER(?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nome.trim());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean salvarNoBanco() {
        String sql = "INSERT INTO clientes (nome, endereco, contato) VALUES (?, ?, ?)";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, endereco);
            stmt.setString(3, contato);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Cliente buscarPorNome(String nome) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Cliente cliente = null;

        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }

        try {
        	conn = Conectar.getConnection();

            String sql = "SELECT id, nome, endereco, contato FROM clientes WHERE nome = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome.trim());

            rs = stmt.executeQuery();

            if (rs.next()) {
                cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setEndereco(rs.getString("endereco"));
                cliente.setContato(rs.getString("contato"));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por nome: " + e.getMessage());
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
        return cliente;
    }
    
    public static Cliente buscarPorId(Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Cliente cliente = null;

        if (id == null) {
            return null;
        }

        try {
        	conn = Conectar.getConnection();

            String sql = "SELECT id, nome, endereco, contato FROM clientes WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setEndereco(rs.getString("endereco"));
                cliente.setContato(rs.getString("contato"));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por id: " + e.getMessage());
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
        return cliente;
    }
}