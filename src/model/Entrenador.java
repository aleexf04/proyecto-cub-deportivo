package model;

public class Entrenador extends Usuario {
    private String especialidad; // Ejemplo: Táctica, Porteros...
    private int experienciaAnios;

    public Entrenador() {
        super();
    }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public int getExperienciaAnios() { return experienciaAnios; }
    public void setExperienciaAnios(int experienciaAnios) { this.experienciaAnios = experienciaAnios; }
}