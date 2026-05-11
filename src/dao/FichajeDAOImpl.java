package dao;

import db.ConexionDB;
import dto.FichajeDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FichajeDAOImpl implements FichajeDAO{

    @Override
    public boolean asignarEquipo(int idJugador, int idEquipo) {
        String sql = "INSERT INTO fichajes(jugador_id, equipo_id, fecha_fichaje) VALUES (?,?, CURDATE())";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            ps.setInt(2, idEquipo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<FichajeDTO> listarFichajes() {
        List <FichajeDTO> lista = new ArrayList<>();
        String sql = "SELECT f.jugador_id, f.equipo_id, u.nombre, u.apellidos, e.nombre AS nombre_equipo, j.posicion "
                + "FROM fichajes f "
                + "JOIN jugadores j ON f.jugador_id = j.usuario_id "
                + "JOIN usuarios u ON j.usuario_id = u.id "
                + "JOIN equipos e ON f.equipo_id = e.id";
        try (Connection conn = ConexionDB.getConnection(); 
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                FichajeDTO dto = new FichajeDTO();

                dto.setIdJugador(rs.getInt("Jugador_id"));
                dto.setIdEquipo(rs.getInt("Equipo_id"));
                // Combinamos nombre y apellido para el DTO
                dto.setNombreJugador(rs.getString("nombre") + " " + rs.getString("apellidos"));
                dto.setNombreEquipo(rs.getString("nombre_equipo"));
                dto.setPosicion(rs.getString("posicion"));
                lista.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean eliminarFichaje(int idJugador, int idEquipo) {
        String sql = "DELETE FROM fichajes WHERE jugador_id = ? AND equipo_id = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            ps.setInt(2, idEquipo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
