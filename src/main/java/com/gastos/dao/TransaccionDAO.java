package com.gastos.dao;

import com.gastos.model.Transaccion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla Transacciones.
 */
public class TransaccionDAO {

    public List<Transaccion> obtenerTodas() {
        // Hago un JOIN para sacar el nombre del usuario junto con el gasto
        String query = "SELECT t.id, t.usuario_id, u.username, t.tipo, t.categoria, t.cantidad, t.fecha " +
                "FROM Transacciones t JOIN Usuarios u ON t.usuario_id = u.id";
        return obtenerListaTransacciones(query, null);
    }

    /**
     * Obtiene las transacciones de un usuario específico.
     * 
     * @param usuarioId ID del usuario
     */
    public List<Transaccion> obtenerPorUsuario(int usuarioId) {
        String query = "SELECT t.id, t.usuario_id, u.username, t.tipo, t.categoria, t.cantidad, t.fecha " +
                "FROM Transacciones t JOIN Usuarios u ON t.usuario_id = u.id " +
                "WHERE t.usuario_id = ?";
        return obtenerListaTransacciones(query, usuarioId);
    }

    private List<Transaccion> obtenerListaTransacciones(String query, Integer parametroId) {
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
                            rs.getString("username"),
                            rs.getString("tipo"),
                            rs.getString("categoria"),
                            rs.getDouble("cantidad"),
                            rs.getString("fecha")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener transacciones: " + e.getMessage());
        }
        return lista;
    }

    public boolean anadirTransaccion(Transaccion t) {
        // Query para insertar la nueva transacción en la tabla
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
     * 
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
