package com.gastos.view;

import com.gastos.dao.TransaccionDAO;
import com.gastos.model.Transaccion;
import com.gastos.model.Usuario;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal del Dashboard.
 */
public class DashboardFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private Usuario usuarioActual;
    private TransaccionDAO transaccionDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblBalance;
    private JLabel lblWelcome;
    private JPanel chartPanelContainer;

    // Campos del formulario
    private JComboBox<String> cbTipo;
    private JTextField txtCategoria;
    private JTextField txtCantidad;

    // Filtros
    private JTextField txtFiltroCategoria;
    private JTextField txtFiltroUsuario;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JLabel lblTotalIngresos;
    private JLabel lblTotalGastos;
    private JLabel lblTotalBalance;
    private TableRowSorter<DefaultTableModel> sorter;

    public DashboardFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        this.transaccionDAO = new TransaccionDAO();

        setTitle("Dashboard - Control de Gastos (" + usuarioActual.getRol() + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Confirmar antes de salir
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });

        // Layout Principal
        setLayout(new BorderLayout(10, 10));
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(contentPane, BorderLayout.CENTER);

        // --- Panel Superior ---
        JPanel topPanel = new JPanel(new BorderLayout());
        lblWelcome = new JLabel("Bienvenido, " + usuarioActual.getUsername());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));

        lblBalance = new JLabel("Balance: 0.0 €");
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBalance.setForeground(new Color(0, 128, 0)); // Dark green

        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setBackground(new Color(70, 130, 180)); // Steel Blue
        btnLogout.setForeground(Color.BLACK); // Texto en negro para legibilidad
        btnLogout.addActionListener(e -> cerrarSesion());

        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> salir());

        topPanel.add(lblWelcome, BorderLayout.WEST);
        
        lblBalance.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(lblBalance, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(btnLogout);
        buttonsPanel.add(btnSalir);
        topPanel.add(buttonsPanel, BorderLayout.EAST);
        contentPane.add(topPanel, BorderLayout.NORTH);

        // --- Panel Central (Tabla y Filtros) ---
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        // Filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filtrar por Categoría:"));
        txtFiltroCategoria = new JTextField(15);
        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> aplicarFiltro());
        JButton btnLimpiarFiltro = new JButton("Limpiar");
        btnLimpiarFiltro.addActionListener(e -> {
            txtFiltroCategoria.setText("");
            if (txtFiltroUsuario != null)
                txtFiltroUsuario.setText("");
            txtFechaInicio.setText("");
            txtFechaFin.setText("");
            aplicarFiltro();
        });
        filterPanel.add(txtFiltroCategoria);

        if ("Administrador".equals(usuarioActual.getRol())) {
            filterPanel.add(new JLabel("  Usuario:"));
            txtFiltroUsuario = new JTextField(10);
            filterPanel.add(txtFiltroUsuario);
        }

        filterPanel.add(new JLabel("  Desde (dd-MM-yyyy):"));
        txtFechaInicio = new JTextField(8);
        filterPanel.add(txtFechaInicio);

        filterPanel.add(new JLabel("  Hasta (dd-MM-yyyy):"));
        txtFechaFin = new JTextField(8);
        filterPanel.add(txtFechaFin);

        filterPanel.add(btnFiltrar);
        filterPanel.add(btnLimpiarFiltro);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // Tabla
        String[] columnNames = { "ID", "Usuario", "Tipo", "Categoría", "Cantidad", "Fecha", "u_id" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        // Ocultar la columna u_id (índice 6)
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // --- Panel Derecho (Gráfica) ---
        chartPanelContainer = new JPanel(new BorderLayout());
        chartPanelContainer.setPreferredSize(new Dimension(350, 0));
        contentPane.add(chartPanelContainer, BorderLayout.EAST);

        // --- Panel Inferior (Formulario y Resumen) ---
        JPanel bottomContainer = new JPanel(new BorderLayout());

        // Resumen
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel cardIngresos = crearTarjeta("Total Ingresos", "0.0 €", new Color(200, 255, 200));
        lblTotalIngresos = (JLabel) cardIngresos.getComponent(1);

        JPanel cardGastos = crearTarjeta("Total Gastos", "0.0 €", new Color(255, 200, 200));
        lblTotalGastos = (JLabel) cardGastos.getComponent(1);

        JPanel cardBalance = crearTarjeta("Balance Actual", "0.0 €", new Color(200, 230, 255));
        lblTotalBalance = (JLabel) cardBalance.getComponent(1);

        summaryPanel.add(cardIngresos);
        summaryPanel.add(cardGastos);
        summaryPanel.add(cardBalance);
        bottomContainer.add(summaryPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Añadir / Eliminar Transacción"));

        bottomPanel.add(new JLabel("Tipo:"));
        cbTipo = new JComboBox<>(new String[] { "Ingreso", "Gasto" });
        bottomPanel.add(cbTipo);

        bottomPanel.add(new JLabel("Categoría:"));
        txtCategoria = new JTextField(10);
        bottomPanel.add(txtCategoria);

        bottomPanel.add(new JLabel("Cantidad:"));
        txtCantidad = new JTextField(8);
        bottomPanel.add(txtCantidad);

        JButton btnAdd = new JButton("Añadir");
        btnAdd.addActionListener(e -> anadirTransaccion());
        bottomPanel.add(btnAdd);

        JButton btnDelete = new JButton("Borrar Seleccionada");
        btnDelete.addActionListener(e -> borrarTransaccion());
        bottomPanel.add(btnDelete);

        bottomContainer.add(bottomPanel, BorderLayout.CENTER);
        contentPane.add(bottomContainer, BorderLayout.SOUTH);

        // Cargar Datos
        cargarDatos();
    }

    private JPanel crearTarjeta(String titulo, String valor, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(color);
        p.setBorder(BorderFactory.createLineBorder(color.darker(), 1));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValor.setName(titulo); // Para identificarla luego

        p.add(lblTitulo, BorderLayout.NORTH);
        p.add(lblValor, BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(150, 80));
        return p;
    }

    private void cargarDatos() {
        tableModel.setRowCount(0);
        List<Transaccion> transacciones;

        if ("Administrador".equals(usuarioActual.getRol())) {
            transacciones = transaccionDAO.obtenerTodas();
        } else {
            transacciones = transaccionDAO.obtenerPorUsuario(usuarioActual.getId());
        }

        double ingresos = 0;
        double gastos = 0;
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        for (Transaccion t : transacciones) {
            // Convertir fecha de yyyy-MM-dd (DB) a dd-MM-yyyy (UI) si es necesario
            String fechaUI = t.getFecha();
            try {
                if (fechaUI.contains("-") && fechaUI.indexOf("-") == 4) {
                    Date d = new SimpleDateFormat("yyyy-MM-dd").parse(fechaUI);
                    fechaUI = new SimpleDateFormat("dd-MM-yyyy").format(d);
                }
            } catch (Exception e) {}

            Object[] row = {
                    t.getId(),
                    t.getUsername(),
                    t.getTipo(),
                    t.getCategoria(),
                    t.getCantidad(),
                    fechaUI,
                    t.getUsuarioId()
            };
            tableModel.addRow(row);

            if ("Ingreso".equals(t.getTipo())) {
                ingresos += t.getCantidad();
            } else {
                gastos += t.getCantidad();
                // Acumular en el dataset para el gráfico de gastos (Evitar UnknownKeyException)
                int index = -1;
                try {
                    index = dataset.getIndex(t.getCategoria());
                } catch (Exception ex) {
                    index = -1;
                }

                if (index == -1) {
                    dataset.setValue(t.getCategoria(), t.getCantidad());
                } else {
                    Number actual = dataset.getValue(index);
                    dataset.setValue(t.getCategoria(), actual.doubleValue() + t.getCantidad());
                }
            }
        }

        actualizarBalance(ingresos, gastos);
        actualizarGrafica(dataset);
    }

    private void actualizarBalance(double ingresos, double gastos) {
        double balance = ingresos - gastos;
        lblBalance.setText(String.format("Balance: %.2f €", balance));
        lblTotalIngresos.setText(String.format("%.2f €", ingresos));
        lblTotalGastos.setText(String.format("%.2f €", gastos));
        lblTotalBalance.setText(String.format("%.2f €", balance));

        if (balance < 0) {
            lblBalance.setForeground(Color.RED);
            lblTotalBalance.setForeground(Color.RED);
        } else {
            lblBalance.setForeground(new Color(0, 128, 0));
            lblTotalBalance.setForeground(new Color(0, 128, 128));
        }
    }

    private void actualizarGrafica(DefaultPieDataset<String> dataset) {
        chartPanelContainer.removeAll();
        if (dataset.getItemCount() > 0) {
            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Gastos por Categoría",
                    dataset,
                    true, true, false);
            ChartPanel chartPanel = new ChartPanel(pieChart);
            chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
        } else {
            chartPanelContainer.add(new JLabel("No hay gastos para mostrar", SwingConstants.CENTER),
                    BorderLayout.CENTER);
        }
        chartPanelContainer.revalidate();
        chartPanelContainer.repaint();
    }

    private void aplicarFiltro() {
        String cat = txtFiltroCategoria.getText().trim();
        String user = (txtFiltroUsuario != null) ? txtFiltroUsuario.getText().trim() : "";
        String inicio = txtFechaInicio.getText().trim();
        String fin = txtFechaFin.getText().trim();

        List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();

        if (!cat.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + cat, 3));
        }

        if (!user.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + user, 1));
        }

        if (!inicio.isEmpty() || !fin.isEmpty()) {
            final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            filters.add(new RowFilter<DefaultTableModel, Object>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                    String fechaStr = (String) entry.getValue(5);
                    try {
                        Date fechaFila = sdf.parse(fechaStr);
                        if (!inicio.isEmpty()) {
                            Date dInicio = sdf.parse(inicio);
                            if (fechaFila.before(dInicio)) return false;
                        }
                        if (!fin.isEmpty()) {
                            Date dFin = sdf.parse(fin);
                            if (fechaFila.after(dFin)) return false;
                        }
                    } catch (Exception e) {
                        return false;
                    }
                    return true;
                }
            });
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void anadirTransaccion() {
        String tipo = (String) cbTipo.getSelectedItem();
        String categoria = txtCategoria.getText().trim();
        String cantidadStr = txtCantidad.getText().trim();

        if (categoria.isEmpty() || cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe rellenar categoría y cantidad.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double cantidad = Double.parseDouble(cantidadStr);
            String fecha = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            Transaccion t = new Transaccion(0, usuarioActual.getId(), usuarioActual.getUsername(), tipo, categoria,
                    cantidad, fecha);
            if (transaccionDAO.anadirTransaccion(t)) {
                txtCategoria.setText("");
                txtCantidad.setText("");
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la transacción.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrarTransaccion() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fila de la tabla.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convertir índice por si hay filtros aplicados
        int modelRow = table.convertRowIndexToModel(selectedRow);

        int idTransaccion = (int) tableModel.getValueAt(modelRow, 0);
        int idUsuarioTrans = (int) tableModel.getValueAt(modelRow, 6);

        // Validación de seguridad (por si acaso)
        if ("Estudiante".equals(usuarioActual.getRol()) && usuarioActual.getId() != idUsuarioTrans) {
            JOptionPane.showMessageDialog(this, "No tiene permisos para borrar esta transacción.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de borrar la transacción ID " + idTransaccion + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            if (transaccionDAO.borrarTransaccion(idTransaccion)) {
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al borrar la transacción.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cerrarSesion() {
        int conf = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas cerrar sesión?", "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }

    private void salir() {
        ImageIcon icon = null;
        try {
            ImageIcon original = new ImageIcon(getClass().getResource("/logo.png"));
            Image img = original.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono: " + e.getMessage());
        }

        Object[] options = { "Sí, salir", "Cancelar" };
        int n = JOptionPane.showOptionDialog(this,
                "¿Estás seguro de que deseas salir de la aplicación?",
                "Confirmar Salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                icon,
                options,
                options[0]);

        if (n == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Gracias por usar Control de Gastos Estudiantil.\n¡Que tengas un gran día!",
                    "Despedida",
                    JOptionPane.INFORMATION_MESSAGE,
                    icon);
            System.exit(0);
        }
    }
}
