package esfe.presentacion;

import esfe.dominio.Movimiento;
import esfe.persistencia.MovimientoDAO;
import esfe.dominio.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// FlatLaf: Look & Feel moderno
import com.formdev.flatlaf.FlatDarkLaf;

public class MainForm extends JFrame {

    private User userAutenticate;

    public User getUserAutenticate() {
        return userAutenticate;
    }

    public void setUserAutenticate(User userAutenticate) {
        this.userAutenticate = userAutenticate;
    }

    public MainForm() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el estilo FlatLaf");
        }

        setTitle("💼 Simulador de Finanzas Domésticas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        createMenu();
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        setJMenuBar(menuBar);

        JMenu menuPerfil = new JMenu("👤 Perfil");
        menuBar.add(menuPerfil);

        JMenuItem itemChangePassword = new JMenuItem("Cambiar contraseña");
        itemChangePassword.addActionListener(e -> {
            ChangePasswordForm changePassword = new ChangePasswordForm(this);
            changePassword.setVisible(true);
        });
        menuPerfil.add(itemChangePassword);

        JMenuItem itemChangeUser = new JMenuItem("Cambiar de usuario");
        itemChangeUser.addActionListener(e -> {
            LoginForm loginForm = new LoginForm(this);
            loginForm.setVisible(true);
        });
        menuPerfil.add(itemChangeUser);

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuPerfil.add(itemSalir);

        JMenu menuMantenimiento = new JMenu("⚙️ Mantenimientos");
        menuBar.add(menuMantenimiento);

        JMenuItem itemUsers = new JMenuItem("Usuarios");
        itemUsers.addActionListener(e -> {
            UserReadingForm userReadingForm = new UserReadingForm(this);
            userReadingForm.setVisible(true);
        });
        menuMantenimiento.add(itemUsers);

        JMenu menuFinanzas = new JMenu("💰 Finanzas");
        menuBar.add(menuFinanzas);

        JMenuItem itemSimulador = new JMenuItem("Simulador de Finanzas");
        itemSimulador.addActionListener(ev -> {
            JFrame ventanaFinanzas = new JFrame("📊 Simulador de Finanzas Domésticas");
            ventanaFinanzas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ventanaFinanzas.setSize(800, 600);
            ventanaFinanzas.setLocationRelativeTo(null);
            ventanaFinanzas.add(new FinancePanel());
            ventanaFinanzas.setVisible(true);
        });
        menuFinanzas.add(itemSimulador);
    }

    public class FinancePanel extends JPanel {

        private JTextField descripcionField, montoField, fechaField;
        private JComboBox<String> tipoBox;
        private JTable tabla;
        private JLabel resumenLabel, consejoLabel;

        public FinancePanel() {
            setLayout(new BorderLayout(15, 15));
            setBorder(new EmptyBorder(15, 15, 15, 15));
            setBackground(UIManager.getColor("Panel.background"));

            Font fuente = new Font("Segoe UI", Font.PLAIN, 14);
            Font fuenteBold = new Font("Segoe UI", Font.BOLD, 14);

            // Formulario de ingreso
            JPanel topPanel = new JPanel(new GridLayout(2, 5, 10, 10));
            topPanel.setBorder(BorderFactory.createTitledBorder("➕ Agregar Movimiento"));
            topPanel.setBackground(getBackground());

            tipoBox = new JComboBox<>(new String[]{"Ingreso", "Egreso"});
            descripcionField = new JTextField();
            montoField = new JTextField();
            fechaField = new JTextField();
            JButton agregarBtn = new JButton("Agregar");

            tipoBox.setFont(fuente);
            descripcionField.setFont(fuente);
            montoField.setFont(fuente);
            fechaField.setFont(fuente);
            agregarBtn.setFont(fuenteBold);

            topPanel.add(new JLabel("Tipo:", JLabel.RIGHT));
            topPanel.add(new JLabel("Descripción:", JLabel.RIGHT));
            topPanel.add(new JLabel("Monto:", JLabel.RIGHT));
            topPanel.add(new JLabel("Fecha (YYYY-MM-DD):", JLabel.RIGHT));
            topPanel.add(new JLabel(""));

            topPanel.add(tipoBox);
            topPanel.add(descripcionField);
            topPanel.add(montoField);
            topPanel.add(fechaField);
            topPanel.add(agregarBtn);

            add(topPanel, BorderLayout.NORTH);

            // Tabla de movimientos
            tabla = new JTable(new DefaultTableModel(new String[]{"ID", "Tipo", "Descripción", "Monto", "Fecha"}, 0));
            tabla.setFont(fuente);
            tabla.setRowHeight(24);
            tabla.setFillsViewportHeight(true);
            tabla.setSelectionBackground(new Color(75, 110, 175));
            tabla.setGridColor(new Color(80, 80, 80));
            tabla.getTableHeader().setFont(fuenteBold);
            tabla.getTableHeader().setBackground(new Color(40, 40, 40));
            tabla.getTableHeader().setForeground(Color.WHITE);

            JScrollPane scroll = new JScrollPane(tabla);
            scroll.setBorder(BorderFactory.createTitledBorder("📑 Historial de Movimientos"));
            add(scroll, BorderLayout.CENTER);

            // Panel inferior: resumen + consejo
            JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
            bottomPanel.setBorder(BorderFactory.createTitledBorder("📋 Resumen"));
            bottomPanel.setBackground(getBackground());

            resumenLabel = new JLabel("Resumen: ");
            consejoLabel = new JLabel("Consejo: ");
            resumenLabel.setFont(fuenteBold);
            consejoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));

            resumenLabel.setForeground(new Color(102, 255, 178)); // verde
            consejoLabel.setForeground(Color.LIGHT_GRAY);

            bottomPanel.add(resumenLabel);
            bottomPanel.add(consejoLabel);
            add(bottomPanel, BorderLayout.SOUTH);

            // Acción del botón Agregar
            agregarBtn.addActionListener(e -> {
                try {
                    String tipo = (String) tipoBox.getSelectedItem();
                    String desc = descripcionField.getText();
                    double monto = Double.parseDouble(montoField.getText());
                    String fecha = fechaField.getText();

                    Movimiento m = new Movimiento(tipo, desc, monto, fecha);
                    MovimientoDAO.agregarMovimiento(m);
                    actualizarTabla();
                    mostrarResumen();
                    mostrarConsejo();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            actualizarTabla();
            mostrarResumen();
            mostrarConsejo();
        }

        private void actualizarTabla() {
            DefaultTableModel model = (DefaultTableModel) tabla.getModel();
            model.setRowCount(0);
            List<Movimiento> lista = MovimientoDAO.obtenerMovimientos();
            for (Movimiento m : lista) {
                model.addRow(new Object[]{m.id, m.tipo, m.descripcion, m.monto, m.fecha});
            }
        }

        private void mostrarResumen() {
            double ingresos = MovimientoDAO.calcularTotalPorTipo("Ingreso");
            double egresos = MovimientoDAO.calcularTotalPorTipo("Egreso");
            resumenLabel.setText("Resumen → Ingresos: $" + ingresos + " | Egresos: $" + egresos);
        }

        private void mostrarConsejo() {
            double ingresos = MovimientoDAO.calcularTotalPorTipo("Ingreso");
            double egresos = MovimientoDAO.calcularTotalPorTipo("Egreso");
            String consejo = "Buen trabajo.";
            if (egresos > ingresos) {
                consejo = "¡Cuidado! Estás gastando más de lo que ganas.";
            } else if (ingresos - egresos > 500) {
                consejo = "¡Genial! Considera ahorrar o invertir.";
            }
            consejoLabel.setText("Consejo: " + consejo);
        }
    }
}
