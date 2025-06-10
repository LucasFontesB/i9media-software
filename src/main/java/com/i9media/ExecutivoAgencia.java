package com.i9media;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExecutivoAgencia {

    public static boolean vincularExecutivoAgencia(int executivoId, int agenciaId) throws SQLException {
        String sqlCheck = "SELECT COUNT(*) FROM executivo_agencia WHERE executivo_id = ? AND agencia_id = ?";
        String sqlInsert = "INSERT INTO executivo_agencia (executivo_id, agencia_id) VALUES (?, ?)";

        try (
            Connection conn = Conectar.getConnection();
            PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
            PreparedStatement psInsert = conn.prepareStatement(sqlInsert)
        ) {
            psCheck.setInt(1, executivoId);
            psCheck.setInt(2, agenciaId);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    psInsert.setInt(1, executivoId);
                    psInsert.setInt(2, agenciaId);
                    psInsert.executeUpdate();
                    return true;
                }
            }
        }
        return false;
    }
}
