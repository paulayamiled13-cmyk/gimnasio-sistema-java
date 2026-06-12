package com.gimnasio.ui;

import com.gimnasio.dao.ReporteDAO;
import com.gimnasio.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ReportesPanel extends JPanel {

    private final ReporteDAO dao = new ReporteDAO();
    private final JTable table = new JTable();

    private final JLabel lblTituloReporte = new JLabel("Seleccione un reporte para visualizar la información");
    private final JLabel lblDescripcion = new JLabel(
            "Use los botones superiores para consultar socios activos, morosidad, inventario o ingresos mensuales.");

    private final Color ROJO = new Color(220, 38, 38);
    private final Color NARANJA = new Color(255, 111, 0);
    private final Color VERDE = new Color(34, 139, 84);
    private final Color AZUL = new Color(37, 99, 235);
    private final Color MORADO = new Color(124, 58, 237);
    private final Color GRIS_OSCURO = new Color(45, 45, 45);
    private final Color FONDO = new Color(245, 245, 245);
    private final Color BORDE = new Color(220, 220, 220);
    private final Color NEGRO_TABLA = new Color(25, 25, 25);

    public ReportesPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(FONDO);
        build();
        cargarVistaInicial();
    }

    private void build() {
        JPanel top = crearPanelAcciones();
        JPanel tablaCard = crearPanelTabla();

        add(top, BorderLayout.NORTH);
        add(tablaCard, BorderLayout.CENTER);
    }

    private JPanel crearPanelAcciones() {
        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 4));
        encabezado.setOpaque(false);

        JLabel titulo = new JLabel("Consultas administrativas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(25, 25, 25));

        JLabel subtitulo = new JLabel(
                "Seleccione el tipo de reporte que desea generar para el control operativo del gimnasio.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(90, 90, 90));

        encabezado.add(titulo);
        encabezado.add(subtitulo);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);

        JButton btnSocios = boton("Socios activos", VERDE, new Color(46, 160, 95), 150);
        JButton btnMorosidad = boton("Morosidad", ROJO, NARANJA, 140);
        JButton btnInventario = boton("Inventario", AZUL, new Color(59, 130, 246), 140);
        JButton btnIngresos = boton("Ingresos mensuales", MORADO, new Color(147, 51, 234), 180);

        botones.add(btnSocios);
        botones.add(btnMorosidad);
        botones.add(btnInventario);
        botones.add(btnIngresos);

        card.add(encabezado, BorderLayout.NORTH);
        card.add(botones, BorderLayout.CENTER);

        btnSocios.addActionListener(e -> cargar(
                "Reporte de socios activos",
                "Listado de socios registrados con estado activo dentro del sistema.",
                new String[] { "DNI", "Socio", "Teléfono", "Correo", "Estado" },
                () -> dao.sociosActivos()));

        btnMorosidad.addActionListener(e -> cargar(
                "Reporte de morosidad",
                "Socios con membresías vencidas o pendientes de regularización.",
                new String[] { "DNI", "Socio", "Tipo", "Vencimiento", "Estado" },
                () -> dao.membresiasVencidas()));

        btnInventario.addActionListener(e -> cargar(
                "Reporte de inventario",
                "Listado general de productos, precios, stock disponible y estado.",
                new String[] { "Producto", "Descripción", "Precio", "Stock", "Estado" },
                () -> dao.inventario()));

        btnIngresos.addActionListener(e -> cargar(
                "Reporte de ingresos mensuales",
                "Resumen de ingresos registrados por periodo mensual.",
                new String[] { "Periodo", "Total" },
                () -> dao.ingresos()));

        return card;
    }

    private JPanel crearPanelTabla() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JPanel encabezadoTabla = new JPanel(new BorderLayout());
        encabezadoTabla.setOpaque(false);

        JPanel texto = new JPanel(new GridLayout(2, 1, 0, 3));
        texto.setOpaque(false);

        lblTituloReporte.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloReporte.setForeground(new Color(25, 25, 25));

        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDescripcion.setForeground(new Color(90, 90, 90));

        texto.add(lblTituloReporte);
        texto.add(lblDescripcion);

        JLabel etiqueta = new JLabel("  Resultados  ");
        etiqueta.setOpaque(true);
        etiqueta.setBackground(ROJO);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        etiqueta.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        encabezadoTabla.add(texto, BorderLayout.WEST);
        encabezadoTabla.add(etiqueta, BorderLayout.EAST);

        table.setModel(new DefaultTableModel(
                new String[] { "Reporte", "Descripción" }, 0));

        estilizarTabla();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE, 1));
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(encabezadoTabla, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
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
        btn.setPreferredSize(new Dimension(ancho, 40));
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

                    if (value != null) {
                        String texto = value.toString();

                        if (texto.equalsIgnoreCase("Activo")) {
                            c.setForeground(VERDE);
                        } else if (texto.equalsIgnoreCase("Inactivo")
                                || texto.equalsIgnoreCase("Vencida")
                                || texto.equalsIgnoreCase("Vencido")) {
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

    private interface Query {
        List<Object[]> get() throws Exception;
    }

    private void cargarVistaInicial() {
        DefaultTableModel m = new DefaultTableModel(
                new String[] { "Reporte disponible", "Uso del reporte" }, 0);

        m.addRow(new Object[] { "Socios activos", "Permite visualizar los socios habilitados en el gimnasio." });
        m.addRow(new Object[] { "Morosidad", "Permite revisar membresías vencidas o pendientes." });
        m.addRow(new Object[] { "Inventario", "Permite consultar productos, stock y estado." });
        m.addRow(new Object[] { "Ingresos mensuales", "Permite visualizar el total de ingresos por periodo." });

        table.setModel(m);
        estilizarTabla();
    }

    private void cargar(String titulo, String descripcion, String[] columnas, Query query) {
        try {
            lblTituloReporte.setText(titulo);
            lblDescripcion.setText(descripcion);

            DefaultTableModel m = new DefaultTableModel(columnas, 0);

            for (Object[] r : query.get()) {
                m.addRow(r);
            }

            table.setModel(m);
            estilizarTabla();

        } catch (Exception ex) {
            SwingUtil.error(this, ex.getMessage());
        }
    }
}