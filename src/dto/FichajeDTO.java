package dto;

import java.sql.Date;

public class FichajeDTO {

    private String nombreJugador;
    private String apellidosJugador;
    private String nombreEquipo;
    private String posicion;
    private Date fechaFichaje;

    // Constructor, Getters y Setters
    public FichajeDTO() {
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }
    
    public String getPosicion(){
        return posicion;
    }

    public void setPosicion(String posicion){
        this.posicion = posicion;
    }
}
