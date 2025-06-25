package com.gestioneventos.model.participaciones;

/**
 * Representa los posibles roles que una persona puede tener en un evento.
 */
public enum RolParticipacion {
    INSTRUCTOR("Instructor"),
    
    ORGANIZADOR("Organizador"),
    
    PARTICIPANTE("Participante"),

    ARTISTA("Artista"),
    
    CURADOR("Curador"),
    
    PRESENTADOR("Presentador");
    
    private final String descripcion;
    
    RolParticipacion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return this.descripcion;
    }
}