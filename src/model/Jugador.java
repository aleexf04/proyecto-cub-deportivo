package model;

public class Jugador extends Usuario {

    private String posicion; // Ejemplo: Delantero, Portero...
    private int dorsal;      // Número de la camiseta

    public Jugador() {
        super(); // Llama al constructor de Usuario
    }

    // Getters y Setters específicos de Jugador
    public String getPosicion() {
        return posicion;
    }

    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    public int getDorsal() {
        return dorsal;
    }

    public void setDorsal(int dorsal) {
        this.dorsal = dorsal;
    }

    @Override
    public String toString() {
        // Esto es lo que aparecerá en el ComboBox
        return getNombre() + " " + getApellidos() + " (" + posicion + ")";
    }
}
