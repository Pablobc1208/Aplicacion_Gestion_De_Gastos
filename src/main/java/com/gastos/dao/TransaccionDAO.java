package com.gastos.dao;

import com.gastos.model.Transaccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla Transacciones.
 */
public class TransaccionDAO {

    /**
     * Obtiene todas las transacciones (para el Admin).
     */
    public List<Transaccion> obtenerTodas() {
        String query = "SELECT id, usuario_id, tipo, categoria, cantidad, fecha FROM Transacciones";
        return obtenerTransacciones(query, null);
    }

    /**
     * Obtiene las transacciones de un usuario específico.
     * @param usuarioId ID del usuario
     */
    public List<Transaccion> obtenerPorUsuario(int usuarioId) {
        String query = "SELECT id, usuario_id, tipo, categoria, cantidad, fecha FROM Transacciones WHERE usuario_id = ?";
        return obtenerTransacciones(query, usuarioId);
    }

    private List<Transaccion> obtenerTransacciones(String query, Integer parametroId) {
        List<Transaccion> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            if (parametroId != null) {
                pstmt.setInt(1, parametroId);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Transaccion(
                            rs.getInt("id"),
                            rs.getInt("usuario_id"),
                            rs.getString("tipo"),
                            rs.getString("categoria"),
                            rs.getDouble("cantidad"),
                            rs.getString("fecha")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener transacciones: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Añade una nueva transacción.
     * @param t Objeto Transaccion
     * @return true si se añadió con éxito
     */
    public boolean anadirTransaccion(Transaccion t) {
        String query = "INSERT INTO Transacciones (usuario_id, tipo, categoria, cantidad, fecha) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, t.getUsuarioId());
            pstmt.setString(2, t.getTipo());
            pstmt.setString(3, t.getCategoria());
            pstmt.setDouble(4, t.getCantidad());
            pstmt.setString(5, t.getFecha());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al añadir transacción: " + e.getMessage());
        }
        return false;
    }

    /**
     * Borra una transacción por su ID.
     * @param id ID de la transacción
     * @return true si se borró con éxito
     */
    public boolean borrarTransaccion(int id) {
        String query = "DELETE FROM Transacciones WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al borrar transacción: " + e.getMessage());
        }
        return false;
    }
}
