import javax.swing.SwingUtilities;
import view.LoginFrame;

public class Main {
    public static void main(String[] args) {
        // Ejecutamos la interfaz en el hilo de eventos de Swing (buena práctica)
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}