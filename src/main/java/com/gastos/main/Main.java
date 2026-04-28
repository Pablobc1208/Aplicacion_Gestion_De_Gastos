package com.gastos.main;

import com.gastos.dao.DatabaseConfig;
import com.gastos.view.LoginFrame;

import javax.swing.SwingUtilities;

/**
 * Clase principal que arranca la aplicación.
 */
public class Main {
    public static void main(String[] args) {
        // Inicializar la base de datos (crea tablas e inserta datos si no existen)
        System.out.println("Inicializando la base de datos...");
        DatabaseConfig.inicializarBaseDeDatos();

        // Configurar el Look and Feel FlatLaf para un diseño moderno
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel FlatLaf.");
        }

        // Lanzar la interfaz gráfica en el hilo de despacho de eventos
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
