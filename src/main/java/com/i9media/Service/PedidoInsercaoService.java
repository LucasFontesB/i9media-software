package com.i9media.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.sql.Types;

import org.springframework.stereotype.Service;

import com.i9media.Conectar;
import com.i9media.models.PIDTO;
import com.i9media.models.PedidoInsercao;

@Service
public class PedidoInsercaoService {
	
	public static boolean piEstaBloqueado(PIDTO pi, String usuarioAtual) {
	    String emEdicaoPor = pi.getEmEdicaoPor();
	    Date edicaoInicio = pi.getEdicaoInicio();

	    if (emEdicaoPor == null) {
	    	System.out.println("emEdicaoPor é NULL");
	        return false;
	    }

	    if (emEdicaoPor.equals(usuarioAtual)) {
	    	System.out.println("Usuario emEdicaoPor é igual ao usuarioAtual");
	        return false;
	    }

	    if (edicaoInicio != null) {
	        long diffMs = new Date().getTime() - edicaoInicio.getTime();
	        if (diffMs >= 30 * 60 * 1000) {
	        	System.out.println("Agora: " + new Date());
	        	System.out.println("Início: " + edicaoInicio);
	        	System.out.println("Diferença em ms: " + diffMs + " | em minutos: " + (diffMs / 1000 / 60));
	        	System.out.println("Diferença de tempo maior que 30 minutos");
	            return false; 
	        } else {
	        	System.out.println("Diferença de tempo menor que 30 minutos");
	            return true; 
	        }
	    }
	    System.out.println("PI bloqueado");
	    return true;
	}
	
	public static void deletar(Integer piId) throws SQLException {
        String sql = "DELETE FROM pi WHERE id = ?";
        
        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, piId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new SQLException("Nenhum registro encontrado para exclusão com o ID: " + piId);
            }
        }
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
        System.out.println("Salvando EmEdição com o usuario: "+usuario);
        pi.setEmEdicaoPor(usuario);
        System.out.println("Salvando Inicio De Edição: "+new Date());
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
    
    public static void atualizar(PIDTO pi) throws SQLException {
        String sql = "UPDATE pi SET " +
                "cliente_id = ?, agencia_id = ?, veiculo = ?, praca = ?, valorLiquido = ?, repasseVeiculo = ?, imposto = ?, " +
                "bvAgencia = ?, comissaoPercentual = ?, valorComissao = ?, totalLiquido = ?, " +
                "midiaResponsavel = ?, percentualIndicacao = ?, midia = ?, liquidoFinal = ?, " +
                "porcimposto = ?, porcbv = ?, piAgencia = ?, vencimentopiAgencia = ?, " +
                "checkingEnviado = ?, piI9_id = ?, dataPagamentoParaVeiculo = ?, nfVeiculo = ?, executivo_id = ? " +
                "WHERE id = ?";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pi.getClienteId());
            stmt.setInt(2, pi.getAgenciaId());
            stmt.setString(3, pi.getVeiculo());
            stmt.setString(4, pi.getPraca());
            stmt.setBigDecimal(5, pi.getValorLiquido());
            stmt.setBigDecimal(6, pi.getRepasseVeiculo());
            stmt.setBigDecimal(7, pi.getImposto());
            stmt.setBigDecimal(8, pi.getBvAgencia());
            stmt.setBigDecimal(9, pi.getComissaoPercentual());
            stmt.setBigDecimal(10, pi.getValorComissao());
            stmt.setBigDecimal(11, pi.getTotalLiquido());
            stmt.setString(12, pi.getMidiaResponsavel());
            stmt.setBigDecimal(13, pi.getPercentualIndicacao());
            stmt.setBigDecimal(14, pi.getMidia());
            stmt.setBigDecimal(15, pi.getLiquidoFinal());
            stmt.setBigDecimal(16, pi.getPorcImposto());
            stmt.setBigDecimal(17, pi.getPorcBV());
            stmt.setString(18, pi.getPiAgencia());

            if (pi.getVencimentopiAgencia() != null) {
                stmt.setDate(19, new java.sql.Date(pi.getVencimentopiAgencia().getTime()));
            } else {
                stmt.setNull(19, Types.DATE);
            }

            if (pi.getCheckingEnviado() != null) {
                stmt.setDate(20, new java.sql.Date(pi.getCheckingEnviado().getTime()));
            } else {
                stmt.setNull(20, Types.DATE);
            }

            if (pi.getPiI9Id() != null) {
                stmt.setString(21, pi.getPiI9Id());
            } else {
                stmt.setNull(21, Types.INTEGER);
            }

            if (pi.getDataPagamentoParaVeiculo() != null) {
                stmt.setDate(22, new java.sql.Date(pi.getDataPagamentoParaVeiculo().getTime()));
            } else {
                stmt.setNull(22, Types.DATE);
            }

            stmt.setString(23, pi.getNfVeiculo());
            stmt.setInt(24, pi.getExecutivoId());
            stmt.setInt(25, pi.getId());

            stmt.executeUpdate();
        }
    }
}