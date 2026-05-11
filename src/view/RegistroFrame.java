package view;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import java.awt.*;
import javax.swing.*;
import model.Entrenador;
import model.Jugador;

public class RegistroFrame extends JFrame {

    // Componentes comunes
    private JTextField txtUser, txtEmail, txtNombre, txtApellidos;
    private JPasswordField txtPass;
    private JComboBox<String> comboRol;
    private JButton btnRegistrar, btnVolver;

    // Componentes específicos (Dinámicos)
    private JPanel panelDinamico;
    private JTextField txtDato1, txtDato2;
    private JLabel lblDato1, lblDato2;

    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    public RegistroFrame() {
        setTitle("Registro de Nuevo Miembro");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel Norte: Datos Generales ---
        JPanel pnlGeneral = new JPanel(new GridLayout(6, 2, 5, 5));
        pnlGeneral.setBorder(BorderFactory.createTitledBorder("Datos de Usuario"));

        pnlGeneral.add(new JLabel("Username:"));
        txtUser = new JTextField();
        pnlGeneral.add(txtUser);
        pnlGeneral.add(new JLabel("Password:"));
        txtPass = new JPasswordField();
        pnlGeneral.add(txtPass);
        pnlGeneral.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        pnlGeneral.add(txtEmail);
        pnlGeneral.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        pnlGeneral.add(txtNombre);
        pnlGeneral.add(new JLabel("Apellidos:"));
        txtApellidos = new JTextField();
        pnlGeneral.add(txtApellidos);
        pnlGeneral.add(new JLabel("Rol:"));
        comboRol = new JComboBox<>(new String[]{"JUGADOR", "ENTRENADOR"});
        pnlGeneral.add(comboRol);

        // --- Panel Central: Campos Dinámicos ---
        panelDinamico = new JPanel(new GridLayout(2, 2, 5, 5));
        panelDinamico.setBorder(BorderFactory.createTitledBorder("Información Específica"));
        lblDato1 = new JLabel("Posición:");
        txtDato1 = new JTextField();
        lblDato2 = new JLabel("Dorsal:");
        txtDato2 = new JTextField();
        panelDinamico.add(lblDato1);
        panelDinamico.add(txtDato1);
        panelDinamico.add(lblDato2);
        panelDinamico.add(txtDato2);

        // --- Panel Sur: Botones ---
        JPanel pnlBotones = new JPanel();
        btnRegistrar = new JButton("Finalizar Registro");
        btnVolver = new JButton("Volver");
        pnlBotones.add(btnRegistrar);
        pnlBotones.add(btnVolver);

        add(pnlGeneral, BorderLayout.NORTH);
        add(panelDinamico, BorderLayout.CENTER);
        add(pnlBotones, BorderLayout.SOUTH);

        // --- LÓGICA DINÁMICA ---
        comboRol.addActionListener(e -> actualizarCampos());

        btnRegistrar.addActionListener(e -> registrar());

        btnVolver.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void actualizarCampos() {
        if (comboRol.getSelectedItem().equals("JUGADOR")) {
            lblDato1.setText("Posición:");
            lblDato2.setText("Dorsal:");
        } else {
            lblDato1.setText("Especialidad:");
            lblDato2.setText("Años Exp:");
        }
    }

    private void registrar() {
        String rol = (String) comboRol.getSelectedItem();
        boolean exito = false;

        if (rol.equals("JUGADOR")) {
            Jugador j = new Jugador();
            rellenarDatosBase(j);
            j.setPosicion(txtDato1.getText());
            j.setDorsal(Integer.parseInt(txtDato2.getText()));
            exito = usuarioDAO.registrarJugador(j);
        } else {
            Entrenador ent = new Entrenador();
            rellenarDatosBase(ent);
            ent.setEspecialidad(txtDato1.getText());
            ent.setExperienciaAnios(Integer.parseInt(txtDato2.getText()));
            exito = usuarioDAO.registrarEntrenador(ent);
        }

        if (exito) {
            JOptionPane.showMessageDialog(this, "Registro completado con éxito");
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar. Revisa los datos.");
        }
    }

    private void rellenarDatosBase(model.Usuario u) {
        u.setUsername(txtUser.getText());
        u.setPassword(new String(txtPass.getPassword()));
        u.setEmail(txtEmail.getText());
        u.setNombre(txtNombre.getText());
        u.setApellidos(txtApellidos.getText());
        u.setRol((String) comboRol.getSelectedItem());
    }
}
