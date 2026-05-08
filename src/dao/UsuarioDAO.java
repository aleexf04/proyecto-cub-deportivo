package dao;
import model.Usuario;
import model.Jugador;
import model.Entrenador;

public interface UsuarioDAO {
    boolean login(String user, String pass);
    // Usamos el objeto específico según el rol
    boolean registrarJugador(Jugador j);
    boolean registrarEntrenador(Entrenador e);
}