package dao;

import dto.FichajeDTO;
import java.util.List;

public interface FichajeDAO {
    boolean asignarEquipo(int idJugador, int idEquipo);
    List<FichajeDTO> listarFichajes();
    boolean eliminarFichaje(int idJugador, int idEquipo);
}
