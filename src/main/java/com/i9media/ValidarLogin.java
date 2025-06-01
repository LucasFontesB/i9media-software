package com.i9media;

public class ValidarLogin {
	public static boolean Validar(String nome, String senha) {
		if(nome == null || nome.isEmpty() || senha == null || senha.isEmpty()) {
			CaixaMensagem.info_box("Erro Login", "Preencha Todos Os Campos");
			return false;
		}else {
			return true;
		}
	}
}
