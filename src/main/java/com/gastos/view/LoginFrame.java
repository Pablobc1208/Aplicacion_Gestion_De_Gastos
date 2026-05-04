package com.gastos.view;

import com.gastos.dao.UsuarioDAO;
import com.gastos.model.Usuario;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Color;
import java.awt.Image;

/**
 * Ventana de inicio de sesión de la aplicación.
 */
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private UsuarioDAO usuarioDAO;

    public LoginFrame() {
        usuarioDAO = new UsuarioDAO();

        setTitle("Control de Gastos Estudiantil - Login");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con BorderLayout y un poco de margen exterior
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel superior para el título/logo
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.setOpaque(false);
        
        // Logo
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/logo.png"));
            Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(img));
            topPanel.add(lblLogo);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }

        JLabel lblTitle = new JLabel("Control de Gastos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        // Añado el título al panel de arriba
        topPanel.add(lblTitle);

        // Panel central para el formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblUsername = new JLabel("Usuario:");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new java.awt.Dimension(200, 35));

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new java.awt.Dimension(200, 35));

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lblUsername, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(lblPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(txtPassword, gbc);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setOpaque(false);
        
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setBackground(new Color(41, 128, 185)); // Azul brillante moderno
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new java.awt.Dimension(140, 35));
        
        JButton btnRegister = new JButton("Registrarse");
        btnRegister.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnRegister.setFocusPainted(false);
        btnRegister.setPreferredSize(new java.awt.Dimension(120, 35));
        
        btnLogin.addActionListener(e -> autenticarUsuario());
        btnRegister.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            this.dispose();
        });

        // Añado que se pueda pulsar Enter para entrar sin tener que darle al botón
        java.awt.event.ActionListener enterAction = e -> autenticarUsuario();
        txtUsername.addActionListener(enterAction);
        txtPassword.addActionListener(enterAction);
        
        bottomPanel.add(btnLogin);
        bottomPanel.add(btnRegister);

        // Añadir paneles al frame
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Envolver formPanel en un panel que lo centre verticalmente
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(formPanel);
        mainPanel.add(wrapper, BorderLayout.CENTER);
        
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
            
            // Si el login es correcto, abro el Dashboard y cierro esta ventana
            DashboardFrame dashboard = new DashboardFrame(usuario);
            dashboard.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Login", JOptionPane.ERROR_MESSAGE);
        }
    }
}
