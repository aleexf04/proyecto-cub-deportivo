package view;

import dao.*;
import dto.FichajeDTO;
import java.awt.*;
import java.util.List;
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

    //Añadimos valores al MainDashboard para poder diferenciar si el usuario es entrenador o jugador y que tenga diferentes permisos
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
            dispose();// aqui igual usamos el dispose para salir y CERRAR la pestaña
        });
        
        menuArchivo.add(itemSalir);
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);
    }

    private void initTabla() {
        // Tabla Equipos
        //Creamos un Array ya que para DefaultTableModel solo entiende esa estructura para iniciar los encabezados
        // Con esto creamos el modelo para que luego la tablaEquipos (vista) utilice el modelo
        // Si iniciaramos solo JTable no tendría un modelo, es decir solo serviría de manera estática
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
        JPanel pnlEquipos = new JPanel(new GridLayout(0, 1, 5, 5));
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
        JPanel pnlFichajes = new JPanel(new GridLayout(0, 1, 5, 5));
        pnlFichajes.setBorder(BorderFactory.createTitledBorder("Mercado de Fichajes"));

        comboJugadores = new JComboBox<>();
        comboEquipos = new JComboBox<>();
        btnFichar = new JButton("Confirmar Fichaje");
        btnEliminarFichaje = new JButton("Eliminar Fichaje");

        pnlFichajes.add(new JLabel("Jugador:"));
        pnlFichajes.add(comboJugadores);
        pnlFichajes.add(new JLabel("Equipo:"));
        pnlFichajes.add(comboEquipos);
        pnlFichajes.add(btnFichar);
        pnlFichajes.add(new JSeparator());
        pnlFichajes.add(btnEliminarFichaje);

        pnlDerecho.add(pnlEquipos);
        pnlDerecho.add(pnlFichajes);

        //Con el BorderLayout.EAST hacemos que se ajuste el panel derecho a la parte derecha de la pantalla
        //Ya que arriba hemos usado BorderLayout.CENTER para que los equipos, jugadores y fichajes se alinearan al centro
        //de la misma manera aprovecha todo el espacio de manera vertical hasta que todos los elementos sean visibles
        add(pnlDerecho, BorderLayout.EAST);

        // Eventos
        btnGuardarEq.addActionListener(e -> accionGuardarEquipo());
        btnEliminarEq.addActionListener(e -> accionEliminarEquipo());
        btnFichar.addActionListener(e -> accionFichar());
        btnEliminarFichaje.addActionListener(e -> accionEliminarFichaje());
    }

    private void actualizarTabla() {
        // Equipos
        //Lo que hace el RowCount en resumen es que lo que hay en memoria lo borra, 
        // que no se dupliquen los equipos que ya estan creados, mientras que el bucle for
        // recupera los equipos para que no se borren de la base de datos, eso hace que 
        //podamos colocar el nuevo equipo y los equipos anteriores sin riesgo de que dupliquen
        modeloTabla.setRowCount(0);
        for (Equipo eq : equipoDAO.listarTodos()) {
            modeloTabla.addRow(new Object[]{eq.getId(), eq.getNombre(), eq.getCategoria()});
        }

        // Jugadores
        //hacemos lo mismo que con la tabla de los equipos pero esta vez con los jugadores
        modeloTablaJugadores.setRowCount(0);
        for (Jugador j : usuarioDAO.listarJugadores()) {
            modeloTablaJugadores.addRow(new Object[]{
                j.getId(), j.getNombre() + " " + j.getApellidos(), j.getPosicion(), j.getDorsal()
            });
        }



        // Fichajes (Guardamos en memoria para el borrado)
        // Aquí hacemos exactamente lo mismo, solo que guardamos los fichajes 
        // ya existentes en una variable listarFichajesMemoria 
        modeloTablaFichajes.setRowCount(0);
        listaFichajesMemoria = fichajeDAO.listarFichajes();
        for (FichajeDTO f : listaFichajesMemoria) {
            modeloTablaFichajes.addRow(new Object[]{
                f.getNombreJugador(), f.getNombreEquipo(), f.getPosicion()
            });
        }
    }

    private void actualizarCombos() {
        //Aqui lo que hacemos es básicamente lo mismo, vaciamos los combos para que después 
        // no se vean duplicados, es decir, borramos los combos de memoria para luego llamar a listarJugadore()
        // sacar la información de la base de datos y voler a añadirla al comoJugadores 
        comboJugadores.removeAllItems();
        for (Jugador j : usuarioDAO.listarJugadores()) comboJugadores.addItem(j);

        //Repetimos los mismos pasos que para actualizar los comboJugadores()
        comboEquipos.removeAllItems();
        for (Equipo eq : equipoDAO.listarTodos()) comboEquipos.addItem(eq);
    }


    // En este apartado, es sencillo, añadimos los equipos, si el nombre no está vacío 
    // se ejecuta y se guarda el equipo, el único dato importante es el id que lo iniciamos a 0
    // esto se debe porque como hemos definido el id como autoincrement si ponemos cualquier numero 
    // que no sea 0 habría un conflicto con la base de datos si ese id ya está en uso, lo podríamos 
    // sustituir por null si en la declaración de variables en vez de usar int id hubieramos usado Integer id
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

    //En este caso, lo que llama la atención es el -1 en el if lo que hace este menos uno es asegurarse
    //  que el getSelectedRow ha cogido cualquier número, es decir, que el usuario ya ha seleccionado la 
    // fila del equipo que quiere eliminar, ya que por defecto si no ha seleccionado ninguna getSelectedRow 
    // por si solo devuelve -1
    private void accionEliminarEquipo() {
        int fila = tablaEquipos.getSelectedRow();
        if (fila != -1) {
            int id = (int) modeloTabla.getValueAt(fila, 0);// aqui seleccionamos el valor 0, porque 
            // en la columna 0 es donde se encuentra el id TAMBIÉN se le hace un casrting porque el getValueAt
            // devuelve un elemento de la clase Object, por eso casteamos y obligamos a que se convierta en int
            if (equipoDAO.eliminar(id)) {
                actualizarTabla();
                actualizarCombos();
            }
        }
    }

    private void accionFichar() {
        //Aqui hacemos lo mismo, le hacemos el cast para forzar a que se conviertan en objetos de su respectiva clase 
        Jugador j = (Jugador) comboJugadores.getSelectedItem();
        Equipo e = (Equipo) comboEquipos.getSelectedItem();
        if (j != null && e != null) {//Si no son nulos se hace el fichaje
            if (fichajeDAO.asignarEquipo(j.getId(), e.getId())) {
                JOptionPane.showMessageDialog(this, "Fichaje realizado correctamente");
                actualizarTabla();
            }
        }
    }


    //Esta vez usamos la misma lógica que para eliminar un equipo, usando el -1
    private void accionEliminarFichaje() {
        int fila = tablaFichajes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un fichaje de la tabla inferior");
            return;
        }

        //En esta parte, guardamos el fichaje seleccionado en (fila), para hacer una pregunta usando JPane
        FichajeDTO seleccionado = listaFichajesMemoria.get(fila);
        int resp = JOptionPane.showConfirmDialog(this, "¿Borrar fichaje?", "Confirmar", JOptionPane.YES_NO_OPTION);
        //Esto define que si la respuesta del JOptionPane es yes, se elimina el fichaje y si es no, se cierra la pestaña
        if (resp == JOptionPane.YES_OPTION) {
            if (fichajeDAO.eliminarFichaje(seleccionado.getIdJugador(), seleccionado.getIdEquipo())) {
                actualizarTabla();
                JOptionPane.showMessageDialog(this, "Fichaje eliminado");
            }
        }
    }


    //esta parte restringe todos los botones, combos y casillas de texto para los usuarios que tenga como rol JUGADOR
    //Esto hace que solo los usuarios que esten registrados como entrenadores puedan crear los equipos y hacer fichajes
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

            //le colocamos un titulo al usuario JUGADOR para que se de cuenta de que no puede usarlo y está en modo lectura
            setTitle(getTitle() + " - [MODO LECTURA]");
        }
    }
}