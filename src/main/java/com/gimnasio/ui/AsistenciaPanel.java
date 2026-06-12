package com.gimnasio.ui;

import com.gimnasio.dao.AsistenciaDAO;
import com.gimnasio.dao.MembresiaDAO;
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

public class AsistenciaPanel extends JPanel {

    private final SocioDAO socioDAO = new SocioDAO();
    private final MembresiaDAO membresiaDAO = new MembresiaDAO();
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();

    private final JTextField txtDni = new JTextField(18);
    private final JLabel lblResultado = new JLabel("Esperando validación de acceso");
    private final JTable table = new JTable();

    private final Color ROJO = new Color(220, 38, 38);
    private final Color NARANJA = new Color(255, 111, 0);
    private final Color VERDE = new Color(34, 139, 84);
    private final Color AZUL = new Color(37, 99, 235);
    private final Color GRIS_OSCURO = new Color(45, 45, 45);
    private final Color FONDO = new Color(245, 245, 245);
    private final Color BORDE = new Color(220, 220, 220);
    private final Color NEGRO_TABLA = new Color(25, 25, 25);

    public AsistenciaPanel(Usuario usuario) {
        setLayout(new BorderLayout(0, 16));
        setBackground(FONDO);
        build();
        cargar();
    }

    private void build() {
        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);

        JPanel cardValidacion = crearCardValidacion();
        JPanel cardResultado = crearCardResultado();

        top.add(cardValidacion, BorderLayout.CENTER);
        top.add(cardResultado, BorderLayout.SOUTH);

        table.setModel(new DefaultTableModel(
                new String[] { "ID", "DNI", "Socio", "Fecha", "Hora", "Resultado" }, 0));

        estilizarTabla();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE, 1));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel tablaCard = new JPanel(new BorderLayout(0, 10));
        tablaCard.setBackground(Color.WHITE);
        tablaCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel tituloTabla = new JLabel("Historial de asistencias registradas");
        tituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloTabla.setForeground(new Color(25, 25, 25));

        tablaCard.add(tituloTabla, BorderLayout.NORTH);
        tablaCard.add(scroll, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(tablaCard, BorderLayout.CENTER);
    }

    private JPanel crearCardValidacion() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 4));
        encabezado.setOpaque(false);

        JLabel titulo = new JLabel("Validación de ingreso");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(25, 25, 25));

        JLabel descripcion = new JLabel("Ingrese el DNI del socio para verificar si cuenta con una membresía vigente.");
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descripcion.setForeground(new Color(90, 90, 90));

        encabezado.add(titulo);
        encabezado.add(descripcion);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        form.setOpaque(false);

        JLabel lblDni = label("DNI / QR:");
        estilizarCampo(txtDni);

        JButton btnValidar = boton("Validar acceso", VERDE, new Color(46, 160, 95), 160);
        JButton btnLimpiar = boton("Limpiar", GRIS_OSCURO, new Color(70, 70, 70), 110);

        form.add(lblDni);
        form.add(txtDni);
        form.add(btnValidar);
        form.add(btnLimpiar);

        card.add(encabezado, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);

        btnValidar.addActionListener(e -> validar());
        btnLimpiar.addActionListener(e -> limpiar());

        txtDni.addActionListener(e -> validar());

        return card;
    }

    private JPanel crearCardResultado() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(255, 248, 242));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 190), 1),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)));

        JLabel titulo = new JLabel("Resultado:");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(new Color(35, 35, 35));

        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblResultado.setForeground(NARANJA);

        card.add(titulo, BorderLayout.WEST);
        card.add(lblResultado, BorderLayout.CENTER);

        return card;
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
        campo.setPreferredSize(new Dimension(220, 38));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 205, 205), 1),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
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

    private void estilizarTabla() {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(new Color(30, 30, 30));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 224, 210));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
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

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

                    if (column == 5 && value != null) {
                        if (value.toString().equalsIgnoreCase("Acceso permitido")) {
                            c.setForeground(VERDE);
                        } else if (value.toString().equalsIgnoreCase("Acceso denegado")) {
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

    private void validar() {
        try {
            String dni = txtDni.getText().trim();

            if (dni.isEmpty()) {
                lblResultado.setText("Ingrese un DNI para validar el acceso.");
                lblResultado.setForeground(NARANJA);
                return;
            }

            Socio socio = socioDAO.buscarPorDni(dni);

            if (socio == null || !"Activo".equalsIgnoreCase(socio.getEstado())) {
                lblResultado.setText("Acceso denegado: socio no existe o está inactivo.");
                lblResultado.setForeground(ROJO);
                return;
            }

            Membresia activa = membresiaDAO.membresiaActivaPorSocio(socio.getIdSocio());

            String resultado = activa == null ? "Acceso denegado" : "Acceso permitido";

            asistenciaDAO.registrar(socio.getIdSocio(), resultado);

            lblResultado.setText(resultado + " - " + socio.getNombres() + " " + socio.getApellidos());

            if ("Acceso permitido".equalsIgnoreCase(resultado)) {
                lblResultado.setForeground(VERDE);
            } else {
                lblResultado.setForeground(ROJO);
            }

            cargar();

        } catch (Exception ex) {
            SwingUtil.error(this, ex.getMessage());
        }
    }

    private void cargar() {
        try {
            DefaultTableModel m = (DefaultTableModel) table.getModel();
            m.setRowCount(0);

            for (Object[] r : asistenciaDAO.listar()) {
                m.addRow(r);
            }

        } catch (Exception ex) {
            SwingUtil.error(this, ex.getMessage());
        }
    }

    private void limpiar() {
        txtDni.setText("");
        lblResultado.setText("Esperando validación de acceso");
        lblResultado.setForeground(NARANJA);
        txtDni.requestFocus();
    }
}