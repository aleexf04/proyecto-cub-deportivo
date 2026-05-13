package dao;

import db.ConexionDB;
import java.sql.*;
import model.Entrenador;
import model.Jugador;
import model.Usuario;

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
            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
            return false;
        } finally {
            if (conn != null) try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean registrarEntrenador(Entrenador e) {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            // 1. Insertar en la tabla padre (USUARIOS)
            String sqlUser = "INSERT INTO usuarios (username, password, email, nombre, apellidos, rol) VALUES (?,?,?,?,?,?)";
            PreparedStatement psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, e.getUsername());
            psUser.setString(2, e.getPassword());
            psUser.setString(3, e.getEmail());
            psUser.setString(4, e.getNombre());
            psUser.setString(5, e.getApellidos());
            psUser.setString(6, "ENTRENADOR");
            psUser.executeUpdate();

            // Obtener el ID generado para el usuario
            ResultSet rs = psUser.getGeneratedKeys();
            if (rs.next()) {
                int newId = rs.getInt(1);

                // 2. Insertar en la tabla hija (ENTRENADORES) usando ese ID
                // IMPORTANTE: Revisa que estos nombres coincidan con tu DB
                String sqlEnt = "INSERT INTO entrenadores (usuario_id, especialidad, experiencia_anios) VALUES (?,?,?)";
                PreparedStatement psEnt = conn.prepareStatement(sqlEnt);
                psEnt.setInt(1, newId);
                psEnt.setString(2, e.getEspecialidad());
                psEnt.setInt(3, e.getExperienciaAnios());
                psEnt.executeUpdate();
            }

            conn.commit(); // Guardamos los dos inserts
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            ex.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean login(String user, String pass) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        try (Connection conn = ConexionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            return ps.executeQuery().next();//esto devuelve true si el usuario y la contraseña son correctos
        } catch (SQLException e) {
            return false; //aquí devolvemos false si el login falla, el catch recoge el programa y devuelve false
        }
    }


    @Override
    public java.util.List<model.Jugador> listarJugadores() {
        java.util.List<model.Jugador> lista = new java.util.ArrayList<>();
        String sql = "SELECT u.id, u.nombre, u.apellidos, j.posicion, j.dorsal "
                + "FROM usuarios u JOIN jugadores j ON u.id = j.usuario_id";

        try (Connection conn = db.ConexionDB.getConnection(); 
            PreparedStatement ps = conn.prepareStatement(sql); 
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.Jugador j = new model.Jugador();
                j.setId(rs.getInt("id"));
                j.setNombre(rs.getString("nombre"));
                j.setApellidos(rs.getString("apellidos"));
                j.setPosicion(rs.getString("posicion"));
                j.setDorsal(rs.getInt("dorsal"));
                lista.add(j);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Usuario validar(String username, String password){
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        try(Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setRol(rs.getString("rol"));
                    return u;
                }
            }catch(SQLException e){
                e.printStackTrace();
            }return null;
    }
}
