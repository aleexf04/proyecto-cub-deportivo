package dao;

import db.ConexionDB;
import model.Jugador;
import java.sql.*;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public boolean registrarJugador(Jugador j) {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); // Iniciamos transacción manual

            // 1. Insertar en tabla USUARIOS
            String sqlUser = "INSERT INTO usuarios (username, password, email, nombre, apellidos, rol) VALUES (?,?,?,?,?,?)";
            PreparedStatement psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, j.getUsername());
            psUser.setString(2, j.getPassword());
            psUser.setString(3, j.getEmail());
            psUser.setString(4, j.getNombre());
            psUser.setString(5, j.getApellidos());
            psUser.setString(6, "JUGADOR");
            psUser.executeUpdate();

            // Obtener el ID generado
            ResultSet rs = psUser.getGeneratedKeys();
            if (rs.next()) {
                int newId = rs.getInt(1);
                
                // 2. Insertar en tabla JUGADORES usando ese ID
                String sqlJug = "INSERT INTO jugadores (usuario_id, posicion, dorsal) VALUES (?,?,?)";
                PreparedStatement psJug = conn.prepareStatement(sqlJug);
                psJug.setInt(1, newId);
                psJug.setString(2, j.getPosicion());
                psJug.setInt(3, j.getDorsal());
                psJug.executeUpdate();
            }

            conn.commit(); // Si todo va bien, guardamos
            return true;
        } catch (SQLException ex) {
            if (conn != null) try { conn.rollback(); } catch (SQLException e) { e.printStackTrace(); }
            ex.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public boolean login(String user, String pass) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean registrarEntrenador(model.Entrenador e) {
        // Lógica idéntica a registrarJugador pero apuntando a la tabla 'entrenadores'
        return false; // Implementar siguiendo el ejemplo de arriba
    }
}