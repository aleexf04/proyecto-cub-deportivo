package view;

import dao.*;
import dto.FichajeDTO;
import model.Equipo;
import model.Jugador;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class MainDashboard extends JFrame {

    //DAOs
    private EquipoDAO equipoDAO = new EquipoDAOImpl();
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private FichajeDAO fichajeDAO = new FichajeDAOImp();

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
        JMenu menuArchivo = new JMenuBar("Sesión");
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
        
    }

}