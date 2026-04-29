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

        // Configurar el Look and Feel FlatLaf para un diseño moderno oscuro
        try {
            com.formdev.flatlaf.FlatDarkLaf.setup();
            
            // Configurar propiedades globales para un aspecto más premium
            javax.swing.UIManager.put( "Button.arc", 15 );
            javax.swing.UIManager.put( "Component.arc", 15 );
            javax.swing.UIManager.put( "ProgressBar.arc", 15 );
            javax.swing.UIManager.put( "TextComponent.arc", 15 );
            
            // Colores más modernos para Focus y selección
            javax.swing.UIManager.put( "Component.focusWidth", 2 );
            javax.swing.UIManager.put( "Component.innerFocusWidth", 1 );
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
