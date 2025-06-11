package com.i9media.models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.i9media.CaixaMensagem;
import com.i9media.Conectar;

public class Agencia {
    private Integer id;
    private String nome;
    private String cnpj;
    private String endereco;
    private String contato;
    private List<Integer> executivosIds = new ArrayList<>();
    private Integer executivoPadrao;
    private BigDecimal valorBV;
    
    public static boolean existePorNome(String nome) {
        String sql = "SELECT 1 FROM agencia WHERE LOWER(nome) = LOWER(?)";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "Agencia{" +
               "id=" + id +
               ", nome='" + nome + '\'' +
               ", cnpj='" + cnpj + '\'' +
               ", endereco='" + endereco + '\'' +
               ", contato='" + contato + '\'' +
               ", executivosIds=" + executivosIds +
               ", executivoPadrao=" + executivoPadrao +
               ", valorBV=" + valorBV +
               '}';
    }

    public boolean salvarNoBanco() {
        String sql = "INSERT INTO agencia (nome, cnpj, endereco, contato, valor_bv) " +
                     "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;
        boolean sucesso = false;

        try {
            conn = Conectar.getConnection();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, this.nome);
            ps.setString(2, this.cnpj);
            ps.setString(3, this.endereco);
            ps.setString(4, this.contato);
            ps.setBigDecimal(5, this.valorBV);

            int linhasAfetadas = ps.executeUpdate();
            if (linhasAfetadas > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    this.id = rs.getInt(1);

                    for (Integer executivoId : executivosIds) {
                        try (PreparedStatement psNn = conn.prepareStatement(
                            "INSERT INTO executivo_agencia (executivo_id, agencia_id) VALUES (?, ?)")) {
                            psNn.setInt(1, executivoId);
                            psNn.setInt(2, this.id);
                            psNn.executeUpdate();
                        }
                    }

                    conn.commit();
                    sucesso = true;
                    CaixaMensagem.info_box("Cadastro", "Agência cadastrada com sucesso! ID: " + this.id);
                }
            } else {
                conn.rollback();
                CaixaMensagem.info_box("Falha", "Nenhuma linha inserida.");
            }

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            CaixaMensagem.info_box("Erro", "Erro ao cadastrar agência: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return sucesso;
    }
    
    public static BigDecimal buscarValorBVPorNome(String nomeAgencia) {
        String sql = "SELECT valor_bv FROM agencia WHERE LOWER(nome) = LOWER(?)";
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;
        BigDecimal valorBV = BigDecimal.ZERO;

        try {
            conn = Conectar.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, nomeAgencia);
            rs = ps.executeQuery();

            if (rs.next()) {
                valorBV = rs.getBigDecimal("valor_bv");
            }
        } catch (Exception e) {
            CaixaMensagem.info_box("Erro", "Erro ao buscar BV da agência.");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return valorBV;
    }
    
    public static Agencia buscarPorNome(String nomeAgencia) {
        String sql = "SELECT * FROM agencia WHERE LOWER(nome) = LOWER(?)";
        PreparedStatement ps = null;
        Connection conn = null;
        Agencia agencia = null;
        
        try {
            conn = Conectar.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, nomeAgencia);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                agencia = new Agencia();
                agencia.setId(Integer.valueOf(rs.getInt("id")));
                agencia.setNome(rs.getString("nome"));
                agencia.setCnpj(rs.getString("cnpj"));
                agencia.setEndereco(rs.getString("endereco"));
                agencia.setContato(rs.getString("contato"));
                agencia.setValorBV(rs.getBigDecimal("valor_bv"));
            } 
        } catch (Exception e) {
            CaixaMensagem.info_box("Erro", "Erro ao buscar agência no banco");
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return agencia;
    }
    
    public static Agencia buscarPorId(Integer id) {
        String sql = "SELECT * FROM agencia WHERE id = ?";
        PreparedStatement ps = null;
        Connection conn = null;
        Agencia agencia = null;
        
        try {
            conn = Conectar.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                agencia = new Agencia();
                agencia.setId(Integer.valueOf(rs.getInt("id")));
                agencia.setNome(rs.getString("nome"));
                agencia.setCnpj(rs.getString("cnpj"));
                agencia.setEndereco(rs.getString("endereco"));
                agencia.setContato(rs.getString("contato"));
                agencia.setValorBV(rs.getBigDecimal("valor_bv"));
            } 
        } catch (Exception e) {
            CaixaMensagem.info_box("Erro", "Erro ao buscar agência no banco");
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return agencia;
    }

    public List<Integer> getExecutivosIds() {
        return executivosIds;
    }

    public void setExecutivosIds(List<Integer> executivosIds) {
        this.executivosIds = executivosIds;
    }

    public Integer getExecutivoPadrao() {
        return executivoPadrao;
    }

    public void setExecutivoPadrao(Integer executivoPadrao) {
        this.executivoPadrao = executivoPadrao;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }
    public BigDecimal getValorBV() { return valorBV; }
    public void setValorBV(BigDecimal valorBV) { this.valorBV = valorBV; }
}