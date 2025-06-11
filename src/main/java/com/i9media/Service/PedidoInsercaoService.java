package com.i9media.Service;

import java.sql.SQLException;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.i9media.models.PIDTO;
import com.i9media.models.PedidoInsercao;

@Service
public class PedidoInsercaoService {
	
	public static boolean piEstaBloqueado(PIDTO pi, String usuarioAtual) {
	    String emEdicaoPor = pi.getEmEdicaoPor();
	    Date edicaoInicio = pi.getEdicaoInicio();

	    // Se ninguém está editando, então não está bloqueado
	    if (emEdicaoPor == null) {
	        return false;
	    }

	    // Se o próprio usuário está editando, não está bloqueado para ele
	    if (emEdicaoPor.equals(usuarioAtual)) {
	        return false;
	    }

	    // Se está em edição por outro, verifica se o tempo expirou (30 minutos)
	    if (edicaoInicio != null) {
	        long diffMs = new Date().getTime() - edicaoInicio.getTime();
	        if (diffMs >= 30 * 60 * 1000) {
	            return false; // Edição expirou
	        } else {
	            return true; // Ainda está bloqueado
	        }
	    }

	    // Se edicaoInicio for null, mas emEdicaoPor está preenchido, assume bloqueado por segurança
	    return true;
	}

    public static boolean tentarBloquearParaEdicao(Integer piId, String usuario) throws SQLException {
        PedidoInsercao pi = PedidoInsercao.buscarPorId(piId);
        if (pi == null) {
            throw new RuntimeException("PI não encontrado");
        }

        if (pi.getEmEdicaoPor() != null && !pi.getEmEdicaoPor().equals(usuario)) {
        	System.out.println("Pi em edição por: "+pi.getEmEdicaoPor());
        	System.out.println("User atual: "+usuario);
            Date agora = new Date();
            long diffMs = agora.getTime() - 
                          (pi.getEdicaoInicio() != null ? pi.getEdicaoInicio().getTime() : 0);
            if (diffMs < 30 * 60 * 1000) {
            	System.out.print("Falso");
                return false;
            }
        }
        pi.setEmEdicaoPor(usuario);
        System.out.print("Usuario para salvar como edição: "+usuario);
        pi.setEdicaoInicio(new Date());
        pi.atualizar();
        return true;
    }

    public static void liberarBloqueio(Integer piId) throws SQLException {
        PedidoInsercao pi = PedidoInsercao.buscarPorId(piId);
        if (pi == null) {
            throw new RuntimeException("PI não encontrado");
        }

        pi.setEmEdicaoPor(null);
        pi.setEdicaoInicio(null);
        pi.atualizar(); 
    }
}