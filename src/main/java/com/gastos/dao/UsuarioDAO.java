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
     * 
     * @param username El nombre de usuario
     * @param password La contraseña
     * @return Objeto Usuario si las credenciales son correctas, null en caso
     *         contrario
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
                            rs.getString("rol"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Registra un nuevo usuario con el rol por defecto 'estudiante'.
     * 
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return true si se registró con éxito, false en caso contrario
     */
    public boolean registrarUsuario(String username, String password) {
        String query = "INSERT INTO Usuarios (username, password, rol) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, "estudiante"); // Rol por defecto

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Comprueba si ya existe un usuario con el mismo nombre.
     * 
     * @param username El nombre de usuario a comprobar
     * @return true si existe, false en caso contrario
     */
    public boolean existeUsuario(String username) {
        String query = "SELECT COUNT(*) FROM Usuarios WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al comprobar existencia de usuario: " + e.getMessage());
        }
        return false;
    }

}
