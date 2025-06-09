package com.i9media.models;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.i9media.CaixaMensagem;
import com.i9media.Conectar;

public class Agencia {
    private String id;
    private String nome;
    private String cnpj;
    private String endereco;
    private String contato;
    private String executivoResponsavel;
    private BigDecimal valorBV;
    
    public boolean salvarNoBanco() {
        String sql = "INSERT INTO agencia (nome, cnpj, endereco, contato, executivo_responsavel, valor_bv) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        Connection conn = null;
        boolean sucesso = false;

        try {
            conn = Conectar.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, this.nome);
            ps.setString(2, this.cnpj);
            ps.setString(3, this.endereco);
            ps.setString(4, this.contato);
            ps.setString(5, this.executivoResponsavel);
            ps.setBigDecimal(6, this.valorBV);

            int linhasAfetadas = ps.executeUpdate();
            if (linhasAfetadas > 0) {
                sucesso = true;
                CaixaMensagem.info_box("Cadastro", "Agência cadastrada com sucesso!");
            } else {
                CaixaMensagem.info_box("Falha", "Nenhuma linha inserida.");
            }

        } catch (Exception e) {
            CaixaMensagem.info_box("Erro", "Erro ao cadastrar agência.");
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
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
        String sql = "SELECT * FROM agencia WHERE nome = ?";
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
                agencia.setId(String.valueOf(rs.getInt("id")));
                agencia.setNome(rs.getString("nome"));
                agencia.setCnpj(rs.getString("cnpj"));
                agencia.setEndereco(rs.getString("endereco"));
                agencia.setContato(rs.getString("contato"));
                agencia.setExecutivoResponsavel(rs.getString("executivo_responsavel"));
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

    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
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

    public String getExecutivoResponsavel() {
        return executivoResponsavel;
    }

    public void setExecutivoResponsavel(String executivoResponsavel) {
        this.executivoResponsavel = executivoResponsavel;
    }

    public BigDecimal getValorBV() {
        return valorBV;
    }

    public void setValorBV(BigDecimal valorBV) {
        this.valorBV = valorBV;
    }
}