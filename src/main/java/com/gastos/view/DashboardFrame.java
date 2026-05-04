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
        lblBalance.setForeground(new Color(46, 204, 113)); // Bright green

        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setBackground(new Color(231, 76, 60)); // Alizarin Red
        btnLogout.setForeground(Color.WHITE); 
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setFocusPainted(false);
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

        // Pongo los filtros en dos filas porque en una sola no cabían los botones
        JPanel filterPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Categoría:"));
        txtFiltroCategoria = new JTextField(12);
        row1.add(txtFiltroCategoria);

        if ("Administrador".equals(usuarioActual.getRol())) {
            row1.add(new JLabel("  Usuario:"));
            txtFiltroUsuario = new JTextField(10);
            row1.add(txtFiltroUsuario);
        }

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Desde:"));
        txtFechaInicio = new JTextField(8);
        row2.add(txtFechaInicio);
        row2.add(new JLabel("  Hasta:"));
        txtFechaFin = new JTextField(8);
        row2.add(txtFechaFin);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setBackground(new Color(52, 152, 219));
        btnFiltrar.setForeground(Color.WHITE);
        btnFiltrar.addActionListener(e -> aplicarFiltro());

        JButton btnLimpiarFiltro = new JButton("Limpiar");
        btnLimpiarFiltro.addActionListener(e -> {
            txtFiltroCategoria.setText("");
            if (txtFiltroUsuario != null) txtFiltroUsuario.setText("");
            txtFechaInicio.setText("");
            txtFechaFin.setText("");
            aplicarFiltro();
        });

        row2.add(new JLabel("  ")); // Espaciador
        row2.add(btnFiltrar);
        row2.add(btnLimpiarFiltro);

        filterPanel.add(row1);
        filterPanel.add(row2);

        // Añadir soporte para "Enter" en los campos de texto
        java.awt.event.ActionListener enterAction = e -> aplicarFiltro();
        txtFiltroCategoria.addActionListener(enterAction);
        if (txtFiltroUsuario != null) txtFiltroUsuario.addActionListener(enterAction);
        txtFechaInicio.addActionListener(enterAction);
        txtFechaFin.addActionListener(enterAction);

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
        // Oculto la columna u_id porque el usuario no necesita verla, solo sirve para programar
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
        // Le doy más anchura para que los textos de la gráfica no se corten
        chartPanelContainer.setPreferredSize(new Dimension(450, 0));
        contentPane.add(chartPanelContainer, BorderLayout.EAST);

        // --- Panel Inferior (Formulario y Resumen) ---
        JPanel bottomContainer = new JPanel(new BorderLayout());

        // Resumen
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel cardIngresos = crearTarjeta("Total Ingresos", "0.0 €", new Color(30, 62, 43), new Color(46, 204, 113));
        lblTotalIngresos = (JLabel) cardIngresos.getComponent(1);

        JPanel cardGastos = crearTarjeta("Total Gastos", "0.0 €", new Color(74, 35, 35), new Color(231, 76, 60));
        lblTotalGastos = (JLabel) cardGastos.getComponent(1);

        JPanel cardBalance = crearTarjeta("Balance Actual", "0.0 €", new Color(27, 54, 93), new Color(52, 152, 219));
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

    private JPanel crearTarjeta(String titulo, String valor, Color bgColor, Color fgColor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bgColor);
        p.setBorder(BorderFactory.createLineBorder(fgColor, 2, true));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(fgColor);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValor.setForeground(Color.WHITE);
        lblValor.setName(titulo); // Para identificarla luego

        p.add(lblTitulo, BorderLayout.NORTH);
        p.add(lblValor, BorderLayout.CENTER);
        p.setPreferredSize(new Dimension(150, 90));
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
            lblBalance.setForeground(new Color(231, 76, 60)); // Rojo si estamos en números rojos
        } else {
            lblBalance.setForeground(new Color(46, 204, 113)); // Verde si hay dinero
        }
    }

    private void actualizarGrafica(DefaultPieDataset<String> dataset) {
        chartPanelContainer.removeAll();
        if (dataset.getItemCount() > 0) {
            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Gastos por Categoría",
                    dataset,
                    true, true, false);
            
            // Pongo el fondo transparente y las letras en blanco para que pegue con el diseño oscuro
            pieChart.setBackgroundPaint(new Color(0, 0, 0, 0)); 
            pieChart.getTitle().setPaint(Color.WHITE);
            pieChart.getLegend().setBackgroundPaint(new Color(0, 0, 0, 0));
            pieChart.getLegend().setItemPaint(Color.WHITE);
            
            org.jfree.chart.plot.PiePlot<?> plot = (org.jfree.chart.plot.PiePlot<?>) pieChart.getPlot();
            plot.setBackgroundPaint(new Color(0, 0, 0, 0));
            plot.setOutlinePaint(null);
            plot.setLabelBackgroundPaint(new Color(40, 40, 40));
            plot.setLabelPaint(Color.WHITE);
            plot.setLabelShadowPaint(null);
            plot.setLabelOutlinePaint(null);
            
            // Con esto evitamos que las palabras se corten feo y salgan más simples
            plot.setSimpleLabels(true);
            plot.setInteriorGap(0.05); // Dejo un poco de hueco para los textos largos
            
            // Llamo a mi método para que cada categoría tenga su color chulo
            configurarColores(plot, dataset);

            ChartPanel chartPanel = new ChartPanel(pieChart);
            chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
        } else {
            chartPanelContainer.add(new JLabel("No hay gastos para mostrar", SwingConstants.CENTER),
                    BorderLayout.CENTER);
        }
        chartPanelContainer.revalidate();
        chartPanelContainer.repaint();
    }

    // Método para controlar los colores de la gráfica
    private void configurarColores(org.jfree.chart.plot.PiePlot<?> plot, DefaultPieDataset<String> dataset) {
        // Colores fijos para las categorías que ya conocemos
        java.util.Map<String, Color> coloresFijos = new java.util.HashMap<>();
        coloresFijos.put("Comida", new Color(231, 76, 60));      // Rojo
        coloresFijos.put("Transporte", new Color(52, 152, 219));  // Azul
        coloresFijos.put("Material Escolar", new Color(46, 204, 113)); // Verde
        coloresFijos.put("Ocio", new Color(241, 196, 15));      // Amarillo
        coloresFijos.put("Alojamiento", new Color(155, 89, 182)); // Morado
        coloresFijos.put("Deporte", new Color(230, 126, 34));    // Naranja

        // Recorro el dataset para asignar los colores
        for (int i = 0; i < dataset.getItemCount(); i++) {
            String categoria = dataset.getKey(i);
            if (coloresFijos.containsKey(categoria)) {
                plot.setSectionPaint(categoria, coloresFijos.get(categoria));
            } else {
                // Si es una categoría nueva, le invento un color basado en el nombre (hash)
                int hash = categoria.hashCode();
                int r = (hash & 0xFF0000) >> 16;
                int g = (hash & 0x00FF00) >> 8;
                int b = (hash & 0x0000FF);
                // Aseguro que no sea muy oscuro para que se vea
                plot.setSectionPaint(categoria, new Color((r % 150) + 50, (g % 150) + 50, (b % 150) + 50));
            }
        }
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
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

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
        // Ahora pillo todas las filas seleccionadas, no solo una
        int[] selectedRows = table.getSelectedRows();
        
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar al menos una fila de la tabla.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mensaje;
        if (selectedRows.length == 1) {
            int modelRow = table.convertRowIndexToModel(selectedRows[0]);
            mensaje = "¿Está seguro de borrar la transacción ID " + tableModel.getValueAt(modelRow, 0) + "?";
        } else {
            mensaje = "¿Está seguro de borrar las " + selectedRows.length + " transacciones seleccionadas?";
        }

        int conf = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (conf == JOptionPane.YES_OPTION) {
            boolean algunError = false;
            for (int viewRow : selectedRows) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                int idTransaccion = (int) tableModel.getValueAt(modelRow, 0);
                int idUsuarioTrans = (int) tableModel.getValueAt(modelRow, 6);

                // Compruebo si el estudiante intenta borrar algo que no es suyo
                if ("Estudiante".equals(usuarioActual.getRol()) && usuarioActual.getId() != idUsuarioTrans) {
                    algunError = true;
                    continue; 
                }

                if (!transaccionDAO.borrarTransaccion(idTransaccion)) {
                    algunError = true;
                }
            }
            
            if (algunError) {
                JOptionPane.showMessageDialog(this, "Hubo problemas al borrar algunas transacciones (quizás por permisos).", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
            
            // Recargo la tabla para que se vea que han desaparecido
            cargarDatos();
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
