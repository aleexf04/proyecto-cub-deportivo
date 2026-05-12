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

    //Creamos la pestaña de registro
    public RegistroFrame() {
        setTitle("Registro de Nuevo Miembro");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Panel Norte: Datos Generales ---
        JPanel pnlGeneral = new JPanel(new GridLayout(6, 2, 5, 5));
        pnlGeneral.setBorder(BorderFactory.createTitledBorder("Datos de Usuario"));

        //Añadimos todos los valores, con sus labels para que puedan añadir texto y vean el identificativo
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


        //Aqui viene la dificultad
        // --- Panel Central: Campos Dinámicos ---
        panelDinamico = new JPanel(new GridLayout(2, 2, 5, 5));
        panelDinamico.setBorder(BorderFactory.createTitledBorder("Información Específica"));
        //Primeramente los vamos a inicializar como si estuvieramos añadiendo un jugador para que los label 
        // no se vean vacíos pero más adelante veremos como dependiendo de la opción del comBox cambian
        lblDato1 = new JLabel("Posición:");
        txtDato1 = new JTextField();
        lblDato2 = new JLabel("Dorsal:");
        txtDato2 = new JTextField();
        panelDinamico.add(lblDato1);
        panelDinamico.add(txtDato1);
        panelDinamico.add(lblDato2);
        panelDinamico.add(txtDato2);

        // --- Botones ---
        //Aqui añadimos los botones para finalizar el registro o volver atrás
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


    //Aquí es donde cambiamos los campos dinámicos, es decir que si el item del comboRol es JUGADOR
    // los campos serán Posicion y Dorsal, pero si es otro (ENTRENADOR), sus campos serán diferentes
    private void actualizarCampos() {
        if (comboRol.getSelectedItem().equals("JUGADOR")) {
            lblDato1.setText("Posición:");
            lblDato2.setText("Dorsal:");
        } else {
            lblDato1.setText("Especialidad:");
            lblDato2.setText("Años Exp:");
        }
    }

    //Le volvemos a hacer un casting para que el rol salga en tipo String
    private void registrar() {
        String rol = (String) comboRol.getSelectedItem();
        boolean exito = false;

        //Aqui comprobamos si el equasl es jugador se crea un jugador y si no lo es en el else se crea Entrenador
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

        //con la variable booleana vemos si se hac completado correctamente los dos campos y salta un mensaje de confirmación o error
        if (exito) {
            JOptionPane.showMessageDialog(this, "Registro completado con éxito");
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar. Revisa los datos.");
        }
    }


    //este método lo que hace es asignar los datos que hay en los cuadros de texto a la creación de usuarios
    // u es el usuario vacío al que se le está asignando cada atributo, nombre, contraseña, email, etc etc
    private void rellenarDatosBase(model.Usuario u) {
        u.setUsername(txtUser.getText());
        u.setPassword(new String(txtPass.getPassword()));// lo creamos como String para que coincida el dato con el creado en Usuario
        u.setEmail(txtEmail.getText());
        u.setNombre(txtNombre.getText());
        u.setApellidos(txtApellidos.getText());
        u.setRol((String) comboRol.getSelectedItem());
    }
}
