package com.gimnasio.ui;

import com.gimnasio.dao.UsuarioDAO;
import com.gimnasio.model.Usuario;
import com.gimnasio.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtClave;

    private final Color ROJO = new Color(220, 38, 38);
    private final Color NARANJA = new Color(255, 111, 0);

    public LoginFrame() {
        setTitle("Login - Sistema de Gestión Interno para Gimnasio");
        setSize(980, 620);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);

        construir();
    }

    private void construir() {
        JPanel fondo = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Fondo rojo oscuro limpio
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(12, 12, 12),
                        w, h, new Color(120, 10, 10));
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                // Sombra lateral izquierda para profundidad
                GradientPaint sombraIzquierda = new GradientPaint(
                        0, 0, new Color(0, 0, 0, 120),
                        w / 2, 0, new Color(0, 0, 0, 0));
                g2.setPaint(sombraIzquierda);
                g2.fillRect(0, 0, w, h);

                // Sombra inferior suave
                GradientPaint sombraInferior = new GradientPaint(
                        0, h - 180, new Color(0, 0, 0, 0),
                        0, h, new Color(0, 0, 0, 80));
                g2.setPaint(sombraInferior);
                g2.fillRect(0, h - 180, w, 180);

                // Línea decorativa inferior, como el diseño inicial
                g2.setColor(new Color(255, 255, 255, 35));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, h - 120, w, h - 170);

                g2.dispose();
            }
        };

        JPanel card = new JPanel(null);
        card.setBackground(new Color(12, 12, 12, 245));
        card.setBounds(300, 55, 390, 490);
        fondo.add(card);

        JLabel titulo = new JLabel("<html><center>SISTEMA DE GESTIÓN<br>INTERNO PARA GIMNASIO</center></html>");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBounds(20, 35, 350, 80);
        card.add(titulo);

        JLabel subtitulo = new JLabel("Acceso administrativo del sistema");
        subtitulo.setForeground(new Color(220, 220, 220));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        subtitulo.setBounds(25, 120, 340, 25);
        card.add(subtitulo);

        JLabel lblUsuario = etiqueta("Usuario");
        lblUsuario.setBounds(45, 165, 300, 22);
        card.add(lblUsuario);

        txtUsuario = campoTexto();
        txtUsuario.setBounds(45, 190, 300, 42);
        card.add(txtUsuario);

        JLabel lblClave = etiqueta("Contraseña");
        lblClave.setBounds(45, 245, 300, 22);
        card.add(lblClave);

        txtClave = campoPassword();
        txtClave.setBounds(45, 270, 300, 42);
        card.add(txtClave);

        JButton btnLogin = botonPrincipal("INICIAR SESIÓN");
        btnLogin.setBounds(45, 342, 300, 48);
        card.add(btnLogin);

        JButton btnCancelar = botonSecundario("CANCELAR");
        btnCancelar.setBounds(45, 406, 300, 42);
        card.add(btnCancelar);

        JLabel pie = new JLabel(
                "POWER GYM SYSTEM  |  Socios  •  Membresías  •  Asistencia  •  Inventario  •  Reportes");
        pie.setForeground(Color.WHITE);
        pie.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pie.setBounds(25, 535, 850, 25);
        fondo.add(pie);

        fondo.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int x = (fondo.getWidth() - card.getWidth()) / 2;
                int y = (fondo.getHeight() - card.getHeight()) / 2 - 10;

                card.setLocation(x, Math.max(y, 25));
                pie.setBounds(25, fondo.getHeight() - 45, 850, 25);
            }
        });

        btnLogin.addActionListener(e -> login());
        btnCancelar.addActionListener(e -> System.exit(0));
        getRootPane().setDefaultButton(btnLogin);

        setContentPane(fondo);

        SwingUtilities.invokeLater(() -> txtUsuario.requestFocusInWindow());
    }

    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JTextField campoTexto() {
        JTextField campo = new JTextField();
        campo.setUI(new BasicTextFieldUI());
        campo.setOpaque(true);
        campo.setEditable(true);
        campo.setEnabled(true);
        campo.setBackground(Color.WHITE);
        campo.setForeground(Color.BLACK);
        campo.setCaretColor(Color.BLACK);
        campo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return campo;
    }

    private JPasswordField campoPassword() {
        JPasswordField campo = new JPasswordField();
        campo.setUI(new BasicPasswordFieldUI());
        campo.setOpaque(true);
        campo.setEditable(true);
        campo.setEnabled(true);
        campo.setBackground(Color.WHITE);
        campo.setForeground(Color.BLACK);
        campo.setCaretColor(Color.BLACK);
        campo.setEchoChar('●');
        campo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        return campo;
    }

    private JButton botonPrincipal(String texto) {
        JButton btn = new JButton(texto);
        btn.setUI(new BasicButtonUI());
        btn.setBackground(NARANJA);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ROJO);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(NARANJA);
            }
        });

        return btn;
    }

    private JButton botonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setUI(new BasicButtonUI());
        btn.setBackground(new Color(55, 55, 55));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(85, 85, 85));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(55, 55, 55));
            }
        });

        return btn;
    }

    private void login() {
        try {
            String usuario = txtUsuario.getText().trim();
            String clave = new String(txtClave.getPassword());

            if (usuario.isEmpty() || clave.isEmpty()) {
                SwingUtil.error(this, "Debe ingresar usuario y contraseña.");
                return;
            }

            Usuario u = new UsuarioDAO().login(usuario, clave);

            if (u == null) {
                SwingUtil.error(this, "Credenciales incorrectas o usuario inactivo.");
                return;
            }

            new MainFrame(u).setVisible(true);
            dispose();

        } catch (Exception ex) {
            SwingUtil.error(this, "Error al iniciar sesión: " + ex.getMessage());
        }
    }
}