package view;

import dao.EquipoDAO;
import dao.EquipoDAOImpl;
import model.Equipo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainDashboard extends JFrame {
    private JTable tablaEquipos;
    private DefaultTableModel modeloTabla;
    private EquipoDAO equipoDAO = new EquipoDAOImpl();
    
    // Campos del formulario lateral
    private JTextField txtNombreEquipo, txtCategoria;
    private JButton btnGuardar, btnEliminar;

    public MainDashboard() {
        setTitle("Club Deportivo Ayala - Panel Principal");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. MENU SUPERIOR
        JMenuBar menuBar = new JMenuBar();
        JMenu menuSesion = new JMenu("Sesión");
        JMenuItem itemCerrar = new JMenuItem("Cerrar Sesión");
        itemCerrar.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        menuSesion.add(itemCerrar);
        menuBar.add(menuSesion);
        setJMenuBar(menuBar);

        // 2. PANEL CENTRAL (TABLA)
        String[] columnas = {"ID", "Nombre del Equipo", "Categoría"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaEquipos = new JTable(modeloTabla);
        actualizarTabla(); // Cargar datos al iniciar

        // 3. PANEL LATERAL (FORMULARIO)
        JPanel pnlLateral = new JPanel(new GridLayout(6, 1, 10, 10));
        pnlLateral.setBorder(BorderFactory.createTitledBorder("Gestión de Equipos"));
        
        txtNombreEquipo = new JTextField();
        txtCategoria = new JTextField();
        btnGuardar = new JButton("Nuevo Equipo");
        btnEliminar = new JButton("Eliminar Seleccionado");

        pnlLateral.add(new JLabel("Nombre:"));
        pnlLateral.add(txtNombreEquipo);
        pnlLateral.add(new JLabel("Categoría:"));
        pnlLateral.add(txtCategoria);
        pnlLateral.add(btnGuardar);
        pnlLateral.add(btnEliminar);

        // 4. ACCIONES
        btnGuardar.addActionListener(e -> guardarEquipo());
        btnEliminar.addActionListener(e -> eliminarEquipo());

        // ENSAMBLADO FINAL
        setLayout(new BorderLayout());
        add(new JScrollPane(tablaEquipos), BorderLayout.CENTER);
        add(pnlLateral, BorderLayout.EAST);
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        List<Equipo> lista = equipoDAO.listarTodos();
        for (Equipo eq : lista) {
            Object[] fila = {eq.getId(), eq.getNombre(), eq.getCategoria()};
            modeloTabla.addRow(fila);
        }
    }

    private void guardarEquipo() {
        if (txtNombreEquipo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
            return;
        }
        Equipo eq = new Equipo();
        eq.setNombre(txtNombreEquipo.getText());
        eq.setCategoria(txtCategoria.getText());

        if (equipoDAO.insertar(eq)) {
            JOptionPane.showMessageDialog(this, "Equipo guardado");
            txtNombreEquipo.setText("");
            txtCategoria.setText("");
            actualizarTabla();
        }
    }

    private void eliminarEquipo() {
        int fila = tablaEquipos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un equipo de la tabla");
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        int confirmar = JOptionPane.showConfirmDialog(this, "¿Borrar equipo?");
        
        if (confirmar == JOptionPane.YES_OPTION) {
            if (equipoDAO.eliminar(id)) {
                actualizarTabla();
            }
        }
    }
}