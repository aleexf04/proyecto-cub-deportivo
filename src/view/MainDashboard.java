package view;

import dao.*;
import dto.FichajeDTO;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;

public class MainDashboard extends JFrame {

    //DAOs
    private EquipoDAO equipoDAO = new EquipoDAOImpl();
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private FichajeDAO fichajeDAO = new FichajeDAOImpl();

    //Componentes de quipos
    private JTable tablaEquipos;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombreEquipo, txtCategoria;

    //COmponentes de Fichajes (Relacion N:M)
    private JComboBox<Jugador> comboJugadores;
    private JComboBox<Equipo> comboEquipos;
    private JTable tablaJugadores;
    private DefaultTableModel modeloTablaJugadores;
    private JTable tablaFichajes;
    private DefaultTableModel modeloTablaFichajes;

    public MainDashboard(){
        setTitle("Club Deportivo Ayala");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        //1. Menu superior
        initMenu();

        //2. Panel central
        initTabla();

        //3. Panel lateral
        initPanelLateral();

        //Cargar datos iniciales
        actualizarTabla();
        actualizarCombos();
    }

    private void initMenu(){
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

    private void initTabla(){
        // 1. Inicializar tabla equipos
        String[] colEquipos = {"ID", "Nombre Equipo", "Categoría"};
        modeloTabla = new DefaultTableModel(colEquipos, 0);
        tablaEquipos = new JTable(modeloTabla);

        // 2. Iniciaizar tabla jugadores
        String[] colJugadores = {"ID", "Nombre completo", "Posición", "Dorsal"};
        modeloTablaJugadores = new DefaultTableModel(colJugadores, 0);
        tablaJugadores = new JTable(modeloTablaJugadores);

        //3. Inicializar tabla fichajes
        String[] colFichajes = {"Jugador", "Equipo", "Posición"};
        modeloTablaFichajes = new DefaultTableModel(colFichajes, 0);
        tablaFichajes = new JTable(modeloTablaFichajes);

        //4. Organizar el panel
        JPanel pnlTablas = new JPanel(new GridLayout(3, 1, 10, 10));
        pnlTablas.add(new JScrollPane(tablaEquipos));
        pnlTablas.add(new JScrollPane(tablaJugadores));
        pnlTablas.add(new JScrollPane(tablaFichajes));

        // 5. Titulos a los paneles
        ((JComponent) pnlTablas.getComponent(0)).setBorder(BorderFactory.createTitledBorder("Equipos Registrados"));
        ((JComponent) pnlTablas.getComponent(1)).setBorder(BorderFactory.createTitledBorder("Jugadores Registrados"));
        ((JComponent) pnlTablas.getComponent(2)).setBorder(BorderFactory.createTitledBorder("Relaciónn Jugador - Club"));
        
        //6. Colocar en el centro
        add(pnlTablas, BorderLayout.CENTER);
    }

    private void initPanelLateral(){
        JPanel pnlDerecho = new JPanel();
        pnlDerecho.setLayout(new BoxLayout(pnlDerecho, BoxLayout.Y_AXIS));
        pnlDerecho.setPreferredSize(new Dimension(300, 0));
        pnlDerecho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Panel de gestion de los equipos
        JPanel pnlEquipos = new JPanel(new GridLayout(6, 1, 5, 5));
        pnlEquipos.setBorder(BorderFactory.createTitledBorder("Gestión de equipos"));

        txtNombreEquipo = new JTextField();
        txtCategoria = new JTextField();
        JButton  btnGuardarEq = new JButton("Aladir equipo");
        JButton btnEliminarEq = new JButton("Elimiar equipo");

        pnlEquipos.add(new JLabel("Nombre"));
        pnlEquipos.add(txtNombreEquipo);
        pnlEquipos.add(new JLabel("Categoría"));
        pnlEquipos.add(txtCategoria);
        pnlEquipos.add(btnGuardarEq);
        pnlEquipos.add(btnEliminarEq);

        // Fichajes
        JPanel pnlFichajes = new JPanel(new GridLayout(5, 1, 5, 5));
        pnlFichajes.setBorder(BorderFactory.createTitledBorder("Asignar Jugador a Equipo"));

        comboJugadores = new JComboBox<>();
        comboEquipos = new JComboBox<>();
        JButton btnFichar = new JButton("Confirmar Fichaje");

        pnlFichajes.add(new JLabel("Jugador:"));
        pnlFichajes.add(comboJugadores);
        pnlFichajes.add(new JLabel("Equipo:"));
        pnlFichajes.add(comboEquipos);
        pnlFichajes.add(btnFichar);

        //añadir subpanel 
        pnlDerecho.add(pnlEquipos);
        pnlDerecho.add(Box.createRigidArea(new Dimension(0,20)));
        pnlDerecho.add(pnlFichajes);

        add(pnlDerecho, BorderLayout.EAST);

        //Eventos
        btnGuardarEq.addActionListener(e -> accionGuardarEquipo());
        btnEliminarEq.addActionListener(e -> accionEliminarEquipo());
        btnFichar.addActionListener(e -> accionFichar());
    }

    private void actualizarTabla(){
        //actualizar equipo
        modeloTabla.setRowCount(0);
        for(Equipo eq : equipoDAO.listarTodos()){
            modeloTabla.addRow(new Object[]{eq.getId(), eq.getNombre(), eq.getCategoria()});
        }

        //Actualiza jugador
        modeloTablaJugadores.setRowCount(0);
        for(Jugador j : usuarioDAO.listarJugadores()){
            modeloTablaJugadores.addRow(new Object[]{
                j.getId(), j.getNombre() + " " + j.getApellidos(), j.getPosicion(), j.getDorsal()});
        }

        modeloTablaFichajes.setRowCount(0);
        for (FichajeDTO f : fichajeDAO.listarFichajes()) {
            modeloTablaFichajes.addRow(new Object[]{
                f.getNombreJugador(),
                f.getNombreEquipo(),
                f.getPosicion()
            });
        }
    }

    private void actualizarCombos(){
        comboJugadores.removeAllItems();
        for(Jugador j : usuarioDAO.listarJugadores()){
            comboJugadores.addItem(j);
        }

        comboEquipos.removeAllItems();
        for(Equipo eq : equipoDAO.listarTodos()){
            comboEquipos.addItem(eq);
        }
    }

    private void accionGuardarEquipo(){
        String nom = txtNombreEquipo.getText();
        String cat = txtCategoria.getText();
        if(!nom.isEmpty()){
            Equipo eq = new Equipo(0, nom, cat);
            if(equipoDAO.insertar(eq)){
                actualizarTabla();
                actualizarCombos();
                txtNombreEquipo.setText("");
                txtCategoria.setText("");
            }
        }
    }

    private void accionEliminarEquipo(){
        int fila = tablaEquipos.getSelectedRow();
        if(fila != -1){
            int id = (int) modeloTabla.getValueAt(fila, 0);
            if(equipoDAO.eliminar(id)){
                actualizarTabla();
                actualizarCombos();
            }
        }
    }

    private void accionFichar() {
        Jugador seleccionado = (Jugador) comboJugadores.getSelectedItem();
        Equipo equipoDestino = (Equipo) comboEquipos.getSelectedItem();

        if (seleccionado != null && equipoDestino != null) {
            if (fichajeDAO.asignarEquipo(seleccionado.getId(), equipoDestino.getId())) {
                JOptionPane.showMessageDialog(this, "Fichaje realizado: " + seleccionado.getNombre() + " -> " + equipoDestino.getNombre());

                actualizarTabla();

            } else {
                JOptionPane.showMessageDialog(this, "Error al realizar el fichaje");
            }
        }
    }

}