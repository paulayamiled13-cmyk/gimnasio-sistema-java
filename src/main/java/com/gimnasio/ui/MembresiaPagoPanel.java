package com.gimnasio.ui;

import com.gimnasio.config.Db;
import com.gimnasio.dao.MembresiaDAO;
import com.gimnasio.dao.PagoDAO;
import com.gimnasio.dao.SocioDAO;
import com.gimnasio.model.Membresia;
import com.gimnasio.model.Socio;
import com.gimnasio.model.Usuario;
import com.gimnasio.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class MembresiaPagoPanel extends JPanel {

    private final Usuario usuario;
    private final SocioDAO socioDAO = new SocioDAO();
    private final MembresiaDAO membresiaDAO = new MembresiaDAO();
    private final PagoDAO pagoDAO = new PagoDAO();

    private final JTextField txtDni = new JTextField(10);
    private final JComboBox<String> cmbTipo = new JComboBox<>(
            new String[] { "Mensual", "Premium", "Trimestral", "Anual" });
    private final JTextField txtInicio = new JTextField("2026-06-01");
    private final JTextField txtFin = new JTextField("2026-06-30");
    private final JTextField txtCosto = new JTextField("120.00");
    private final JComboBox<String> cmbMetodo = new JComboBox<>(new String[] { "Efectivo", "Yape", "Plin", "Tarjeta" });

    private final JTable tableMembresias = new JTable();
    private final JTable tablePagos = new JTable();

    private final JLabel lblEstadoCarga = new JLabel("Tablas listas para cargar información.");

    private final Color ROJO = new Color(220, 38, 38);
    private final Color NARANJA = new Color(255, 111, 0);
    private final Color VERDE = new Color(34, 139, 84);
    private final Color AZUL = new Color(37, 99, 235);
    private final Color GRIS_OSCURO = new Color(45, 45, 45);
    private final Color FONDO = new Color(245, 245, 245);
    private final Color BORDE = new Color(220, 220, 220);
    private final Color NEGRO_TABLA = new Color(25, 25, 25);

    public MembresiaPagoPanel(Usuario usuario) {
        this.usuario = usuario;
        setLayout(new BorderLayout());
        setBackground(FONDO);
        build();
        cargarTablas();
    }

    private void build() {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel formCard = crearFormulario();
        JPanel actions = crearAcciones();

        tableMembresias.setModel(new DefaultTableModel(
                new String[] { "ID", "DNI", "Socio", "Tipo", "Inicio", "Vencimiento", "Costo", "Estado" }, 0));

        tablePagos.setModel(new DefaultTableModel(
                new String[] { "ID", "DNI", "Socio", "Fecha", "Monto", "Método", "Estado" }, 0));

        estilizarTabla(tableMembresias);
        estilizarTabla(tablePagos);

        JScrollPane scrollMembresias = crearScrollTabla(tableMembresias);
        JScrollPane scrollPagos = crearScrollTabla(tablePagos);

        JPanel panelMembresias = crearPanelTabla("Listado de membresías registradas", scrollMembresias);
        JPanel panelPagos = crearPanelTabla("Historial de pagos registrados", scrollPagos);

        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        panelMembresias.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        panelPagos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        contenido.add(formCard);
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(actions);
        contenido.add(Box.createVerticalStrut(12));
        contenido.add(panelMembresias);
        contenido.add(Box.createVerticalStrut(12));
        contenido.add(panelPagos);

        JScrollPane scrollGeneral = new JScrollPane(contenido);
        scrollGeneral.setBorder(null);
        scrollGeneral.getViewport().setBackground(FONDO);
        scrollGeneral.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollGeneral.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollGeneral.getVerticalScrollBar().setUnitIncrement(18);

        add(scrollGeneral, BorderLayout.CENTER);
    }

    private JPanel crearFormulario() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel titulo = new JLabel("Datos de membresía y pago");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(25, 25, 25));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JPanel form = new JPanel(new GridLayout(3, 4, 12, 12));
        form.setOpaque(false);

        estilizarCampo(txtDni);
        estilizarCombo(cmbTipo);
        estilizarCampo(txtInicio);
        estilizarCampo(txtFin);
        estilizarCampo(txtCosto);
        estilizarCombo(cmbMetodo);

        form.add(label("DNI del socio"));
        form.add(txtDni);

        form.add(label("Tipo de membresía"));
        form.add(cmbTipo);

        form.add(label("Fecha inicio yyyy-mm-dd"));
        form.add(txtInicio);

        form.add(label("Fecha vencimiento yyyy-mm-dd"));
        form.add(txtFin);

        form.add(label("Costo / Monto"));
        form.add(txtCosto);

        form.add(label("Método de pago"));
        form.add(cmbMetodo);

        card.add(titulo, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearAcciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izquierda.setOpaque(false);

        JButton btnRegistrar = boton("Registrar membresía", VERDE, new Color(46, 160, 95), 180);
        JButton btnPago = boton("Registrar pago", AZUL, new Color(59, 130, 246), 150);
        JButton btnAnular = boton("Anular pago", ROJO, NARANJA, 150);
        JButton btnActualizar = boton("Actualizar tablas", GRIS_OSCURO, new Color(70, 70, 70), 160);

        izquierda.add(btnRegistrar);
        izquierda.add(btnPago);
        izquierda.add(btnAnular);
        izquierda.add(btnActualizar);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);

        lblEstadoCarga.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEstadoCarga.setForeground(new Color(90, 90, 90));

        derecha.add(lblEstadoCarga);

        panel.add(izquierda, BorderLayout.WEST);
        panel.add(derecha, BorderLayout.EAST);

        btnRegistrar.addActionListener(e -> registrarMembresia());
        btnPago.addActionListener(e -> registrarPago());
        btnAnular.addActionListener(e -> anularPago());
        btnActualizar.addActionListener(e -> cargarTablas());

        return panel;
    }

    private JPanel crearPanelTabla(String titulo, JScrollPane scroll) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(25, 25, 25));

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane crearScrollTabla(JTable tabla) {
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE, 1));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setPreferredSize(new Dimension(900, 180));
        scroll.setMinimumSize(new Dimension(900, 160));
        return scroll;
    }

    private JLabel label(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(35, 35, 35));
        return lbl;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setForeground(Color.BLACK);
        campo.setBackground(new Color(250, 250, 250));
        campo.setCaretColor(Color.BLACK);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 205, 205), 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(new Color(250, 250, 250));
        combo.setForeground(Color.BLACK);
        combo.setBorder(BorderFactory.createLineBorder(new Color(205, 205, 205), 1));
    }

    private JButton boton(String texto, Color base, Color hover, int ancho) {
        JButton btn = new JButton(texto);
        btn.setUI(new BasicButtonUI());
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(ancho, 38));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(base);
            }
        });

        return btn;
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(30);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setForeground(new Color(30, 30, 30));
        tabla.setBackground(Color.WHITE);
        tabla.setSelectionBackground(new Color(255, 224, 210));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setFillsViewportHeight(true);

        JTableHeader header = tabla.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                lbl.setBackground(NEGRO_TABLA);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                return lbl;
            }
        });

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                if (isSelected) {
                    c.setBackground(new Color(255, 224, 210));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));

                    if (value != null) {
                        String texto = value.toString();

                        if (texto.equalsIgnoreCase("Vigente")
                                || texto.equalsIgnoreCase("Registrado")
                                || texto.equalsIgnoreCase("Pagado")) {
                            c.setForeground(VERDE);
                        } else if (texto.equalsIgnoreCase("Vencida")
                                || texto.equalsIgnoreCase("Anulado")) {
                            c.setForeground(ROJO);
                        } else {
                            c.setForeground(new Color(30, 30, 30));
                        }
                    } else {
                        c.setForeground(new Color(30, 30, 30));
                    }
                }

                return c;
            }
        });
    }

    private void registrarMembresia() {
        try {
            Socio socio = socioDAO.buscarPorDni(txtDni.getText().trim());

            if (socio == null) {
                SwingUtil.error(this, "No existe socio con ese DNI.");
                return;
            }

            Membresia m = new Membresia(
                    0,
                    socio.getIdSocio(),
                    cmbTipo.getSelectedItem().toString(),
                    LocalDate.parse(txtInicio.getText().trim()),
                    LocalDate.parse(txtFin.getText().trim()),
                    new BigDecimal(txtCosto.getText().trim()),
                    "Vigente");

            if (!m.getFechaVencimiento().isAfter(m.getFechaInicio())) {
                SwingUtil.error(this, "La fecha de vencimiento debe ser posterior a la fecha de inicio.");
                return;
            }

            membresiaDAO.registrar(m);
            cargarTablas();
            SwingUtil.info(this, "Membresía registrada correctamente.");

        } catch (Exception ex) {
            SwingUtil.error(this, "No se pudo registrar membresía: " + ex.getMessage());
        }
    }

    private void registrarPago() {
        try {
            Socio socio = socioDAO.buscarPorDni(txtDni.getText().trim());

            if (socio == null) {
                SwingUtil.error(this, "No existe socio con ese DNI.");
                return;
            }

            Membresia activa = membresiaDAO.membresiaActivaPorSocio(socio.getIdSocio());

            if (activa == null) {
                SwingUtil.error(this, "El socio no tiene membresía vigente. Registre primero una membresía.");
                return;
            }

            pagoDAO.registrar(
                    socio.getIdSocio(),
                    activa.getIdMembresia(),
                    LocalDate.now(),
                    new BigDecimal(txtCosto.getText().trim()),
                    cmbMetodo.getSelectedItem().toString());

            cargarTablas();
            SwingUtil.info(this, "Pago registrado correctamente.");

        } catch (Exception ex) {
            SwingUtil.error(this, "No se pudo registrar pago: " + ex.getMessage());
        }
    }

    private void anularPago() {
        try {
            int row = tablePagos.getSelectedRow();

            if (row < 0) {
                SwingUtil.error(this, "Seleccione un pago.");
                return;
            }

            int id = Integer.parseInt(tablePagos.getValueAt(row, 0).toString());

            if (SwingUtil.confirm(this, "¿Desea anular el pago seleccionado?")) {
                pagoDAO.anular(id);
                cargarTablas();
            }

        } catch (Exception ex) {
            SwingUtil.error(this, ex.getMessage());
        }
    }

    private void cargarTablas() {
        int totalMembresias = 0;
        int totalPagos = 0;

        try {
            DefaultTableModel modeloMembresias = (DefaultTableModel) tableMembresias.getModel();
            modeloMembresias.setRowCount(0);

            String sqlMembresias = """
                    SELECT
                        m.id_membresia,
                        s.dni,
                        CONCAT(s.nombres, ' ', s.apellidos) AS socio,
                        m.tipo_membresia,
                        m.fecha_inicio,
                        m.fecha_vencimiento,
                        m.costo,
                        m.estado
                    FROM membresia m
                    INNER JOIN socio s ON s.id_socio = m.id_socio
                    ORDER BY m.id_membresia DESC
                    """;

            try (Connection cn = Db.getConnection();
                    PreparedStatement ps = cn.prepareStatement(sqlMembresias);
                    ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    modeloMembresias.addRow(new Object[] {
                            rs.getInt("id_membresia"),
                            rs.getString("dni"),
                            rs.getString("socio"),
                            rs.getString("tipo_membresia"),
                            rs.getDate("fecha_inicio"),
                            rs.getDate("fecha_vencimiento"),
                            rs.getBigDecimal("costo"),
                            rs.getString("estado")
                    });
                    totalMembresias++;
                }
            }

            DefaultTableModel modeloPagos = (DefaultTableModel) tablePagos.getModel();
            modeloPagos.setRowCount(0);

            String sqlPagos = """
                    SELECT
                        p.id_pago,
                        s.dni,
                        CONCAT(s.nombres, ' ', s.apellidos) AS socio,
                        p.fecha_pago,
                        p.monto,
                        p.metodo_pago,
                        p.estado_pago
                    FROM pago p
                    INNER JOIN socio s ON s.id_socio = p.id_socio
                    ORDER BY p.id_pago DESC
                    """;

            try (Connection cn = Db.getConnection();
                    PreparedStatement ps = cn.prepareStatement(sqlPagos);
                    ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    modeloPagos.addRow(new Object[] {
                            rs.getInt("id_pago"),
                            rs.getString("dni"),
                            rs.getString("socio"),
                            rs.getDate("fecha_pago"),
                            rs.getBigDecimal("monto"),
                            rs.getString("metodo_pago"),
                            rs.getString("estado_pago")
                    });
                    totalPagos++;
                }
            }

            lblEstadoCarga.setText("Membresías: " + totalMembresias + " | Pagos: " + totalPagos);
            lblEstadoCarga.setForeground(totalMembresias > 0 || totalPagos > 0 ? VERDE : ROJO);

            tableMembresias.revalidate();
            tableMembresias.repaint();
            tablePagos.revalidate();
            tablePagos.repaint();

        } catch (Exception ex) {
            lblEstadoCarga.setText("Error al cargar tablas.");
            lblEstadoCarga.setForeground(ROJO);
            SwingUtil.error(this, "Error al cargar membresías y pagos: " + ex.getMessage());
        }
    }
}