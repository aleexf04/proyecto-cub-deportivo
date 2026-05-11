package dao;

import dto.FichajeDTO;
impor java.util.List;

public class FichajeDAO {
    boolean asignarEquipo(int idJugador, int idEquipo);
    List<FichajeDTO> listarFichajes();
}
