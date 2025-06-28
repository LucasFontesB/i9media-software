package com.i9media.models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.i9media.CaixaMensagem;
import com.i9media.Conectar;

public class Usuario {
	private String id;
	private String nome;
	private String usuario;
	private String senha;
	private String email;
	private Executivo executivo;
	private String departamento;
	private Date criado_em;
	private Integer criadoPor;
	private boolean ativo;
	
	public static boolean salvarUsuario(Usuario usuario) {
		String sql = "INSERT INTO usuarios (nome, usuario, senha, email, departamento, executivo_id, criado_por) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getUsuario());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getDepartamento());

            if (usuario.getExecutivo() != null && usuario.getExecutivo().getId() != null) {
                stmt.setInt(6, usuario.getExecutivo().getId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            stmt.setInt(7, usuario.getCriadoPor());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	
	public static Usuario Iniciar_Usuario(int id) {
	    String sql = "SELECT * FROM usuarios WHERE id = ?";
	    Usuario usuario_logado = null;

	    try (
	        Connection conn = Conectar.getConnection();
	        PreparedStatement ps = conn.prepareStatement(sql)
	    ) {
	        ps.setInt(1, id);
	        try (ResultSet resultado = ps.executeQuery()) {
	            if (resultado.next()) {
	                usuario_logado = new Usuario();
	                usuario_logado.setId(String.valueOf(resultado.getInt("id")));
	                usuario_logado.setNome(resultado.getString("nome"));
	                usuario_logado.setUsuario(resultado.getString("usuario"));
	                usuario_logado.setSenha(resultado.getString("senha"));
	                usuario_logado.setEmail(resultado.getString("email"));
	                usuario_logado.setDepartamento(resultado.getString("departamento"));
	                usuario_logado.setCriado_em(resultado.getDate("criado_em"));
	                usuario_logado.setAtivo(resultado.getBoolean("ativo"));
	                usuario_logado.setCriadoPor(resultado.getInt("criado_por"));
	            } else {
	                CaixaMensagem.info_box("Erro Login", "Usuário Não Encontrado");
	            }
	        }
	    } catch (Exception e) {
	        CaixaMensagem.info_box("Erro", "Erro ao validar usuário");
	        e.printStackTrace();
	    }

	    return usuario_logado;
	}
	
	public Integer getCriadoPor() {
	    return criadoPor;
	}

	public void setCriadoPor(Integer string) {
	    this.criadoPor = string;
	}
	
	public Executivo getExecutivo() {
        return executivo;
    }
    public void setExecutivo(Executivo executivo) {
        this.executivo = executivo;
    }
	
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Date getCriado_em() {
        return criado_em;
    }

    public void setCriado_em(Date criado_em) {
        this.criado_em = criado_em;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
	
}
