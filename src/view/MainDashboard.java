package view;

import dao.*;
import dto.FichajeDTO;
import java.awt.*;
import java.util.List; // Importante: java.util, no java.awt
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;

public class MainDashboard extends JFrame {

    // DAOs
    private EquipoDAO equipoDAO = new EquipoDAOImpl();
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private FichajeDAO fichajeDAO = new FichajeDAOImpl();

    // Componentes de Equipos
    private JTable tablaEquipos;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombreEquipo, txtCategoria;

    // Componentes de Jugadores y Fichajes
    private JComboBox<Jugador> comboJugadores;
    private JComboBox<Equipo> comboEquipos;
    private JTable tablaJugadores;
    private DefaultTableModel modeloTablaJugadores;
    private JTable tablaFichajes;
    private DefaultTableModel modeloTablaFichajes;
    
    // Variables de Estado y Seguridad
    private Usuario usuarioSesion;
    private List<FichajeDTO> listaFichajesMemoria; // Para borrar con precisión

    // Botones (Declarados como variables de clase para aplicar restricciones)
    private JButton btnGuardarEq;
    private JButton btnEliminarEq;
    private JButton btnFichar;
    private JButton btnEliminarFichaje;

    public MainDashboard(Usuario user) {
        this.usuarioSesion = user;
        
        setTitle("Club Deportivo Ayala - Dashboard de Gestión");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Inicializar Componentes
        initMenu();
        initTabla();
        initPanelLateral();

        // 2. Aplicar Seguridad según Rol
        aplicarRestricciones();

        // 3. Cargar datos iniciales
        actualizarTabla();
        actualizarCombos();
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Sesión");
        JMenuItem itemSalir = new JMenuItem("Cerrar Sesión");
        
        itemSalir.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        
        menuArchivo.add(itemSalir);
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);
    }

    private void initTabla() {
        // Tabla Equipos
        String[] colEquipos = {"ID", "Nombre Equipo", "Categoría"};
        modeloTabla = new DefaultTableModel(colEquipos, 0);
        tablaEquipos = new JTable(modeloTabla);

        // Tabla Jugadores
        String[] colJugadores = {"ID", "Nombre completo", "Posición", "Dorsal"};
        modeloTablaJugadores = new DefaultTableModel(colJugadores, 0);
        tablaJugadores = new JTable(modeloTablaJugadores);

        // Tabla Fichajes (N:M)
        String[] colFichajes = {"Jugador", "Equipo", "Posición"};
        modeloTablaFichajes = new DefaultTableModel(colFichajes, 0);
        tablaFichajes = new JTable(modeloTablaFichajes);

        // Panel Central con las 3 tablas
        JPanel pnlTablas = new JPanel(new GridLayout(3, 1, 10, 10));
        pnlTablas.add(new JScrollPane(tablaEquipos));
        pnlTablas.add(new JScrollPane(tablaJugadores));
        pnlTablas.add(new JScrollPane(tablaFichajes));

        // Bordes con título
        ((JComponent) pnlTablas.getComponent(0)).setBorder(BorderFactory.createTitledBorder("Equipos Registrados"));
        ((JComponent) pnlTablas.getComponent(1)).setBorder(BorderFactory.createTitledBorder("Jugadores Registrados"));
        ((JComponent) pnlTablas.getComponent(2)).setBorder(BorderFactory.createTitledBorder("Relación Jugador - Club (Fichajes)"));

        add(pnlTablas, BorderLayout.CENTER);
    }

    private void initPanelLateral() {
        JPanel pnlDerecho = new JPanel();
        pnlDerecho.setLayout(new BoxLayout(pnlDerecho, BoxLayout.Y_AXIS));
        pnlDerecho.setPreferredSize(new Dimension(300, 0));
        pnlDerecho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- MÓDULO EQUIPOS ---
        JPanel pnlEquipos = new JPanel(new GridLayout(6, 1, 5, 5));
        pnlEquipos.setBorder(BorderFactory.createTitledBorder("Gestión de Equipos"));

        txtNombreEquipo = new JTextField();
        txtCategoria = new JTextField();
        btnGuardarEq = new JButton("Añadir Equipo");
        btnEliminarEq = new JButton("Eliminar Equipo");

        pnlEquipos.add(new JLabel("Nombre:"));
        pnlEquipos.add(txtNombreEquipo);
        pnlEquipos.add(new JLabel("Categoría:"));
        pnlEquipos.add(txtCategoria);
        pnlEquipos.add(btnGuardarEq);
        pnlEquipos.add(btnEliminarEq);

        // --- MÓDULO FICHAJES ---
        JPanel pnlFichajes = new JPanel(new GridLayout(7, 1, 5, 5));
        pnlFichajes.setBorder(BorderFactory.createTitledBorder("Mercado de Fichajes"));

        comboJugadores = new JComboBox<>();
        comboEquipos = new JComboBox<>();
        btnFichar = new JButton("Confirmar Fichaje");
        btnEliminarFichaje = new JButton("Eliminar Fichaje");
        btnEliminarFichaje.setBackground(new Color(255, 200, 200));

        pnlFichajes.add(new JLabel("Jugador:"));
        pnlFichajes.add(comboJugadores);
        pnlFichajes.add(new JLabel("Equipo:"));
        pnlFichajes.add(comboEquipos);
        pnlFichajes.add(btnFichar);
        pnlFichajes.add(new JSeparator());
        pnlFichajes.add(btnEliminarFichaje);

        pnlDerecho.add(pnlEquipos);
        pnlDerecho.add(Box.createRigidArea(new Dimension(0, 20)));
        pnlDerecho.add(pnlFichajes);

        add(pnlDerecho, BorderLayout.EAST);

        // Eventos
        btnGuardarEq.addActionListener(e -> accionGuardarEquipo());
        btnEliminarEq.addActionListener(e -> accionEliminarEquipo());
        btnFichar.addActionListener(e -> accionFichar());
        btnEliminarFichaje.addActionListener(e -> accionEliminarFichaje());
    }

    private void actualizarTabla() {
        // Equipos
        modeloTabla.setRowCount(0);
        for (Equipo eq : equipoDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{eq.getId(), eq.getNombre(), eq.getCategoria()});
        }

        // Jugadores
        modeloTablaJugadores.setRowCount(0);
        for (Jugador j : usuarioDAO.listarJugadores()) {
            modeloTablaJugadores.addRow(new Object[]{
                j.getId(), j.getNombre() + " " + j.getApellidos(), j.getPosicion(), j.getDorsal()
            });
        }

        // Fichajes (Guardamos en memoria para el borrado)
        modeloTablaFichajes.setRowCount(0);
        listaFichajesMemoria = fichajeDAO.listarFichajes();
        for (FichajeDTO f : listaFichajesMemoria) {
            modeloTablaFichajes.addRow(new Object[]{
                f.getNombreJugador(), f.getNombreEquipo(), f.getPosicion()
            });
        }
    }

    private void actualizarCombos() {
        comboJugadores.removeAllItems();
        for (Jugador j : usuarioDAO.listarJugadores()) comboJugadores.addItem(j);

        comboEquipos.removeAllItems();
        for (Equipo eq : equipoDAO.listarTodos()) comboEquipos.addItem(eq);
    }

    private void accionGuardarEquipo() {
        String nom = txtNombreEquipo.getText();
        String cat = txtCategoria.getText();
        if (!nom.isEmpty()) {
            if (equipoDAO.insertar(new Equipo(0, nom, cat))) {
                actualizarTabla();
                actualizarCombos();
                txtNombreEquipo.setText("");
                txtCategoria.setText("");
            }
        }
    }

    private void accionEliminarEquipo() {
        int fila = tablaEquipos.getSelectedRow();
        if (fila != -1) {
            int id = (int) modeloTabla.getValueAt(fila, 0);
            if (equipoDAO.eliminar(id)) {
                actualizarTabla();
                actualizarCombos();
            }
        }
    }

    private void accionFichar() {
        Jugador j = (Jugador) comboJugadores.getSelectedItem();
        Equipo e = (Equipo) comboEquipos.getSelectedItem();
        if (j != null && e != null) {
            if (fichajeDAO.asignarEquipo(j.getId(), e.getId())) {
                JOptionPane.showMessageDialog(this, "Fichaje realizado correctamente");
                actualizarTabla();
            }
        }
    }

    private void accionEliminarFichaje() {
        int fila = tablaFichajes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un fichaje de la tabla inferior");
            return;
        }

        FichajeDTO seleccionado = listaFichajesMemoria.get(fila);
        int resp = JOptionPane.showConfirmDialog(this, "¿Borrar fichaje?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (resp == JOptionPane.YES_OPTION) {
            if (fichajeDAO.eliminarFichaje(seleccionado.getIdJugador(), seleccionado.getIdEquipo())) {
                actualizarTabla();
                JOptionPane.showMessageDialog(this, "Fichaje eliminado");
            }
        }
    }

    private void aplicarRestricciones() {
        if ("JUGADOR".equalsIgnoreCase(usuarioSesion.getRol())) {
            // Deshabilitar botones de edición
            btnGuardarEq.setEnabled(false);
            btnEliminarEq.setEnabled(false);
            btnFichar.setEnabled(false);
            btnEliminarFichaje.setEnabled(false);
            
            // Deshabilitar campos de texto
            txtNombreEquipo.setEnabled(false);
            txtCategoria.setEnabled(false);
            
            setTitle(getTitle() + " - [MODO LECTURA]");
        }
    }
}