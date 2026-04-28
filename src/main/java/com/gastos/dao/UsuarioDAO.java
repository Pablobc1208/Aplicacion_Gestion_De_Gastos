package com.gastos.dao;

import com.gastos.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object para la tabla Usuarios.
 */
public class UsuarioDAO {

    /**
     * Autentica a un usuario por username y password.
     * @param username El nombre de usuario
     * @param password La contraseña
     * @return Objeto Usuario si las credenciales son correctas, null en caso contrario
     */
    public Usuario autenticar(String username, String password) {
        String query = "SELECT id, username, password, rol FROM Usuarios WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("rol")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene un usuario por su ID.
     */
    public Usuario obtenerPorId(int id) {
        String query = "SELECT id, username, password, rol FROM Usuarios WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("rol")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
        }
        return null;
    }
}
