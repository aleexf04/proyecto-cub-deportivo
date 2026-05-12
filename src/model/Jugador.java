package model;

public class Jugador extends Usuario {

    private String posicion;
    private int dorsal;

    public Jugador() {
        super();
    }

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
        return getNombre() + " " + getApellidos() + " (" + posicion + ")";
    }
}
