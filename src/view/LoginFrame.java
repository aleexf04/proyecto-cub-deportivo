package view;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnRegister;
    private UsuarioDAO usuarioDAO;

    public LoginFrame() {
        usuarioDAO = new UsuarioDAOImpl(); // Inicializamos el DAO
        
        setTitle("Club Deportivo - Acceso");
        setSize(350, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla
        setLayout(new GridLayout(4, 1, 10, 10));

        // Componentes
        txtUser = new JTextField();
        txtPass = new JPasswordField();
        btnLogin = new JButton("Entrar");
        btnRegister = new JButton("¿No tienes cuenta? Regístrate");

        // Diseño sencillo
        add(new JLabel("  Usuario:", JLabel.LEFT));
        add(txtUser);
        add(new JLabel("  Contraseña:", JLabel.LEFT));
        add(txtPass);
        add(btnLogin);
        add(btnRegister);

        // Evento del botón login
        btnLogin.addActionListener(e -> ejecutarLogin());

        // Evento para abrir registro
        btnRegister.addActionListener(e -> {
            new RegistroFrame().setVisible(true);
            this.dispose();
        });
    }

    private void ejecutarLogin() {
    String user = txtUser.getText();
    String pass = new String(txtPass.getPassword());

    if (usuarioDAO.login(user, pass)) {
        JOptionPane.showMessageDialog(this, "¡Bienvenido al Club!");
        new MainDashboard().setVisible(true); // <--- AHORA ABRE EL DASHBOARD
        this.dispose(); 
    } else {
        JOptionPane.showMessageDialog(this, "Error", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
}