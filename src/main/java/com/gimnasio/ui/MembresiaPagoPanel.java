package com.gimnasio.ui;

import com.gimnasio.config.Db;
import com.gimnasio.dao.MembresiaDAO;
import com.gimnasio.dao.PagoDAO;
import com.gimnasio.dao.SocioDAO;
import com.gimnasio.model.Membresia;
import com.gimnasio.model.Socio;
import com.gimnasio.model.Usuario;
import com.gimnasio.util.ReportePdfUtil;
import com.gimnasio.util.SwingUtil;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class MembresiaPagoPanel extends JPanel {

    private final Usuario usuario;
    private final SocioDAO socioDAO = new SocioDAO();
    private final MembresiaDAO membresiaDAO = new MembresiaDAO();
    private final PagoDAO pagoDAO = new PagoDAO();

    private int idMembresiaSeleccionada = 0;
    private int idSocioSeleccionado = 0;

    private final JTextField txtDni = new JTextField(10);

    private final JComboBox<String> cmbTipo = new JComboBox<>(
            new String[] { "Mensual", "Premium", "Trimestral", "Anual" });

    private final JDateChooser dateInicio = new JDateChooser();
    private final JDateChooser dateFin = new JDateChooser();

    private final JTextField txtCosto = new JTextField("120.00");

    private final JComboBox<String> cmbMetodo = new JComboBox<>(
            new String[] { "Efectivo", "Yape", "Plin", "Tarjeta" });

    private final JTable tableMembresias = new JTable();
    private final JTable tablePagos = new JTable();

    private JButton btnRegistrarPago;

    private final JLabel lblEstadoCarga = new JLabel("Tablas listas para cargar información.");

    private final Color ROJO = new Color(220, 38, 38);
    private final Color NARANJA = new Color(255, 111, 0);
    private final Color VERDE = new Color(34, 139, 84);
    private final Color AZUL = new Color(37, 99, 235);
    private final Color MORADO = new Color(124, 58, 237);
    private final Color GRIS_OSCURO = new Color(45, 45, 45);
    private final Color FONDO = new Color(245, 245, 245);
    private final Color BORDE = new Color(220, 220, 220);
    private final Color NEGRO_TABLA = new Color(25, 25, 25);

    public MembresiaPagoPanel(Usuario usuario) {
        this.usuario = usuario;
        setLayout(new BorderLayout());
        setBackground(FONDO);
        build();
        configurarFechasIniciales();
        actualizarCostoPorTipo();
        actualizarFechaVencimientoPorTipo();
        bloquearPago();
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

        tableMembresias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableMembresias.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarMembresia();
            }
        });

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
        estilizarFecha(dateInicio);
        estilizarFecha(dateFin);
        estilizarCampo(txtCosto);
        estilizarCombo(cmbMetodo);

        txtCosto.setEditable(false);
        txtCosto.setBackground(new Color(240, 240, 240));

        dateFin.setEnabled(false);

        cmbTipo.addActionListener(e -> {
            actualizarCostoPorTipo();
            actualizarFechaVencimientoPorTipo();
        });

        dateInicio.getDateEditor().addPropertyChangeListener("date", e -> actualizarFechaVencimientoPorTipo());

        form.add(label("DNI del socio"));
        form.add(txtDni);

        form.add(label("Tipo de membresía"));
        form.add(cmbTipo);

        form.add(label("Fecha inicio"));
        form.add(dateInicio);

        form.add(label("Fecha vencimiento"));
        form.add(dateFin);

        form.add(label("Costo / Monto"));
        form.add(txtCosto);

        form.add(label("Método de pago"));
        form.add(cmbMetodo);

        card.add(titulo, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);

        return card;
    }

    private void configurarFechasIniciales() {
        LocalDate hoy = LocalDate.now();

        dateInicio.setDate(convertirDate(hoy));
        dateInicio.setMinSelectableDate(convertirDate(hoy));

        dateFin.setDate(convertirDate(hoy.plusMonths(1)));
        dateFin.setMinSelectableDate(convertirDate(hoy.plusDays(1)));
    }

    private void actualizarCostoPorTipo() {
        String tipo = cmbTipo.getSelectedItem().toString();

        switch (tipo) {
            case "Mensual":
                txtCosto.setText("120.00");
                break;
            case "Premium":
                txtCosto.setText("180.00");
                break;
            case "Trimestral":
                txtCosto.setText("300.00");
                break;
            case "Anual":
                txtCosto.setText("900.00");
                break;
            default:
                txtCosto.setText("0.00");
                break;
        }
    }

    private void actualizarFechaVencimientoPorTipo() {
        try {
            if (dateInicio.getDate() == null) {
                return;
            }

            LocalDate fechaInicio = convertirLocalDate(dateInicio.getDate());
            String tipo = cmbTipo.getSelectedItem().toString();

            LocalDate fechaFin;

            switch (tipo) {
                case "Mensual":
                case "Premium":
                    fechaFin = fechaInicio.plusMonths(1);
                    break;
                case "Trimestral":
                    fechaFin = fechaInicio.plusMonths(3);
                    break;
                case "Anual":
                    fechaFin = fechaInicio.plusYears(1);
                    break;
                default:
                    fechaFin = fechaInicio.plusMonths(1);
                    break;
            }

            dateFin.setDate(convertirDate(fechaFin));

        } catch (Exception e) {
            dateFin.setDate(null);
        }
    }

    private Date convertirDate(LocalDate fecha) {
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private LocalDate convertirLocalDate(Date fecha) {
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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
        btnRegistrarPago = boton("Registrar pago", AZUL, new Color(59, 130, 246), 150);
        JButton btnAnular = boton("Anular pago", ROJO, NARANJA, 140);
        JButton btnPdf = boton("Generar PDF", MORADO, new Color(147, 51, 234), 140);
        JButton btnActualizar = boton("Actualizar tablas", GRIS_OSCURO, new Color(70, 70, 70), 160);

        izquierda.add(btnRegistrar);
        izquierda.add(btnRegistrarPago);
        izquierda.add(btnAnular);
        izquierda.add(btnPdf);
        izquierda.add(btnActualizar);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);

        lblEstadoCarga.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEstadoCarga.setForeground(new Color(90, 90, 90));

        derecha.add(lblEstadoCarga);

        panel.add(izquierda, BorderLayout.WEST);
        panel.add(derecha, BorderLayout.EAST);

        btnRegistrar.addActionListener(e -> registrarMembresia());
        btnRegistrarPago.addActionListener(e -> registrarPago());
        btnAnular.addActionListener(e -> anularPago());
        btnPdf.addActionListener(e -> generarPdfPagoSeleccionado());
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

    private void estilizarFecha(JDateChooser fecha) {
        fecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fecha.setDateFormatString("yyyy-MM-dd");
        fecha.setBackground(new Color(250, 250, 250));
        fecha.setBorder(BorderFactory.createLineBorder(new Color(205, 205, 205), 1));
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
                if (btn.isEnabled()) {
                    btn.setBackground(hover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(base);
                }
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

    private void bloquearPago() {
        cmbMetodo.setEnabled(false);

        if (btnRegistrarPago != null) {
            btnRegistrarPago.setEnabled(false);
            btnRegistrarPago.setBackground(new Color(150, 150, 150));
            btnRegistrarPago.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private void habilitarPago() {
        cmbMetodo.setEnabled(true);

        if (btnRegistrarPago != null) {
            btnRegistrarPago.setEnabled(true);
            btnRegistrarPago.setBackground(AZUL);
            btnRegistrarPago.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    private void seleccionarMembresia() {
        int row = tableMembresias.getSelectedRow();

        if (row < 0) {
            idMembresiaSeleccionada = 0;
            idSocioSeleccionado = 0;
            bloquearPago();
            return;
        }

        idMembresiaSeleccionada = Integer.parseInt(tableMembresias.getValueAt(row, 0).toString());

        String dni = tableMembresias.getValueAt(row, 1).toString();
        String tipo = tableMembresias.getValueAt(row, 3).toString();
        Object fechaInicioObj = tableMembresias.getValueAt(row, 4);
        Object fechaFinObj = tableMembresias.getValueAt(row, 5);
        String costo = tableMembresias.getValueAt(row, 6).toString();
        String estado = tableMembresias.getValueAt(row, 7).toString();

        txtDni.setText(dni);
        cmbTipo.setSelectedItem(tipo);
        txtCosto.setText(costo);

        if (fechaInicioObj instanceof java.sql.Date) {
            dateInicio.setDate((java.sql.Date) fechaInicioObj);
        }

        if (fechaFinObj instanceof java.sql.Date) {
            dateFin.setDate((java.sql.Date) fechaFinObj);
        }

        try {
            Socio socio = socioDAO.buscarPorDni(dni);

            if (socio != null) {
                idSocioSeleccionado = socio.getIdSocio();
            } else {
                idSocioSeleccionado = 0;
            }

            if (estado.equalsIgnoreCase("Vigente")) {
                habilitarPago();
            } else {
                bloquearPago();
                SwingUtil.error(this, "Solo puede registrar pago de una membresía vigente.");
            }

        } catch (Exception e) {
            idSocioSeleccionado = 0;
            bloquearPago();
        }
    }

    private void registrarMembresia() {
        try {
            tableMembresias.clearSelection();
            idMembresiaSeleccionada = 0;
            idSocioSeleccionado = 0;
            bloquearPago();

            actualizarCostoPorTipo();
            actualizarFechaVencimientoPorTipo();

            String dni = txtDni.getText().trim();

            if (dni.isEmpty()) {
                SwingUtil.error(this, "Ingrese el DNI del socio.");
                txtDni.requestFocus();
                return;
            }

            if (!dni.matches("\\d{8}")) {
                SwingUtil.error(this, "El DNI debe tener una longitud física de 8 dígitos numéricos.");
                txtDni.requestFocus();
                return;
            }

            Socio socio = socioDAO.buscarPorDni(dni);

            if (socio == null) {
                SwingUtil.error(this, "No existe socio con ese DNI.");
                txtDni.requestFocus();
                return;
            }

            if (dateInicio.getDate() == null || dateFin.getDate() == null) {
                SwingUtil.error(this, "Seleccione la fecha de inicio y vencimiento.");
                return;
            }

            LocalDate fechaInicio = convertirLocalDate(dateInicio.getDate());
            LocalDate fechaFin = convertirLocalDate(dateFin.getDate());
            LocalDate fechaActual = LocalDate.now();

            if (fechaInicio.isBefore(fechaActual)) {
                SwingUtil.error(this, "La fecha de inicio no puede ser anterior a la fecha actual.");
                dateInicio.requestFocus();
                return;
            }

            if (!fechaFin.isAfter(fechaInicio)) {
                SwingUtil.error(this, "La fecha de vencimiento debe ser posterior a la fecha de inicio.");
                dateFin.requestFocus();
                return;
            }

            Membresia m = new Membresia(
                    0,
                    socio.getIdSocio(),
                    cmbTipo.getSelectedItem().toString(),
                    fechaInicio,
                    fechaFin,
                    new BigDecimal(txtCosto.getText().trim()),
                    "Vigente");

            membresiaDAO.registrar(m);
            cargarTablas();
            bloquearPago();

            SwingUtil.info(this,
                    "Membresía registrada correctamente. Para registrar el pago, seleccione la membresía en la tabla.");

        } catch (Exception ex) {
            SwingUtil.error(this, "No se pudo registrar membresía: " + ex.getMessage());
        }
    }

    private void registrarPago() {
        try {
            if (idMembresiaSeleccionada == 0) {
                SwingUtil.error(this, "Seleccione primero una membresía registrada en la tabla.");
                bloquearPago();
                return;
            }

            if (idSocioSeleccionado == 0) {
                SwingUtil.error(this, "Seleccione una membresía válida para registrar el pago.");
                bloquearPago();
                return;
            }

            int row = tableMembresias.getSelectedRow();

            if (row < 0) {
                SwingUtil.error(this, "Seleccione primero una membresía registrada en la tabla.");
                bloquearPago();
                return;
            }

            String estadoMembresia = tableMembresias.getValueAt(row, 7).toString();

            if (!estadoMembresia.equalsIgnoreCase("Vigente")) {
                SwingUtil.error(this, "Solo se puede registrar pago de una membresía vigente.");
                bloquearPago();
                return;
            }

            if (existePagoRegistrado(idMembresiaSeleccionada)) {
                SwingUtil.error(this, "Esta membresía ya tiene un pago registrado.");
                bloquearPago();
                tableMembresias.clearSelection();
                return;
            }

            BigDecimal monto = new BigDecimal(tableMembresias.getValueAt(row, 6).toString());

            pagoDAO.registrar(
                    idSocioSeleccionado,
                    idMembresiaSeleccionada,
                    LocalDate.now(),
                    monto,
                    cmbMetodo.getSelectedItem().toString());

            cargarTablas();

            tableMembresias.clearSelection();
            idMembresiaSeleccionada = 0;
            idSocioSeleccionado = 0;
            bloquearPago();

            SwingUtil.info(this, "Pago registrado correctamente.");

        } catch (Exception ex) {
            SwingUtil.error(this, "No se pudo registrar pago: " + ex.getMessage());
        }
    }

    private boolean existePagoRegistrado(int idMembresia) throws Exception {
        String sql = """
                SELECT COUNT(*) AS total
                FROM pago
                WHERE id_membresia = ?
                  AND estado_pago = 'Registrado'
                """;

        try (Connection cn = Db.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idMembresia);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        }

        return false;
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
                bloquearPago();
            }

        } catch (Exception ex) {
            SwingUtil.error(this, ex.getMessage());
        }
    }

    private void generarPdfPagoSeleccionado() {
        try {
            int row = tablePagos.getSelectedRow();

            if (row < 0) {
                SwingUtil.error(this, "Seleccione un pago registrado para generar el PDF.");
                return;
            }

            int idPago = Integer.parseInt(tablePagos.getValueAt(row, 0).toString());
            String estadoPago = tablePagos.getValueAt(row, 6).toString();

            if (!estadoPago.equalsIgnoreCase("Registrado")) {
                SwingUtil.error(this, "Solo se puede generar PDF de pagos con estado Registrado.");
                return;
            }

            Object[] datos = pagoDAO.obtenerDatosReportePago(idPago);

            if (datos == null) {
                SwingUtil.error(this, "No se encontraron datos del pago seleccionado.");
                return;
            }

            File pdf = ReportePdfUtil.generarReportePagoMembresia(datos);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdf);
            }

            SwingUtil.info(this, "Reporte PDF generado correctamente:\n" + pdf.getAbsolutePath());

        } catch (Exception ex) {
            SwingUtil.error(this, "No se pudo generar el PDF: " + ex.getMessage());
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