package com.gestioneventos.model.eventos;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cines")
public class Cine extends Evento {
    
    @Column(name = "orden_proyeccion")
    private int ordenProyeccion;
    
    @Column(name = "titulo_pelicula")
    private String tituloPelicula;
    
    // Constructor vacío requerido por JPA
    protected Cine() {
        super();
    }
    
    // Constructor con los atributos específicos
    public Cine(String nombre, LocalDate fechaInicio, int duracionEstimada,
               EstadoEvento estadoEvento, boolean permitirInscripcion,
               int ordenProyeccion, String tituloPelicula) {
        super(nombre, fechaInicio, duracionEstimada, estadoEvento, permitirInscripcion);
        setOrdenProyeccion(ordenProyeccion);
        setTituloPelicula(tituloPelicula);
    }
    
    public int getOrdenProyeccion() {
        return ordenProyeccion;
    }
    
    public void setOrdenProyeccion(int ordenProyeccion) {
        if (ordenProyeccion < 1) {
            throw new IllegalArgumentException("El orden de proyección debe ser un número positivo");
        }
        this.ordenProyeccion = ordenProyeccion;
    }
    
    public String getTituloPelicula() {
        return tituloPelicula;
    }
    
    public void setTituloPelicula(String tituloPelicula) {
        if (tituloPelicula == null || tituloPelicula.trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la película no puede ser nulo o vacío");
        }
        this.tituloPelicula = tituloPelicula;
    }
    
    @Override
    public String obtenerDescripcionEspecifica() {
        return "Proyección #" + ordenProyeccion + ": " + tituloPelicula;
    }
    
    @Override
    public void validarRequisitosEspecificos() {
        // Validamos que el evento tenga un título de película definido
        if (tituloPelicula == null || tituloPelicula.trim().isEmpty()) {
            throw new IllegalStateException("La proyección debe tener un título de película");
        }
    }
}