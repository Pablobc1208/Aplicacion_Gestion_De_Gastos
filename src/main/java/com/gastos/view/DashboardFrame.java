package com.gastos.view;

import com.gastos.dao.TransaccionDAO;
import com.gastos.model.Transaccion;
import com.gastos.model.Usuario;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Ventana principal del Dashboard.
 */
public class DashboardFrame extends JFrame {

    private Usuario usuarioActual;
    private TransaccionDAO transaccionDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblBalance;
    private JPanel chartPanelContainer;

    // Campos del formulario
    private JComboBox<String> cbTipo;
    private JTextField txtCategoria;
    private JTextField txtCantidad;

    // Filtros
    private JTextField txtFiltroCategoria;
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
        JLabel lblWelcome = new JLabel("Bienvenido, " + usuarioActual.getUsername());
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        
        lblBalance = new JLabel("Balance: 0.0 €");
        lblBalance.setFont(new Font("Arial", Font.BOLD, 18));
        lblBalance.setForeground(new Color(0, 128, 0)); // Dark green

        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> salir());

        topPanel.add(lblWelcome, BorderLayout.WEST);
        topPanel.add(lblBalance, BorderLayout.CENTER);
        topPanel.add(btnSalir, BorderLayout.EAST);
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
            aplicarFiltro();
        });
        filterPanel.add(txtFiltroCategoria);
        filterPanel.add(btnFiltrar);
        filterPanel.add(btnLimpiarFiltro);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // Tabla
        String[] columnNames = {"ID", "Usuario ID", "Tipo", "Categoría", "Cantidad", "Fecha"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        // --- Panel Derecho (Gráfica) ---
        chartPanelContainer = new JPanel(new BorderLayout());
        chartPanelContainer.setPreferredSize(new Dimension(350, 0));
        contentPane.add(chartPanelContainer, BorderLayout.EAST);

        // --- Panel Inferior (Formulario CRUD) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Añadir / Eliminar Transacción"));

        bottomPanel.add(new JLabel("Tipo:"));
        cbTipo = new JComboBox<>(new String[]{"Ingreso", "Gasto"});
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

        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        // Cargar Datos
        cargarDatos();
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
        DefaultPieDataset dataset = new DefaultPieDataset();

        for (Transaccion t : transacciones) {
            Object[] row = {
                    t.getId(),
                    t.getUsuarioId(),
                    t.getTipo(),
                    t.getCategoria(),
                    t.getCantidad(),
                    t.getFecha()
            };
            tableModel.addRow(row);

            if ("Ingreso".equals(t.getTipo())) {
                ingresos += t.getCantidad();
            } else {
                gastos += t.getCantidad();
                // Acumular en el dataset para el gráfico de gastos
                Number actual = dataset.getValue(t.getCategoria());
                if (actual == null) {
                    dataset.setValue(t.getCategoria(), t.getCantidad());
                } else {
                    dataset.setValue(t.getCategoria(), actual.doubleValue() + t.getCantidad());
                }
            }
        }

        actualizarBalance(ingresos, gastos);
        actualizarGrafica(dataset);
    }

    private void actualizarBalance(double ingresos, double gastos) {
        double balance = ingresos - gastos;
        lblBalance.setText(String.format("Balance: %.2f € (Ingresos: %.2f | Gastos: %.2f)", balance, ingresos, gastos));
        if (balance < 0) {
            lblBalance.setForeground(Color.RED);
        } else {
            lblBalance.setForeground(new Color(0, 128, 0));
        }
    }

    private void actualizarGrafica(DefaultPieDataset dataset) {
        chartPanelContainer.removeAll();
        if (dataset.getItemCount() > 0) {
            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Gastos por Categoría",
                    dataset,
                    true, true, false);
            ChartPanel chartPanel = new ChartPanel(pieChart);
            chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
        } else {
            chartPanelContainer.add(new JLabel("No hay gastos para mostrar", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        chartPanelContainer.revalidate();
        chartPanelContainer.repaint();
    }

    private void aplicarFiltro() {
        String texto = txtFiltroCategoria.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Filtrar por la columna de Categoría (índice 3)
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 3));
        }
    }

    private void anadirTransaccion() {
        String tipo = (String) cbTipo.getSelectedItem();
        String categoria = txtCategoria.getText().trim();
        String cantidadStr = txtCantidad.getText().trim();

        if (categoria.isEmpty() || cantidadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe rellenar categoría y cantidad.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double cantidad = Double.parseDouble(cantidadStr);
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            Transaccion t = new Transaccion(0, usuarioActual.getId(), tipo, categoria, cantidad, fecha);
            if (transaccionDAO.anadirTransaccion(t)) {
                txtCategoria.setText("");
                txtCantidad.setText("");
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la transacción.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrarTransaccion() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fila de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convertir índice por si hay filtros aplicados
        int modelRow = table.convertRowIndexToModel(selectedRow);
        
        int idTransaccion = (int) tableModel.getValueAt(modelRow, 0);
        int idUsuarioTrans = (int) tableModel.getValueAt(modelRow, 1);

        // Validación de seguridad (por si acaso)
        if ("Estudiante".equals(usuarioActual.getRol()) && usuarioActual.getId() != idUsuarioTrans) {
            JOptionPane.showMessageDialog(this, "No tiene permisos para borrar esta transacción.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this, "¿Está seguro de borrar la transacción ID " + idTransaccion + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            if (transaccionDAO.borrarTransaccion(idTransaccion)) {
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al borrar la transacción.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void salir() {
        JOptionPane.showMessageDialog(this, "Gracias por usar Control de Gastos Estudiantil. ¡Hasta pronto!", "Despedida", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
}
