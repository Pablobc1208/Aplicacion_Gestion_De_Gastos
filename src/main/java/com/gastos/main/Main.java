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

        // Uso FlatLaf para que la aplicación no parezca de los años 90 (diseño oscuro)
        try {
            System.out.println("Configurando tema oscuro...");
            boolean setupOk = com.formdev.flatlaf.FlatDarkLaf.setup();
            if (!setupOk) {
                System.err.println("FlatDarkLaf.setup() devolvió false, intentando método alternativo...");
                javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            }
            
            // Pongo los bordes un poco redondeados para que se vea más moderno
            javax.swing.UIManager.put("Button.arc", 15);
            javax.swing.UIManager.put("Component.arc", 15);
            javax.swing.UIManager.put("ProgressBar.arc", 15);
            javax.swing.UIManager.put("TextComponent.arc", 15);
            
            // Colores más modernos para Focus y selección
            javax.swing.UIManager.put("Component.focusWidth", 2);
            javax.swing.UIManager.put("Component.innerFocusWidth", 1);
            
            System.out.println("Tema configurado con éxito.");
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel FlatLaf: " + e.getMessage());
            e.printStackTrace();
        }

        // Lanzar la interfaz gráfica en el hilo de despacho de eventos
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
