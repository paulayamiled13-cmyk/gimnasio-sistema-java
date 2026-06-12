package com.gimnasio.ui;

import com.gimnasio.dao.ProductoDAO;
import com.gimnasio.model.Producto;
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

public class ProductoPanel extends JPanel {

    private final Usuario usuario;
    private final ProductoDAO dao = new ProductoDAO();

    private final JTable table = new JTable();
    private final JTextField txtBuscar = new JTextField(18);

    private final JTextField txtId = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private final JTextField txtPrecio = new JTextField();
    private final JTextField txtStock = new JTextField();

    private final JComboBox<String> cmbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });

    private final Color ROJO = new Color(220, 38, 38);
    private final Color NARANJA = new Color(255, 111, 0);
    private final Color VERDE = new Color(34, 139, 84);
    private final Color AZUL = new Color(37, 99, 235);
    private final Color GRIS_OSCURO = new Color(45, 45, 45);
    private final Color FONDO = new Color(245, 245, 245);
    private final Color BORDE = new Color(220, 220, 220);
    private final Color NEGRO_TABLA = new Color(25, 25, 25);

    public ProductoPanel(Usuario usuario) {
        this.usuario = usuario;
        setLayout(new BorderLayout(0, 8));
        setBackground(FONDO);
        build();
        cargar();
    }

    private void build() {
        txtId.setVisible(false);

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(crearFormulario(), BorderLayout.CENTER);
        top.add(crearAcciones(), BorderLayout.SOUTH);

        table.setModel(new DefaultTableModel(
                new String[] { "ID", "Producto", "Descripción", "Precio", "Stock", "Estado" }, 0));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionar();
            }
        });

        estilizarTabla(table);

        JScrollPane scrollTabla = crearScrollTabla(table);
        JPanel panelTabla = crearPanelTabla("Listado de productos registrados", scrollTabla);

        add(top, BorderLayout.NORTH);
        add(panelTabla, BorderLayout.CENTER);
    }

    private JPanel crearFormulario() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)));

        JLabel titulo = new JLabel("Datos del producto");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(25, 25, 25));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JPanel form = new JPanel(new GridLayout(3, 4, 10, 7));
        form.setOpaque(false);

        estilizarCampo(txtNombre);
        estilizarCampo(txtDescripcion);
        estilizarCampo(txtPrecio);
        estilizarCampo(txtStock);
        estilizarCombo(cmbEstado);

        form.add(label("Producto"));
        form.add(txtNombre);
        form.add(label("Descripción"));
        form.add(txtDescripcion);

        form.add(label("Precio"));
        form.add(txtPrecio);
        form.add(label("Stock"));
        form.add(txtStock);

        form.add(label("Estado"));
        form.add(cmbEstado);
        form.add(new JLabel(""));
        form.add(new JLabel(""));

        card.add(titulo, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearAcciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izquierda.setOpaque(false);

        JButton btnNuevo = boton("Nuevo", GRIS_OSCURO, new Color(70, 70, 70), 130);
        JButton btnGuardar = boton("Guardar", VERDE, new Color(46, 160, 95), 130);
        JButton btnEliminar = boton("Desactivar", ROJO, NARANJA, 140);

        izquierda.add(btnNuevo);
        izquierda.add(btnGuardar);
        izquierda.add(btnEliminar);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);

        JLabel lblBuscar = label("Buscar:");
        estilizarCampo(txtBuscar);

        JButton btnBuscar = boton("Buscar", AZUL, new Color(59, 130, 246), 120);

        derecha.add(lblBuscar);
        derecha.add(txtBuscar);
        derecha.add(btnBuscar);

        panel.add(izquierda, BorderLayout.WEST);
        panel.add(derecha, BorderLayout.EAST);

        btnNuevo.addActionListener(e -> limpiar());
        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnBuscar.addActionListener(e -> cargar());

        return panel;
    }

    private JPanel crearPanelTabla(String titulo, JScrollPane scroll) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(8, 14, 10, 14)));

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
        scroll.setPreferredSize(new Dimension(900, 330));
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
        campo.setPreferredSize(new Dimension(250, 32));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 205, 205), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(new Color(250, 250, 250));
        combo.setForeground(Color.BLACK);
        combo.setPreferredSize(new Dimension(250, 32));
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
        btn.setPreferredSize(new Dimension(ancho, 36));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

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

                        if (texto.equalsIgnoreCase("Activo")) {
                            c.setForeground(VERDE);
                        } else if (texto.equalsIgnoreCase("Inactivo")) {
                            c.setForeground(ROJO);
                        } else if (column == 4) {
                            try {
                                int stock = Integer.parseInt(texto);
                                if (stock <= 10) {
                                    c.setForeground(NARANJA);
                                } else {
                                    c.setForeground(new Color(30, 30, 30));
                                }
                            } catch (Exception ex) {
                                c.setForeground(new Color(30, 30, 30));
                            }
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

    private void cargar() {
        try {
            DefaultTableModel m = (DefaultTableModel) table.getModel();
            m.setRowCount(0);

            for (Producto p : dao.listar(txtBuscar.getText().trim())) {
                m.addRow(new Object[] {
                        p.getIdProducto(),
                        p.getNombreProducto(),
                        p.getDescripcion(),
                        p.getPrecio(),
                        p.getStock(),
                        p.getEstado()
                });
            }

        } catch (Exception ex) {
            SwingUtil.error(this, ex.getMessage());
        }
    }

    private void seleccionar() {
        int row = table.getSelectedRow();

        if (row < 0) {
            return;
        }

        txtId.setText(table.getValueAt(row, 0).toString());
        txtNombre.setText(table.getValueAt(row, 1).toString());
        txtDescripcion.setText(table.getValueAt(row, 2).toString());
        txtPrecio.setText(table.getValueAt(row, 3).toString());
        txtStock.setText(table.getValueAt(row, 4).toString());
        cmbEstado.setSelectedItem(table.getValueAt(row, 5).toString());
    }

    private void guardar() {
        try {
            if (txtNombre.getText().trim().isEmpty()) {
                SwingUtil.error(this, "Ingrese el nombre del producto.");
                return;
            }

            int stock = Integer.parseInt(txtStock.getText().trim());

            if (stock < 0) {
                SwingUtil.error(this, "El stock no puede ser negativo.");
                return;
            }

            Producto p = new Producto(
                    txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText()),
                    usuario.getIdUsuario(),
                    txtNombre.getText().trim(),
                    txtDescripcion.getText().trim(),
                    new BigDecimal(txtPrecio.getText().trim()),
                    stock,
                    cmbEstado.getSelectedItem().toString());

            if (p.getIdProducto() == 0) {
                dao.insertar(p);
            } else {
                dao.actualizar(p);
            }

            SwingUtil.info(this, "Producto guardado correctamente.");
            limpiar();
            cargar();

        } catch (Exception ex) {
            SwingUtil.error(this, "No se pudo guardar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        try {
            if (txtId.getText().isEmpty()) {
                SwingUtil.error(this, "Seleccione un producto de la tabla para desactivarlo.");
                return;
            }

            if (SwingUtil.confirm(this, "¿Desea desactivar el producto seleccionado?")) {
                dao.desactivar(Integer.parseInt(txtId.getText()));
                limpiar();
                cargar();
            }

        } catch (Exception ex) {
            SwingUtil.error(this, ex.getMessage());
        }
    }

    private void limpiar() {
        txtId.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        cmbEstado.setSelectedIndex(0);
        table.clearSelection();
    }
}