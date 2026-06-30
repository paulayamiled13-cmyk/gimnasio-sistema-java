package com.gimnasio.ui;

import com.gimnasio.dao.UsuarioDAO;
import com.gimnasio.model.Usuario;
import com.gimnasio.util.PasswordUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtClave;

    private final Color NEGRO = new Color(10, 10, 10);
    private final Color BLANCO = Color.WHITE;
    private final Color NARANJA = new Color(255, 111, 0);
    private final Color NARANJA_HOVER = new Color(255, 130, 20);
    private final Color GRIS = new Color(55, 55, 55);
    private final Color GRIS_HOVER = new Color(75, 75, 75);
    private final Color ROJO_FONDO = new Color(105, 0, 0);

    public LoginFrame() {
        setTitle("Login - Sistema de Gestión Interno para Gimnasio");

        setSize(1100, 680);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);

        construirInterfaz();
    }

    private void construirInterfaz() {
        JPanel fondo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(15, 5, 5),
                        getWidth(), getHeight(), ROJO_FONDO);

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 35));
                g2.drawLine(0, getHeight() - 90, getWidth(), getHeight() - 120);
            }
        };

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel contenedorPrincipal = new JPanel(new GridLayout(1, 2, 0, 0));
        contenedorPrincipal.setPreferredSize(new Dimension(900, 540));
        contenedorPrincipal.setMinimumSize(new Dimension(900, 540));
        contenedorPrincipal.setBackground(NEGRO);

        JPanel panelIzquierdo = crearPanelLogin();
        JPanel panelDerecho = crearPanelImagen();

        contenedorPrincipal.add(panelIzquierdo);
        contenedorPrincipal.add(panelDerecho);

        centro.add(contenedorPrincipal);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 25, 20, 0));

        JLabel lblFooter = new JLabel(
                "POWER GYM SYSTEM  |  Socios  •  Membresías  •  Asistencia  •  Inventario  •  Reportes");
        lblFooter.setForeground(BLANCO);
        lblFooter.setFont(new Font("Arial", Font.BOLD, 12));
        footer.add(lblFooter);

        fondo.add(centro, BorderLayout.CENTER);
        fondo.add(footer, BorderLayout.SOUTH);

        setContentPane(fondo);
    }

    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel(null);
        panel.setBackground(NEGRO);

        int anchoPanel = 450;
        int anchoCampo = 280;
        int altoCampo = 42;
        int xCampo = (anchoPanel - anchoCampo) / 2;

        JLabel titulo = new JLabel(
                "<html><div style='text-align:center;'>SISTEMA DE GESTIÓN<br>INTERNO PARA GIMNASIO</div></html>");
        titulo.setForeground(BLANCO);
        titulo.setFont(new Font("Arial", Font.BOLD, 25));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBounds(0, 70, anchoPanel, 70);
        panel.add(titulo);

        JLabel subtitulo = new JLabel("Acceso administrativo del sistema");
        subtitulo.setForeground(new Color(220, 220, 220));
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        subtitulo.setBounds(0, 150, anchoPanel, 25);
        panel.add(subtitulo);

        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setForeground(BLANCO);
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        lblUsuario.setHorizontalAlignment(SwingConstants.LEFT);
        lblUsuario.setBounds(xCampo, 200, anchoCampo, 20);
        panel.add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 15));
        txtUsuario.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        txtUsuario.setBounds(xCampo, 227, anchoCampo, altoCampo);
        panel.add(txtUsuario);

        JLabel lblClave = new JLabel("Contraseña");
        lblClave.setForeground(BLANCO);
        lblClave.setFont(new Font("Arial", Font.BOLD, 14));
        lblClave.setHorizontalAlignment(SwingConstants.LEFT);
        lblClave.setBounds(xCampo, 292, anchoCampo, 20);
        panel.add(lblClave);

        txtClave = new JPasswordField();
        txtClave.setFont(new Font("Arial", Font.PLAIN, 15));
        txtClave.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        txtClave.setBounds(xCampo, 319, anchoCampo, altoCampo);
        panel.add(txtClave);

        JButton btnIngresar = crearBoton("INICIAR SESIÓN", NARANJA, NARANJA_HOVER);
        btnIngresar.setBounds(xCampo, 390, anchoCampo, 48);
        btnIngresar.addActionListener(e -> validarLogin());
        panel.add(btnIngresar);

        JButton btnCancelar = crearBoton("CANCELAR", GRIS, GRIS_HOVER);
        btnCancelar.setBounds(xCampo, 453, anchoCampo, 48);
        btnCancelar.addActionListener(e -> System.exit(0));
        panel.add(btnCancelar);

        return panel;
    }

    private JPanel crearPanelImagen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        URL rutaImagen = getClass().getResource("/images/personas.jpg");

        if (rutaImagen != null) {
            panel.add(new ImagenPanel(rutaImagen), BorderLayout.CENTER);
        } else {
            JLabel lblError = new JLabel(
                    "<html><center>No se encontró la imagen<br>/images/personas.jpg</center></html>",
                    SwingConstants.CENTER);
            lblError.setForeground(Color.WHITE);
            lblError.setFont(new Font("Arial", Font.BOLD, 18));
            panel.add(lblError, BorderLayout.CENTER);
        }

        return panel;
    }

    private JButton crearBoton(String texto, Color colorBase, Color colorHover) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setForeground(BLANCO);
        btn.setBackground(colorBase);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(14, 15, 14, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setUI(new BasicButtonUI());

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(colorHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(colorBase);
            }
        });

        return btn;
    }

    private void validarLogin() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtClave.getPassword());

        if (usuario.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese el nombre de usuario.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            txtUsuario.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ingrese la contraseña.",
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
            txtClave.requestFocus();
            return;
        }

        if (!PasswordUtil.validarPassword(password)) {
            JOptionPane.showMessageDialog(
                    this,
                    "La contraseña debe tener entre 8 y 30 caracteres,\n" +
                            "incluir una mayúscula, una minúscula, un número\n" +
                            "y el carácter especial elegido: !",
                    "Validación de contraseña",
                    JOptionPane.WARNING_MESSAGE);
            txtClave.requestFocus();
            return;
        }

        try {
            UsuarioDAO dao = new UsuarioDAO();
            Usuario u = dao.login(usuario, password);

            if (u != null) {
                new MainFrame(u).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "El usuario no existe, está inactivo o no tiene permiso de acceso.",
                        "Acceso denegado",
                        JOptionPane.ERROR_MESSAGE);
                txtClave.setText("");
                txtUsuario.requestFocus();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al validar el acceso: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class ImagenPanel extends JPanel {

        private final Image imagen;

        public ImagenPanel(URL rutaImagen) {
            this.imagen = new ImageIcon(rutaImagen).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (imagen == null) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int panelAncho = getWidth();
            int panelAlto = getHeight();

            int imgAncho = imagen.getWidth(this);
            int imgAlto = imagen.getHeight(this);

            double escalaX = (double) panelAncho / imgAncho;
            double escalaY = (double) panelAlto / imgAlto;
            double escala = Math.max(escalaX, escalaY);

            int nuevoAncho = (int) (imgAncho * escala);
            int nuevoAlto = (int) (imgAlto * escala);

            int x = (panelAncho - nuevoAncho) / 2;
            int y = (panelAlto - nuevoAlto) / 2;

            g2.drawImage(imagen, x, y, nuevoAncho, nuevoAlto, this);
            g2.dispose();
        }
    }
}