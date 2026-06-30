package com.gimnasio.ui;

import com.gimnasio.model.Usuario;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class MainFrame extends JFrame {

    private final Usuario usuario;

    private JPanel contentPanel;

    private JButton btnSocios;
    private JButton btnMembresias;
    private JButton btnAsistencia;
    private JButton btnInventario;
    private JButton btnReportes;

    private final Color NEGRO = new Color(15, 15, 15);
    private final Color NEGRO_MENU = new Color(20, 20, 20);
    private final Color GRIS_MENU = new Color(45, 45, 45);
    private final Color GRIS_HOVER = new Color(65, 65, 65);
    private final Color GRIS_FONDO = new Color(245, 245, 245);
    private final Color BLANCO = Color.WHITE;
    private final Color ROJO = new Color(234, 36, 36);
    private final Color ROJO_HOVER = new Color(245, 70, 70);
    private final Color BORDE = new Color(220, 220, 220);
    private final Color TEXTO_OSCURO = new Color(20, 20, 20);
    private final Color TEXTO_SECUNDARIO = new Color(80, 80, 80);
    private final Color VERDE = new Color(34, 197, 94);

    public MainFrame(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Sistema de Gestión Interno para Gimnasio - "
                + usuario.getNombreUsuario()
                + " (" + usuario.getRol() + ")");

        setSize(1400, 760);
        setMinimumSize(new Dimension(1180, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        build();
        mostrarModulo("Socios");
    }

    private void build() {
        setLayout(new BorderLayout());

        add(crearHeader(), BorderLayout.NORTH);
        add(crearSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(GRIS_FONDO);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NEGRO);
        header.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JPanel left = new JPanel(new BorderLayout(15, 0));
        left.setOpaque(false);

        JLabel lblLogo = new JLabel();
        lblLogo.setOpaque(false);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setVerticalAlignment(SwingConstants.CENTER);
        lblLogo.setPreferredSize(new Dimension(70, 70));

        try {
            URL logoURL = getClass().getResource("/images/logo.png");

            if (logoURL != null) {
                ImageIcon iconoOriginal = new ImageIcon(logoURL);
                Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(62, 62, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(imagenEscalada));
            } else {
                System.out.println("No se encontró el logo en /images/logo.png");
            }

        } catch (Exception e) {
            System.out.println("Error al cargar logo: " + e.getMessage());
        }

        JPanel textoHeader = new JPanel();
        textoHeader.setOpaque(false);
        textoHeader.setLayout(new BoxLayout(textoHeader, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel("SISTEMA DE GESTIÓN INTERNO PARA GIMNASIO");
        lblTitulo.setForeground(BLANCO);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        String subtitulo;

        if (esAdministrador()) {
            subtitulo = "Panel administrativo  |  Control de socios, membresías, asistencia, inventario y reportes";
        } else if (esRecepcionista()) {
            subtitulo = "Panel de recepción  |  Atención de socios, membresías, pagos y asistencia";
        } else {
            subtitulo = "Panel del sistema  |  Acceso según perfil de usuario";
        }

        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setForeground(new Color(220, 220, 220));
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        textoHeader.add(Box.createVerticalStrut(6));
        textoHeader.add(lblTitulo);
        textoHeader.add(Box.createVerticalStrut(10));
        textoHeader.add(lblSubtitulo);

        left.add(lblLogo, BorderLayout.WEST);
        left.add(textoHeader, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        JLabel lblUsuario = new JLabel(
                "<html><div style='text-align:right;'>"
                        + "Usuario: <b>" + usuario.getNombreUsuario() + "</b><br>"
                        + "Rol: <b>" + usuario.getRol() + "</b>"
                        + "</div></html>");
        lblUsuario.setForeground(BLANCO);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnCerrarSesion = crearBotonSuperior("Cerrar sesión");
        btnCerrarSesion.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        right.add(lblUsuario);
        right.add(btnCerrarSesion);

        header.add(left, BorderLayout.CENTER);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(NEGRO_MENU);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 15, 25, 15));

        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        JLabel lblMenu = new JLabel("MENÚ PRINCIPAL");
        lblMenu.setForeground(BLANCO);
        lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnSocios = crearBotonMenu("Socios");
        btnMembresias = crearBotonMenu("Membresías y pagos");
        btnAsistencia = crearBotonMenu("Asistencia");

        btnSocios.addActionListener(e -> mostrarModulo("Socios"));
        btnMembresias.addActionListener(e -> mostrarModulo("Membresías y pagos"));
        btnAsistencia.addActionListener(e -> mostrarModulo("Asistencia"));

        menuPanel.add(lblMenu);
        menuPanel.add(Box.createVerticalStrut(25));

        menuPanel.add(btnSocios);
        menuPanel.add(Box.createVerticalStrut(12));

        menuPanel.add(btnMembresias);
        menuPanel.add(Box.createVerticalStrut(12));

        menuPanel.add(btnAsistencia);
        menuPanel.add(Box.createVerticalStrut(12));

        if (esAdministrador()) {
            btnInventario = crearBotonMenu("Inventario");
            btnReportes = crearBotonMenu("Reportes");

            btnInventario.addActionListener(e -> mostrarModulo("Inventario"));
            btnReportes.addActionListener(e -> mostrarModulo("Reportes"));

            menuPanel.add(btnInventario);
            menuPanel.add(Box.createVerticalStrut(12));

            menuPanel.add(btnReportes);
        }

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));

        JLabel lblSistema = new JLabel("POWER GYM SYSTEM");
        lblSistema.setForeground(BLANCO);
        lblSistema.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblVersion = new JLabel("Gestión interna v1.0");
        lblVersion.setForeground(new Color(180, 180, 180));
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblEstado = new JLabel("● Sistema activo");
        lblEstado.setForeground(VERDE);
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel lblPerfil = new JLabel("Perfil: " + usuario.getRol());
        lblPerfil.setForeground(new Color(180, 180, 180));
        lblPerfil.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        footer.add(lblSistema);
        footer.add(Box.createVerticalStrut(8));
        footer.add(lblVersion);
        footer.add(Box.createVerticalStrut(10));
        footer.add(lblEstado);
        footer.add(Box.createVerticalStrut(10));
        footer.add(lblPerfil);

        sidebar.add(menuPanel, BorderLayout.NORTH);
        sidebar.add(footer, BorderLayout.SOUTH);

        return sidebar;
    }

    private void mostrarModulo(String modulo) {
        if (!tienePermiso(modulo)) {
            JOptionPane.showMessageDialog(
                    this,
                    "No tiene permiso para acceder al módulo: " + modulo,
                    "Acceso restringido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        marcarMenuActivo(modulo);
        contentPanel.removeAll();

        JPanel panelModulo = null;
        String titulo = "";
        String descripcion = "";

        switch (modulo) {
            case "Socios":
                titulo = "Mantenimiento de socios";
                descripcion = "Registro, consulta, actualización y control del estado de los clientes del gimnasio.";
                panelModulo = new SocioPanel(usuario);
                break;

            case "Membresías y pagos":
                titulo = "Membresías y pagos";
                descripcion = "Administración de planes, pagos, vencimientos y métodos de cobro de los socios.";
                panelModulo = new MembresiaPagoPanel(usuario);
                break;

            case "Asistencia":
                titulo = "Control de asistencia";
                descripcion = "Validación de ingreso por DNI o QR, verificación de membresía vigente e historial de accesos.";
                panelModulo = new AsistenciaPanel(usuario);
                break;

            case "Inventario":
                titulo = "Inventario";
                descripcion = "Control de productos, precios, stock disponible y estado de artículos del gimnasio.";
                panelModulo = new ProductoPanel(usuario);
                break;

            case "Reportes":
                titulo = "Reportes administrativos";
                descripcion = "Consulta de socios activos, morosidad, inventario e ingresos mensuales del sistema.";
                panelModulo = new ReportesPanel();
                break;

            default:
                JOptionPane.showMessageDialog(
                        this,
                        "Módulo no encontrado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
        }

        contentPanel.add(crearContenedorModulo(titulo, descripcion, panelModulo), BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel crearContenedorModulo(String titulo, String descripcion, JPanel contenido) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 14));
        wrapper.setBackground(GRIS_FONDO);

        JPanel top = new JPanel(new BorderLayout(20, 0));
        top.setBackground(BLANCO);
        top.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(20, 26, 20, 26)));

        JPanel textoPanel = new JPanel();
        textoPanel.setOpaque(false);
        textoPanel.setLayout(new BoxLayout(textoPanel, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(TEXTO_OSCURO);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel lineaRoja = new JPanel();
        lineaRoja.setBackground(ROJO);
        lineaRoja.setPreferredSize(new Dimension(720, 4));
        lineaRoja.setMaximumSize(new Dimension(720, 4));
        lineaRoja.setMinimumSize(new Dimension(720, 4));
        lineaRoja.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDescripcion = new JLabel(descripcion);
        lblDescripcion.setForeground(TEXTO_SECUNDARIO);
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDescripcion.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        textoPanel.add(lblTitulo);
        textoPanel.add(Box.createVerticalStrut(10));
        textoPanel.add(lineaRoja);
        textoPanel.add(lblDescripcion);

        JLabel badge = new JLabel("Módulo activo", SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(ROJO);
        badge.setForeground(BLANCO);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badge.setPreferredSize(new Dimension(170, 90));

        top.add(textoPanel, BorderLayout.CENTER);
        top.add(badge, BorderLayout.EAST);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BLANCO);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        card.add(contenido, BorderLayout.CENTER);

        wrapper.add(top, BorderLayout.NORTH);
        wrapper.add(card, BorderLayout.CENTER);

        return wrapper;
    }

    private void marcarMenuActivo(String modulo) {
        if (btnSocios != null) {
            btnSocios.setBackground(GRIS_MENU);
        }

        if (btnMembresias != null) {
            btnMembresias.setBackground(GRIS_MENU);
        }

        if (btnAsistencia != null) {
            btnAsistencia.setBackground(GRIS_MENU);
        }

        if (btnInventario != null) {
            btnInventario.setBackground(GRIS_MENU);
        }

        if (btnReportes != null) {
            btnReportes.setBackground(GRIS_MENU);
        }

        switch (modulo) {
            case "Socios":
                if (btnSocios != null) {
                    btnSocios.setBackground(ROJO);
                }
                break;

            case "Membresías y pagos":
                if (btnMembresias != null) {
                    btnMembresias.setBackground(ROJO);
                }
                break;

            case "Asistencia":
                if (btnAsistencia != null) {
                    btnAsistencia.setBackground(ROJO);
                }
                break;

            case "Inventario":
                if (btnInventario != null) {
                    btnInventario.setBackground(ROJO);
                }
                break;

            case "Reportes":
                if (btnReportes != null) {
                    btnReportes.setBackground(ROJO);
                }
                break;
        }
    }

    private JButton crearBotonMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setUI(new BasicButtonUI());
        btn.setBackground(GRIS_MENU);
        btn.setForeground(BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setPreferredSize(new Dimension(180, 48));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!btn.getBackground().equals(ROJO)) {
                    btn.setBackground(GRIS_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!btn.getBackground().equals(ROJO)) {
                    btn.setBackground(GRIS_MENU);
                }
            }
        });

        return btn;
    }

    private JButton crearBotonSuperior(String texto) {
        JButton btn = new JButton(texto);
        btn.setUI(new BasicButtonUI());
        btn.setBackground(ROJO);
        btn.setForeground(BLANCO);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(135, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ROJO_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(ROJO);
            }
        });

        return btn;
    }

    private boolean esAdministrador() {
        return usuario.getRol() != null && usuario.getRol().equalsIgnoreCase("Administrador");
    }

    private boolean esRecepcionista() {
        return usuario.getRol() != null && usuario.getRol().equalsIgnoreCase("Recepcionista");
    }

    private boolean tienePermiso(String modulo) {
        if (esAdministrador()) {
            return true;
        }

        if (esRecepcionista()) {
            return modulo.equals("Socios")
                    || modulo.equals("Membresías y pagos")
                    || modulo.equals("Asistencia");
        }

        return false;
    }
}