package com.i9media;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ValidarLogin {
	public static Integer Validar(String nome, String senha) {
		if(nome == null || nome.isEmpty() || senha == null || senha.isEmpty()) {
			CaixaMensagem.info_box("Erro Login", "Preencha Todos Os Campos");
			return null;
		}else {
			String sql = "SELECT id FROM usuarios WHERE usuario = ? AND senha = ?";
	        PreparedStatement ps = null;
	        Connection conn = null;
	        
	        try {
		           conn = Conectar.getConnection();
		           System.out.println("Conexão estabelecida com sucesso: " + (conn != null));
		           ps = conn.prepareStatement(sql);
		           ps.setString(1, nome);
		           ps.setString(2, senha);
		           ResultSet resultado = ps.executeQuery();
		           
		           if(resultado.next()) {
		        	   return resultado.getInt("id");
		           }else {
		        	   CaixaMensagem.info_box("Erro Login", "Usuário Não Cadastrado");
		        	   return null;
		           }
		        } catch (Exception var13) {
		        	CaixaMensagem.info_box("Erro", "Erro ao validar usuário");      
		           var13.printStackTrace();
		           return null;
		        } finally {
		           try {
		              if (ps != null) {
		                 ps.close();
		              }
		  
		              if (conn != null) {
		                 conn.close();
		              }
		           } catch (Exception var12) {
		              var12.printStackTrace();
		           }
		        }
		}
	}
}
