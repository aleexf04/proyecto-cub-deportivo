package dao;
import model.Entrenador;
import model.Jugador;
import model.Usuario;

public interface UsuarioDAO {
    boolean login(String user, String pass);
    // Usamos el objeto específico según el rol
    boolean registrarJugador(Jugador j);
    boolean registrarEntrenador(Entrenador e);
    java.util.List<model.Jugador> listarJugadores();
    Usuario validar(String username, String password);
}