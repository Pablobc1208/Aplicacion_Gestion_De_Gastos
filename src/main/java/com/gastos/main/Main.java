package com.gastos.main;

import com.gastos.dao.DatabaseConfig;
import com.gastos.view.LoginFrame;

import javax.swing.*;

/**
 * Clase principal que arranca la aplicación.
 */
public class Main {
    public static void main(String[] args) {
        // Inicializar la base de datos (crea tablas e inserta datos si no existen)
        System.out.println("Inicializando la base de datos...");
        DatabaseConfig.inicializarBaseDeDatos();

        // Configurar el Look and Feel para que se vea más nativo/bonito
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel del sistema.");
        }

        // Lanzar la interfaz gráfica en el hilo de despacho de eventos
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
