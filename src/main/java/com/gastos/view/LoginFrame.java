package com.gastos.view;

import com.gastos.dao.UsuarioDAO;
import com.gastos.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana de inicio de sesión de la aplicación.
 */
public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private UsuarioDAO usuarioDAO;

    public LoginFrame() {
        usuarioDAO = new UsuarioDAO();

        setTitle("Control de Gastos Estudiantil - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255)); // Alice Blue

        // Panel superior para el título/logo
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Control de Gastos");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(25, 25, 112)); // Midnight Blue
        topPanel.add(lblTitle);
        // Espacio para logo (opcional)
        // JLabel lblLogo = new JLabel(new ImageIcon("path/to/logo.png"));
        // topPanel.add(lblLogo);

        // Panel central para el formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblUsername = new JLabel("Usuario:");
        lblUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsername = new JTextField(15);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword = new JPasswordField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lblUsername, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(lblPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(txtPassword, gbc);

        // Panel inferior para el botón
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(70, 130, 180)); // Steel Blue
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticarUsuario();
            }
        });
        
        bottomPanel.add(btnLogin);

        // Añadir paneles al frame
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void autenticarUsuario() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, introduzca usuario y contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(username, password);
        if (usuario != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido " + usuario.getUsername() + " (" + usuario.getRol() + ")", "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);
            
            // Abrir Dashboard
            DashboardFrame dashboard = new DashboardFrame(usuario);
            dashboard.setVisible(true);
            
            // Cerrar Login
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Login", JOptionPane.ERROR_MESSAGE);
        }
    }
}
