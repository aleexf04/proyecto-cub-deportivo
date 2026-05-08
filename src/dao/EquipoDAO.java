package dao;
import model.Equipo;
import java.util.List;

public interface EquipoDAO {
    boolean insertar(Equipo e);
    boolean eliminar(int id);
    List<Equipo> listarTodos();
}