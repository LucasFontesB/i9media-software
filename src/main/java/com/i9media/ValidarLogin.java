package com.i9media;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ValidarLogin {
    public static Integer Validar(String nome, String senha) {
        if (nome == null || nome.isEmpty() || senha == null || senha.isEmpty()) {
            CaixaMensagem.info_box("Erro Login", "Preencha Todos Os Campos");
            return null;
        } else {
            String sql = "SELECT id FROM usuarios WHERE usuario = ? AND senha = ?";
            try (
                Connection conn = Conectar.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
            ) {
                System.out.println("Conexão estabelecida com sucesso: " + (conn != null));
                ps.setString(1, nome);
                ps.setString(2, senha);
                try (ResultSet resultado = ps.executeQuery()) {
                    if (resultado.next()) {
                        return resultado.getInt("id");
                    } else {
                        CaixaMensagem.info_box("Erro Login", "Usuário Não Cadastrado");
                        return null;
                    }
                }
            } catch (Exception e) {
                CaixaMensagem.info_box("Erro", "Erro ao validar usuário");
                e.printStackTrace();
                return null;
            }
        }
    }
}
