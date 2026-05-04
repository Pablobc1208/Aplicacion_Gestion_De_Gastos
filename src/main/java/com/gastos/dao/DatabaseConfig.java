package com.gastos.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Configuración y conexión a la base de datos SQLite.
 * Se encarga de inicializar las tablas y cargar datos por defecto si es necesario.
 */
public class DatabaseConfig {

    private static final String URL = "jdbc:sqlite:gastos.db";

    /**
     * Obtiene una conexión a la base de datos.
     * @return Connection
     * @throws SQLException Si hay error de conexión
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Inicializa la base de datos creando las tablas si no existen.
     */
    public static void inicializarBaseDeDatos() {
        // SQL para crear las tablas de usuarios y transacciones
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS Usuarios ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT UNIQUE NOT NULL,"
                + "password TEXT NOT NULL,"
                + "rol TEXT NOT NULL"
                + ");";

        String sqlTransacciones = "CREATE TABLE IF NOT EXISTS Transacciones ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "usuario_id INTEGER,"
                + "tipo TEXT NOT NULL,"
                + "categoria TEXT NOT NULL,"
                + "cantidad REAL NOT NULL,"
                + "fecha TEXT NOT NULL,"
                + "FOREIGN KEY(usuario_id) REFERENCES Usuarios(id)"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Ejecutamos los CREATE TABLE
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlTransacciones);
            
            // Si no hay usuarios, cargamos los datos del CSV de prueba
            if (!existenUsuarios(conn)) {
                cargarDatosIniciales(conn);
            }

        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }

    private static boolean existenUsuarios(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM Usuarios";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private static void cargarDatosIniciales(Connection conn) {
        System.out.println("Cargando datos iniciales desde CSV...");
        String sqlUsuario = "INSERT INTO Usuarios(id, username, password, rol) VALUES(?, ?, ?, ?)";
        String sqlTransaccion = "INSERT INTO Transacciones(id, usuario_id, tipo, categoria, cantidad, fecha) VALUES(?, ?, ?, ?, ?, ?)";

        try (InputStream is = DatabaseConfig.class.getResourceAsStream("/datos_iniciales.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is));
             PreparedStatement pstmtUsuario = conn.prepareStatement(sqlUsuario);
             PreparedStatement pstmtTransaccion = conn.prepareStatement(sqlTransaccion)) {

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length > 0) {
                    if (datos[0].equals("U")) {
                        // U,id,username,password,rol
                        pstmtUsuario.setInt(1, Integer.parseInt(datos[1]));
                        pstmtUsuario.setString(2, datos[2]);
                        pstmtUsuario.setString(3, datos[3]);
                        pstmtUsuario.setString(4, datos[4]);
                        pstmtUsuario.executeUpdate();
                    } else if (datos[0].equals("T")) {
                        // T,id,usuario_id,tipo,categoria,cantidad,fecha
                        pstmtTransaccion.setInt(1, Integer.parseInt(datos[1]));
                        pstmtTransaccion.setInt(2, Integer.parseInt(datos[2]));
                        pstmtTransaccion.setString(3, datos[3]);
                        pstmtTransaccion.setString(4, datos[4]);
                        pstmtTransaccion.setDouble(5, Double.parseDouble(datos[5]));
                        pstmtTransaccion.setString(6, datos[6]);
                        pstmtTransaccion.executeUpdate();
                    }
                }
            }
            System.out.println("Datos iniciales cargados con éxito.");

        } catch (Exception e) {
            System.err.println("Error al cargar datos iniciales: " + e.getMessage());
        }
    }
}
