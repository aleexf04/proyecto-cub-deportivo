package view;

import dao.UsuarioDAO;
import dao.UsuarioDAOImpl;
import java.awt.GridLayout;
import javax.swing.*;
import model.Usuario;

public class LoginFrame extends JFrame{
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnRegister;
    private UsuarioDAO usuarioDAO;

    public LoginFrame(){
        usuarioDAO = new UsuarioDAOImpl();

        setTitle("Club Deportivo - Acceso");
        setSize(350, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(0, 1, 10, 10));

        //Componentes
        txtUser = new JTextField();
        txtPass = new JPasswordField();
        btnLogin = new JButton("Entrar");
        btnRegister = new JButton("¿No tienes cuenta? Regístrate");


        //Diseño sencillo
        add(new JLabel("  Usuario:", JLabel.LEFT));
        add(txtUser);
        add(new JLabel("  Contraseña:", JLabel.LEFT));
        add(txtPass);
        add(btnLogin);
        add(btnRegister);

        //Evento del login
        btnLogin.addActionListener(e -> ejecutarLogin());

        //Evento para el registro
        btnRegister.addActionListener(e -> {
            new RegistroFrame().setVisible(true);
            this.dispose();
        });
    }

    private void ejecutarLogin(){
        String username = txtUser.getText();
        String pass = new String(txtPass.getPassword());

        Usuario user = usuarioDAO.validar(username, pass);

        if(user != null){
            JOptionPane.showMessageDialog(this, "¡Bienvenido, " + user.getUsername());

            new MainDashboard(user).setVisible(true);
            dispose();
        }else{
            JOptionPane.showMessageDialog(this, "Error: usuario o contraseña incorrectos");
        }
    }
}