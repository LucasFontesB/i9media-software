package com.i9media.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.i9media.Conectar;

public class Cliente {
    private Integer id;
    private String nome;
    private String endereco;
    private String contato;


    public Cliente() {
    }
    
    public static List<Cliente> buscarTodosNomes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nome FROM clientes ORDER BY nome";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                clientes.add(cliente);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientes;
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
        String sql = "SELECT COUNT(*) FROM clientes WHERE LOWER(nome) = LOWER(?)";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome.trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
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
        if (nome == null || nome.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT id, nome, endereco, contato FROM clientes WHERE nome = ?";
        Cliente cliente = null;

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNome(rs.getString("nome"));
                    cliente.setEndereco(rs.getString("endereco"));
                    cliente.setContato(rs.getString("contato"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por nome: " + e.getMessage());
            e.printStackTrace();
        }

        return cliente;
    }
    
    public static Cliente buscarPorId(Integer id) {
        if (id == null) {
            return null;
        }

        String sql = "SELECT id, nome, endereco, contato FROM clientes WHERE id = ?";
        Cliente cliente = null;

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente();
                    cliente.setId(rs.getInt("id"));
                    cliente.setNome(rs.getString("nome"));
                    cliente.setEndereco(rs.getString("endereco"));
                    cliente.setContato(rs.getString("contato"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por id: " + e.getMessage());
            e.printStackTrace();
        }

        return cliente;
    }
}