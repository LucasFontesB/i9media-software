package com.i9media;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DB {
	
	public static String BuscarImposto() {
	    String sql = "SELECT valor FROM imposto WHERE id = ?";

	    try (Connection conn = Conectar.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, 1);
	        try (ResultSet rs = stmt.executeQuery()) { 
	            if (rs.next()) {
	                return rs.getString("valor");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "0";
	}
}
